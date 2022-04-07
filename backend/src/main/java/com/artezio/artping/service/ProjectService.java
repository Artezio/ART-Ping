package com.artezio.artping.service;

import com.artezio.artping.controller.PageData;
import com.artezio.artping.controller.PageHelper;
import com.artezio.artping.controller.request.SearchStringFilter;
import com.artezio.artping.controller.request.SortDto;
import com.artezio.artping.data.exceptions.NoProjectsFoundException;
import com.artezio.artping.data.exceptions.ProjectWithThisNameAlreadyExist;
import com.artezio.artping.data.repository.ProjectEmployeeRepository;
import com.artezio.artping.data.repository.ProjectRepository;
import com.artezio.artping.dto.CreateProjectDto;
import com.artezio.artping.dto.ProjectDto;
import com.artezio.artping.dto.RoleEnum;
import com.artezio.artping.dto.UpdateProjectDto;
import com.artezio.artping.entity.AbstractEntity;
import com.artezio.artping.entity.office.Office;
import com.artezio.artping.entity.project.AbstractProjectEmployee;
import com.artezio.artping.entity.project.Project;
import com.artezio.artping.entity.project.ProjectEmployee;
import com.artezio.artping.entity.project.ProjectPM;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.entity.user.Role;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы с проектами
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final MapperFacade mapper;
    @Autowired(required = false)
    private EmployeeService employeeService;
    @Autowired
    private RoleService roleService;
    private final PageHelper pageHelper;
    private final CurrentUserService currentUserService;
    private final ProjectEmployeeRepository projectEmployeeRepository;

    /**
     * Создание проекта
     *
     * @param project информация о создаваемом проекте
     * @return информация о созданном проекте с его идентификатором
     */
    @Transactional
    public ProjectDto createProject(CreateProjectDto project) {
        if (repository.findByNameIgnoreCase(project.getName()).isPresent()) {
            throw new ProjectWithThisNameAlreadyExist();
        }
        Project projectToSave = new Project();
        projectToSave.setName(project.getName());
        projectToSave.setActive(true);
        projectToSave.setProjectManagers(new ArrayList<>());
        projectToSave.setProjectEmployees(new ArrayList<>());
        createWorkersForProject(projectToSave, project.getPmIds(), project.getEmployeeIds());
        return mapper.map(repository.save(projectToSave), ProjectDto.class);
    }

    /**
     * Изменение информации о проекте
     *
     * @param project информация об изменяемом проекте
     * @return информация об изменённом проекте
     */
    @Transactional
    public ProjectDto updateProject(UpdateProjectDto project) {
        Project projectToUpdate = repository.getOne(UUID.fromString(project.getId()));
        if (isExistProjectWithName(projectToUpdate, project.getName())) {
            throw new ProjectWithThisNameAlreadyExist();
        }
        projectToUpdate.setName(project.getName());
        projectToUpdate.setActive(project.getActive());
        List<ProjectEmployee> projectEmployees = new ArrayList<>(projectToUpdate.getProjectEmployees());
        projectToUpdate.getProjectEmployees().clear();
        projectToUpdate.getProjectManagers().clear();
        projectEmployeeRepository.deleteByProject(projectToUpdate);
        // Изменение полей для сортировки по проектам у сотрудника без учёта текущего проекта
        projectEmployees.forEach(projectEmployee -> {
            Employee employee = projectEmployee.getEmployee();
            List<String> employeeProjectNames = employee.getProjectEmployees().stream()
                    .map(employeeProject -> employeeProject.getProject().getName())
                    .filter(name -> !name.equals(projectToUpdate.getName()))
                    .collect(Collectors.toList());
            EmployeeService.setProjectNameFieldsForEmployeeSorting(employee, employeeProjectNames);
        });
        createWorkersForProject(projectToUpdate, project.getPmIds(), project.getEmployeeIds());
        return mapper.map(repository.save(projectToUpdate), ProjectDto.class);
    }

    private void createWorkersForProject(Project project, List<UUID> projectPMIds, List<UUID> employeeIds) {
        List<ProjectPM> projectPMList = project.getProjectManagers();
        if (projectPMIds != null) {
            for (UUID id : projectPMIds) {
                Employee pm = employeeService.findEmployeeById(id);
                Role pmRole = pm.getRoles().stream()
                        .filter(role -> role.getCode().equals(RoleEnum.PM.getCode()))
                        .findFirst().orElse(null);
                if (pmRole == null) {
                    pm.getRoles().add(roleService.findByCode(RoleEnum.PM.getCode()));
                    EmployeeService.setRoleFieldsForEmployeeSorting(pm);
                }

                ProjectPM projectPM = new ProjectPM(pm, project);
                projectPMList.add(projectPM);
            }
            project.setProjectManagers(projectPMList);
        }

        List<ProjectEmployee> projectEmployees = project.getProjectEmployees();
        if (employeeIds != null) {
            for (UUID id : employeeIds) {
                Employee employee = employeeService.findEmployeeById(id);
                projectEmployees.add(new ProjectEmployee(employee, project));
                Set<String> employeeProjectNames = employee.getProjectEmployees().stream()
                        .map(projectEmployee -> projectEmployee.getProject().getName())
                        .collect(Collectors.toSet());
                employeeProjectNames.add(project.getName());
                EmployeeService.setProjectNameFieldsForEmployeeSorting(employee, employeeProjectNames);
            }
            project.setProjectEmployees(projectEmployees);
        }
    }

    /**
     * Удаление проекта
     *
     * @param projectId идентификатор проекта
     */
    public void deleteProject(UUID projectId) {
        repository.deleteById(projectId);
    }

    /**
     * Получение всех проектов
     *
     * @return список всех проектов
     */
    @Transactional
    public List<ProjectDto> findAll() {
        return mapper.mapAsList(repository.findAll(), ProjectDto.class);
    }

    /**
     * Получение всех активных проектов
     *
     * @return страница с результатами поиска
     */
    @Transactional
    public PageData<ProjectDto> getByFilter(SearchStringFilter filter) {
        SortDto[] sorting = filter.getSorting();
        Pageable pageable;
        if (sorting == null) {
            pageable = pageHelper.processFilter(filter, SortDto.of("name"));
        } else {
            pageable = pageHelper.processFilter(filter, sorting);
        }

        Page<Project> page;
        List<RoleEnum> roles = currentUserService.getCurrentUser().getRoles();
        if (!roles.contains(RoleEnum.ADMIN)) {
            Employee currentEmployee = employeeService.getCurrentEmployee();
            Set<UUID> projectIds = new HashSet<>();
            if (roles.contains(RoleEnum.PM)) {
                projectIds.addAll(currentEmployee.getProjectPMs().stream()
                        .map(projectPM -> projectPM.getProject().getId())
                        .collect(Collectors.toSet()));
            }
            page = repository.findByActiveTrueAndNameContainingIgnoreCaseAndIdIn(
                    filter.getSearchString(), projectIds, pageable);
        } else {
            page = repository.findByActiveTrueAndNameContainingIgnoreCase(filter.getSearchString(), pageable);
        }
        List<ProjectDto> result = mapper.mapAsList(page.getContent(), ProjectDto.class);
        return new PageData<>(result, page.getTotalElements());
    }

    /**
     * Получение справочника доступных проектов текущему сотруднику
     *
     * @return справочник доступных проектов
     */
    public List<ProjectDto> getAllAvailableProjects() {
        List<Project> availableProjects = new ArrayList<>();
        List<RoleEnum> roles = currentUserService.getCurrentUser().getRoles();
        if (roles.contains(RoleEnum.ADMIN) || roles.contains(RoleEnum.HR)) {
            availableProjects = repository.findAllByActive(true);
        } else {
            Employee currentEmployee = employeeService.getCurrentEmployee();
            if (roles.contains(RoleEnum.PM)) {
                availableProjects.addAll(currentEmployee.getProjectPMs().stream()
                        .map(ProjectPM::getProject)
                        .collect(Collectors.toList()));
            }
        }
        return mapper.mapAsList(availableProjects, ProjectDto.class);
    }

    /**
     * Получение информации о проекте по его идентификатору
     *
     * @param id идентификатор проекта
     * @return информация о проекте
     */
    @Transactional
    public ProjectDto findById(String id) {
        return mapper.map(findEntityById(UUID.fromString(id)), ProjectDto.class);
    }

    /**
     * Получение сущности проекта по его идентификатору
     *
     * @param id идентификатор проекта
     * @return сущность проекта
     */
    @Transactional
    public Project findEntityById(UUID id) {
        return repository.findById(id).orElseThrow(NoProjectsFoundException::new);
    }

    /**
     * Получение всех проектов сотрудников в переданном офисе
     *
     * @param officeId идентификатор офиса
     * @return список проектов сотрудников в переданном офисе
     */
    public List<ProjectDto> getProjectsByOfficeId(UUID officeId) {
        List<Employee> employees = employeeService.findAllByOfficeId(officeId);
        List<UUID> employeeIds = employees.stream().map(AbstractEntity::getId)
                .collect(Collectors.toList());
        List<Project> projectsByActiveTrueAndEmployeeIdsIn = repository
                .findProjectsByActiveTrueAndEmployeeIdsIn(employeeIds);
        return mapper.mapAsList(projectsByActiveTrueAndEmployeeIdsIn, ProjectDto.class);
    }

    private boolean isExistProjectWithName(Project projectToUpdate, String projectName) {
        return !projectToUpdate.getName().equals(projectName) && repository.findByNameIgnoreCase(projectName).isPresent();
    }
}
