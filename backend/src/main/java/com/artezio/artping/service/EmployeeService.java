package com.artezio.artping.service;

import com.artezio.artping.controller.PageData;
import com.artezio.artping.controller.PageHelper;
import com.artezio.artping.controller.request.*;
import com.artezio.artping.data.exceptions.*;
import com.artezio.artping.data.repository.*;
import com.artezio.artping.data.specification.EmployeeSpecificationCreator;
import com.artezio.artping.dto.EmployeeTestDto;
import com.artezio.artping.dto.RoleEnum;
import com.artezio.artping.dto.calendar.request.CalendarRequest;
import com.artezio.artping.dto.calendar.request.UpdateCalendarRequest;
import com.artezio.artping.dto.calendar.response.CalendarResponse;
import com.artezio.artping.dto.calendar.response.EventResponse;
import com.artezio.artping.dto.employee.request.UpdateEmployeeRequest;
import com.artezio.artping.dto.employee.response.EmployeeAndUserDto;
import com.artezio.artping.dto.employee.response.EmployeeDetailedResponse;
import com.artezio.artping.dto.employee.response.EmployeeResponse;
import com.artezio.artping.dto.user.request.UserCreationRequest;
import com.artezio.artping.entity.EmployeeSubscription;
import com.artezio.artping.entity.EmployeeTest;
import com.artezio.artping.entity.TestStatusEnum;
import com.artezio.artping.entity.calendar.Calendar;
import com.artezio.artping.entity.calendar.Event;
import com.artezio.artping.entity.office.Office;
import com.artezio.artping.entity.project.Project;
import com.artezio.artping.entity.project.ProjectEmployee;
import com.artezio.artping.entity.project.ProjectPM;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.entity.user.Role;
import com.artezio.artping.entity.user.UserEntity;
import com.artezio.artping.service.utils.UserAgentUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

/**
 * Сервис для работы с сотрудниками
 */
@Slf4j
@Service
@NoArgsConstructor
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;
    @Autowired
    private CalendarRepository calendarRepository;
    @Autowired
    private OfficeRepository officeRepository;
    @Autowired(required = false)
    private OfficeService officeService;
    @Autowired
    private EmployeeSubscriptionRepository subscriptionRepository;
    @Autowired
    private EmployeeTestRepository employeeTestRepository;
    @Autowired
    private MapperFacade mapper;
    @Autowired(required = false)
    private CalendarService calendarService;
    @Autowired(required = false)
    private UserService userService;
    @Autowired(required = false)
    private ProjectService projectService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PageHelper pageHelper;
    @Autowired
    private CurrentUserService currentUserService;

    /**
     * Создание сотрудника
     *
     * @param request    информация о сотруднике
     * @param userEntity сущность пользователя, которая привязывается к сортуднику
     * @return созданная сущность сотрудника
     */
    @Transactional
    public Employee createEmployee(UserCreationRequest request, UserEntity userEntity) {
        log.info("Starting employee creation...");
        Office office = officeService.findEntityById(request.getBaseOffice());
        Employee employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setLogin(request.getLogin());
        String email = request.getEmail();
        if (repository.findOptionalByEmailIgnoreCase(email).isPresent()) {
            throw new EmployeeWithThisEmailAlreadyExist();
        }
        employee.setEmail(email);
        employee.setUser(userEntity);
        employee.setBaseOffice(office);

        Set<Role> roles = new HashSet<>();
        List<RoleEnum> currentEmployeeRoles = currentUserService.getCurrentUser().getRoles();
        for (RoleEnum role : request.getRoles()) {
            if (!currentEmployeeRoles.contains(RoleEnum.ADMIN) && role == RoleEnum.ADMIN) {
                continue;
            }
            roles.add(roleService.findByCode(role.getCode()));
        }
        roles.add(roleService.findByCode(RoleEnum.USER.getCode()));
        employee.setRoles(roles);
        setRoleFieldsForEmployeeSorting(employee);

        List<ProjectEmployee> projectEmployees = new ArrayList<>();
        for (UUID projectId : request.getProjects()) {
            projectEmployees.add(new ProjectEmployee(employee, projectService.findEntityById(projectId)));
        }
        employee.setProjectEmployees(projectEmployees);
        List<String> projectNames = projectEmployees.stream()
                .map(projectEmployee -> projectEmployee.getProject().getName())
                .collect(Collectors.toList());
        setProjectNameFieldsForEmployeeSorting(employee, projectNames);

        List<ProjectPM> projectPMs = new ArrayList<>();
        for (UUID projectPMId : request.getManagedProjects()) {
            projectPMs.add(new ProjectPM(employee, projectService.findEntityById(projectPMId)));
        }
        employee.setProjectPMs(projectPMs);

        List<Office> managedOffices = employee.getManagedOffices();
        if (request.getRoles().contains(RoleEnum.DIRECTOR)) {
            for (UUID managedOfficeId : request.getManagedOffices()) {
                managedOffices.add(officeService.findEntityById(managedOfficeId.toString()));
            }
        }

        List<Office> hrOffices = employee.getOffices();
        if (request.getRoles().contains(RoleEnum.HR)) {
            for (UUID officeId : request.getOffices()) {
                hrOffices.add(officeService.findEntityById(officeId.toString()));
            }
        }

        employee = repository.save(employee);

        Calendar calendar = office.getCalendar();
        if (isTrue(request.getIsCustomCalendar())) {
            Set<Event> officeCalendarEvents = new HashSet<>(calendar.getEvents());
            CalendarRequest calendarRequest = mapper.map(request, CalendarRequest.class);
            calendarRequest.setName(employee.getId().toString());
            calendar = calendarService.createCalendarEntity(calendarRequest);
            calendar.setCustomCalendar(true);
            calendar.getEvents().addAll(officeCalendarEvents);
            calendar = calendarService.saveFromEntity(calendar);
        }
        employee.setCalendar(calendar);

        if (employee.getId() != null) {
            log.info("Employee created with id = " + employee.getId());
        }
        return employee;
    }

    /**
     * Получение всех логинов сотрудников
     *
     * @return список логинов сотрудников
     */
    @Transactional
    public List<String> getAllEmployeesLogin() {
        return repository.getAllEmployeesLogin();
    }

    @Transactional
    public List<String> getAllEmployeeEmails() {
        return repository.getAllEmployeeEmails();
    }

    /**
     * Получение списка с информацией о всех активных сотрудниках
     *
     * @return список активных сотрудников
     */
    @Transactional
    public List<EmployeeResponse> findAll() {
        return mapper.mapAsList(findAllActualEmployeeEntities(), EmployeeResponse.class);
    }

    /**
     * Получение всех сущностей активных сотрудников
     *
     * @return список всех сотрудников
     */
    @Transactional
    public Collection<Employee> findAllActualEmployeeEntities() {
        return repository.findAllActual();
    }

    /**
     * Получение информации о сотруднике по идентификатору
     *
     * @param id идентификатор сотрудника
     * @return инфомрация о сотруднике
     */
    @Transactional(readOnly = true)
    public EmployeeDetailedResponse findById(UUID id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundByIdException(id.toString()));
        EmployeeDetailedResponse response = mapper.map(employee, EmployeeDetailedResponse.class);
        response.setProjects(sortEmployeeProjectsIdsByProjectName(employee.getProjectEmployees()));
        calendarRepository.findByNameIgnoreCase(employee.getId().toString())
                .ifPresent(calendar -> response.setCustomCalendarId(calendar.getId().toString()));
        officeRepository.findById((employee.getBaseOffice().getId()))
                .ifPresent(office -> response.setOfficeCalendarId(office.getCalendarId().toString()));
        CalendarResponse calendarResponse = mapper.map(employee.getCalendar(), CalendarResponse.class);
        calendarResponse.setEvents(mapper.mapAsSet(employee.getCalendar().getEvents(), EventResponse.class));
        setCalendarInfoToResponse(response, calendarResponse);
        return response;
    }

    /**
     * Получение сущности сотрудника по идентификатору
     *
     * @param id идентификатор сотрудника
     * @return сущность сотрудника
     */
    @Transactional
    public Employee findEmployeeById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundByIdException(id.toString()));
    }

    /**
     * Получение списка сущностей пользователей по офису
     *
     * @param officeId идентификатор офиса
     * @return список сущностей сотрудников
     */
    public List<Employee> findAllByOfficeId(UUID officeId) {
        return repository.findAllByBaseOfficeId(officeId);
    }

    /**
     * Получение сотрудников из БД по фильтру
     * <p/>
     *
     * @return список сотрудников
     * @throws SecurityException
     */
    @Transactional
    public PageData<EmployeeAndUserDto> getEmployeesPage(EmployeesPageFilter filter) {
        Page<Employee> page = getEmployees(filter);
        return new PageData<>(mapper.mapAsList(page.getContent(), EmployeeAndUserDto.class), page.getTotalElements());
    }

    /**
     * Получение постраничного списка сущностей сотрудников по фильтру
     *
     * @param filter период, пагинация, фильтрация и сортировка
     * @return постраничный список сущностей сотрудников
     */
    @Transactional
    public PageData<Employee> getEmployeeEntitiesPage(EmployeesTestsPageFilter filter) {
        Page<Employee> page = getEmployees(filter);
        return new PageData<>(page.getContent(), page.getTotalElements());
    }

    /**
     * Постраничное получение сотрудников по фильтру без учёта ролей текущего сотрудника
     *
     * @param filter пагинация, фильтрация и сортировка
     * @return постраничный список сотрудников
     */
    @Transactional
    public PageData<EmployeeAndUserDto> getEmployeesPageFromAllOffices(EmployeesPageFilter filter) {
        EmployeeSpecificationCreator employeeSpecification = mapper.map(filter, EmployeeSpecificationCreator.class);
        Pageable pageable = createSortingPage(filter.getSorting(), filter);
        Page<Employee> page = repository.findAll(employeeSpecification.create(), pageable);
        return new PageData<>(mapper.mapAsList(page.getContent(), EmployeeAndUserDto.class), page.getTotalElements());
    }

    private Page<Employee> getEmployees(EmployeesPageFilter filter) {
        EmployeeSpecificationCreator employeeSpecification = mapper.map(filter, EmployeeSpecificationCreator.class);
        Pageable pageable = createSortingPage(filter.getSorting(), filter);

        Employee currentEmployee = getCurrentEmployee();
        List<RoleEnum> roles = currentUserService.getCurrentUser().getRoles();
        if (!roles.contains(RoleEnum.ADMIN)) {
            Set<Project> projects = currentEmployee.getProjectPMs().stream()
                    .map(ProjectPM::getProject)
                    .collect(Collectors.toSet());
            Set<Office> offices = new HashSet<>(currentEmployee.getOffices());
            offices.addAll(currentEmployee.getManagedOffices());
            UUID officeId = filter.getOfficeId();
            UUID projectId = filter.getProjectId();
            if (roles.contains(RoleEnum.HR) || roles.contains(RoleEnum.DIRECTOR)) {
                employeeSpecification.setOfficeIds(selectAvailableIdsByFilter(offices.stream()
                        .map(Office::getId)
                        .collect(Collectors.toList()), officeId));
            } else {
                officeId = null;
                employeeSpecification.setOfficeIds(null);
            }
            if (roles.contains(RoleEnum.PM)) {
                employeeSpecification.setProjectIds(selectAvailableIdsByFilter(projects.stream()
                        .map(Project::getId)
                        .collect(Collectors.toList()), projectId));
            } else {
                projectId = null;
                employeeSpecification.setProjectIds(null);
            }

            if (officeId == null && projectId == null) {
                employeeSpecification.setConcatenateEmployeeByOfficesAndProjects(true);
            } else {
                if (projectId == null) {
                    employeeSpecification.setProjectIds(null);
                }
                if (officeId == null) {
                    employeeSpecification.setOfficeIds(null);
                }
            }
        }
        return repository.findAll(employeeSpecification.create(), pageable);
    }

    private Pageable createSortingPage(SortDto[] sorting, PageFilterRequest filter) {
        Pageable pageable;
        if (sorting == null) {
            pageable = pageHelper.processFilter(filter);
        } else {
            for (SortDto sortDto : sorting) {
                if (sortDto.getField().equals("projects")) {
                    sortDto.setField(sortDto.isAsc() ? "ascProjectName" : "descProjectName");
                }
                if (sortDto.getField().equals("roles")) {
                    sortDto.setField(sortDto.isAsc() ? "ascRoleName" : "descRoleName");
                }
            }
            pageable = pageHelper.processFilter(filter, sorting);
        }
        return pageable;
    }

    /**
     * Отмена запланированных проверок сотруднику начиная с указанного момента времени
     *
     * @param employeeId идентификатор сотрудника
     * @param time       время от которого будут отменяться проверки
     */
    @Transactional
    public void cancelEmployeeTestsAfterTime(UUID employeeId, ZonedDateTime time) {
        List<EmployeeTest> tests = employeeTestRepository
                .findAllByStatusAndStartTimeAfterAndEmployeeId(TestStatusEnum.PLANNED, time, employeeId);
        tests.addAll(employeeTestRepository.findAllByStatusAndStartTimeAfterAndEmployeeId(TestStatusEnum.IN_PROGRESS,
                time, employeeId));
        tests.forEach(test -> test.setStatus(TestStatusEnum.CANCELED));
    }

    /**
     * Добавление подписки firebase сотруднику за исключением супер пользователя
     *
     * @param subscriptionAddRequestDto идентификатор подписки firebase
     * @param userAgent                 информация о клиенте
     * @throws SecurityException если проблемы с авторизацией
     */
    @Transactional
    public void addSubscription(SubscriptionAddRequestDto subscriptionAddRequestDto, String userAgent)
            throws SecurityException {
        log.info("Start addSubscription method..");
        Employee employee = getCurrentEmployee();
        // Заглушка для супер пользователя
        if (employee.getLogin().equals("admin")) {
            throw new ArtPingException("You can't create subscription for super user!");
        }

        List<EmployeeSubscription> validSubscriptions = subscriptionRepository
                .findByIdAndSubscribeIdAndValid(employee.getId(),
                        subscriptionAddRequestDto.getSubscribeId(), true);
        if (!validSubscriptions.isEmpty()) {
            // Подписка уже существует
            log.info("The subscription for the user with login {} already existed.", employee.getLogin());
            return;
        }

        subscriptionRepository.stopSubscription(subscriptionAddRequestDto.getSubscribeId());

        EmployeeSubscription newEmployeeSubscription = new EmployeeSubscription();
        newEmployeeSubscription.setEmployee(employee);
        newEmployeeSubscription.setSubscribeId(subscriptionAddRequestDto.getSubscribeId());
        newEmployeeSubscription.setUserAgent(userAgent);
        newEmployeeSubscription.setType(UserAgentUtils.detectDeviceType(userAgent));
        newEmployeeSubscription.setValid(true);

        employee.getSubscriptions().add(newEmployeeSubscription);

        repository.save(employee);
        log.info("Method addSubscription method finished successfully.");
    }

    /**
     * Отметка подписки сотрудника как не валидная
     *
     * @param subscriptionDeleteRequestDto идентификатор подписки firebase
     */
    @Transactional
    public void deleteSubscription(SubscriptionDeleteRequestDto subscriptionDeleteRequestDto) {
        List<EmployeeSubscription> validSubscriptions = subscriptionRepository
                .findAllBySubscribeIdAndValid(subscriptionDeleteRequestDto.getSubscribeId(), Boolean.TRUE);
        if (validSubscriptions.isEmpty()) {
            // Подписка уже отмечена как не валидная
            log.info("The subscription {} already mark as invalid.",
                    subscriptionDeleteRequestDto.getSubscribeId());
            return;
        }

        validSubscriptions.forEach(subscription -> subscription.setValid(Boolean.FALSE));
        subscriptionRepository.saveAll(validSubscriptions);
    }

    /**
     * Получение текущего авторизованного сотрудника из БД. Под супер пользователем возвращается заглушка.
     *
     * @return текущий пользователь
     * @throws SecurityException если проблемы с авторизацией
     */
    public Employee getCurrentEmployee() throws SecurityException {
        try {
            UserEntity user = userService.getCurrentUserEntity();
            Employee employee = user.getEmployee();
            EmployeeAndUserDto superUser = userService.getUserByLogin("admin");

            if (superUser.getId().equals(user.getId().toString()) // Заглушка для супер пользователя
                    && employee == null) {
                employee = new Employee();
                employee.setFirstName("Super");
                employee.setLastName("User");
                employee.setLogin("admin");
                employee.getRoles().add(roleService.findByCode(RoleEnum.ADMIN.getCode()));
            }
            return employee;
        } catch (Exception e) {
            throw new SecurityException(e.getMessage());
        }
    }

    /**
     * Получение списка тестов сотрудника на определенный период с учётом таймзоны
     *
     * @param id        идентификатор сотрудника
     * @param startDate дата начала периода
     * @param endDate   дата конца периода
     * @return список тестов сотрудника
     */
    @Transactional
    public List<EmployeeTestDto> getEmployeeTestByEmployeeId(UUID id, LocalDate startDate, LocalDate endDate) {
        ZoneId tz;
        Employee emp = getCurrentEmployee();
        if (emp == null || emp.getBaseOffice() == null) {
            tz = ZoneId.of("UTC");
        } else {
            tz = ZoneId.of(emp.getBaseOffice().getTimezone());
        }

        // добавляем один день, чтобы в результат вошли данные по проверкам, назначенным на последний день
        endDate = endDate.plusDays(1);
        return mapper.mapAsList(employeeTestRepository.findForPeriod(id, startDate.atStartOfDay(tz), endDate.atStartOfDay(tz)), EmployeeTestDto.class);
    }

    /**
     * Изменение информации о сотруднике
     *
     * @param id              идентификатор сотрудника
     * @param employeeRequest новая информация о сотруднике
     * @return изменённая информация о сортуднике
     */
    @Transactional
    public EmployeeAndUserDto update(String id, UpdateEmployeeRequest employeeRequest) {
        Employee employee = repository.findById(UUID.fromString(id)).orElseThrow(NoEmployeesFoundException::new);
        String login = employeeRequest.getLogin();
        if (!employee.getLogin().equals(login) &&
                userService.getOptionalUserByLogin(employeeRequest.getLogin()).isPresent()) {
            throw new UserWithThisLoginAlreadyExist();
        }

        String email = employeeRequest.getEmail();
        if (email != null) {
            if (!employee.getEmail().equals(email) && repository.findOptionalByEmailIgnoreCase(email).isPresent()) {
                throw new EmployeeWithThisEmailAlreadyExist();
            }
            employee.setEmail(email);
        }
        String firstName = employeeRequest.getFirstName();
        if (firstName != null) {
            employee.setFirstName(firstName);
        }
        String lastName = employeeRequest.getLastName();
        if (lastName != null) {
            employee.setLastName(lastName);
        }
        if (login != null) {
            employee.setLogin(login);
            employee.getUser().setLogin(login);
        }

        List<ProjectEmployee> projectEmployees = employee.getProjectEmployees();
        projectEmployees.clear();
        for (UUID projectId : employeeRequest.getProjects()) {
            projectEmployees.add(new ProjectEmployee(employee, projectService.findEntityById(projectId)));
        }
        setProjectNameFieldsForEmployeeSorting(employee, projectEmployees.stream()
                .map(projectEmployee -> projectEmployee.getProject().getName())
                .collect(Collectors.toList()));

        List<RoleEnum> roles = employeeRequest.getRoles();
        Set<Role> employeeRoles = employee.getRoles();
        List<RoleEnum> currentUserRoles = currentUserService.getCurrentUser().getRoles();
        if (roles != null) {
            if (!employeeRoles.contains(roleService.findByCode(RoleEnum.ADMIN.getCode())) &&
                    !currentUserRoles.contains(RoleEnum.ADMIN)) {
                roles.remove(RoleEnum.ADMIN);
            }
            employeeRoles.clear();
            for (RoleEnum role : roles) {
                employeeRoles.add(roleService.findByCode(role.getCode()));
            }
            employeeRoles.add(roleService.findByCode(RoleEnum.USER.getCode()));
        }
        setRoleFieldsForEmployeeSorting(employee);

        List<ProjectPM> projectPMs = employee.getProjectPMs();
        projectPMs.clear();
        if (employeeRoles.contains(roleService.findByCode(RoleEnum.PM.getCode()))) {
            for (UUID projectPMId : employeeRequest.getManagedProjects()) {
                projectPMs.add(new ProjectPM(employee, projectService.findEntityById(projectPMId)));
            }
        }

        if (isTrue(employeeRequest.getIsCustomCalendar())) {
            Calendar calendar = employee.getCalendar();
            Optional<Calendar> personalCalendar = calendarRepository.findByNameIgnoreCase(employee.getId().toString());
            if (personalCalendar.isPresent()) {
                UpdateCalendarRequest updateCalendarRequest = mapper.map(employeeRequest, UpdateCalendarRequest.class);
                updateCalendarRequest.setName(employee.getId().toString());
                calendarService.update(updateCalendarRequest, personalCalendar.get().getId().toString());
            } else {
                CalendarRequest calendarRequest = mapper.map(employeeRequest, CalendarRequest.class);
                calendarRequest.setName(employee.getId().toString());
                Calendar createdCalendar = calendarService.createCalendarEntity(calendarRequest);
                createdCalendar.getEvents().addAll(calendar.getEvents());
                createdCalendar.setCustomCalendar(true);

                calendar = calendarService.saveFromEntity(createdCalendar);
                employee.setCalendar(calendar);
            }
        }

        List<Office> hrOffices = employee.getOffices();
        hrOffices.clear();
        if (employeeRoles.contains(roleService.findByCode(RoleEnum.HR.getCode()))) {
            for (UUID hrOfficeId : employeeRequest.getOffices()) {
                hrOffices.add(officeService.findEntityById(hrOfficeId.toString()));
            }
        }

        List<Office> managedOffices = employee.getManagedOffices();
        managedOffices.clear();
        if (employeeRoles.contains(roleService.findByCode(RoleEnum.DIRECTOR.getCode()))) {
            for (UUID managedOfficeId : employeeRequest.getManagedOffices()) {
                managedOffices.add(officeService.findEntityById(managedOfficeId.toString()));
            }
        }

        String officeId = employeeRequest.getBaseOffice();
        if (officeId != null) {
            Office currentOffice = officeService.findEntityById(officeId);
            if (!employeeRequest.getIsCustomCalendar()) {
                employee.setCalendar(currentOffice.getCalendar());
            } else {
                Calendar calendar = calendarRepository.findByNameIgnoreCase(employee.getId().toString())
                        .orElseThrow(() -> new CalendarNotFound(employee.getId().toString()));
                employee.setCalendar(calendar);
            }
            employee.setBaseOffice(currentOffice);
        }
        return mapper.map(repository.save(employee), EmployeeAndUserDto.class);
    }

    /**
     * Добавление первого и последнего наименования отсортированных ролей
     * для последующей сортировки сотрудников по ролям
     *
     * @param employee сущность сотрудника
     */
    public static void setRoleFieldsForEmployeeSorting(Employee employee) {
        List<String> roleNames = employee.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        employee.setAscRoleName(roleNames.stream()
                .min(Comparator.comparing(name -> name, String.CASE_INSENSITIVE_ORDER))
                .orElse(null));
        employee.setDescRoleName(roleNames.stream()
                .max(Comparator.comparing(name -> name, String.CASE_INSENSITIVE_ORDER))
                .orElse(null));
    }

    private static void setCalendarInfoToResponse(EmployeeDetailedResponse employeeAndUserDto, CalendarResponse calendarResponse) {
        employeeAndUserDto.setCalendarId(calendarResponse.getId());
        employeeAndUserDto.setCalendarName(calendarResponse.getName());
        employeeAndUserDto.setStartWeekDay(calendarResponse.getStartWeekDay());
        employeeAndUserDto.setWeekendDays(calendarResponse.getWeekendDays());
        employeeAndUserDto.setWorkHoursFrom(calendarResponse.getWorkHoursFrom());
        employeeAndUserDto.setWorkHoursTo(calendarResponse.getWorkHoursTo());
        employeeAndUserDto.setEvents(new ArrayList<>(calendarResponse.getEvents()));
    }

    private static List<UUID> sortEmployeeProjectsIdsByProjectName(List<ProjectEmployee> projects) {
        List<UUID> projectsIds = new ArrayList<>();
        if (projects != null) {
            projectsIds = projects.stream()
                    .sorted(Comparator.comparing(project -> project.getProject().getName()))
                    .map(project -> project.getProject().getId())
                    .collect(Collectors.toList());
        }
        return projectsIds;
    }

    private static List<UUID> selectAvailableIdsByFilter(List<UUID> availableIds, UUID filterId) {
        List<UUID> ids = availableIds;
        if (filterId != null && availableIds.contains(filterId)) {
            ids = Collections.singletonList(filterId);
        }
        return ids;
    }

    /**
     * Добавление первого и последнего наименования отсортированных проектов
     * для последующей сортировки сотрудников по проектам
     *
     * @param employee     сущность сотрудника
     * @param projectNames названия проектов
     */
    public static void setProjectNameFieldsForEmployeeSorting(Employee employee, Collection<String> projectNames) {
        employee.setAscProjectName(projectNames.stream()
                .min(Comparator.comparing(name -> name, String.CASE_INSENSITIVE_ORDER))
                .orElse(null));
        employee.setDescProjectName(projectNames.stream()
                .max(Comparator.comparing(name -> name, String.CASE_INSENSITIVE_ORDER))
                .orElse(null));
    }

    /**
     * Получение всех активных сотрудников с ролью HR
     *
     * @return список активных сотрудников с ролью HR
     */
    public List<Employee> getAllHREmployees() {
        return repository.findAllByUserActiveTrueAndRoles(roleService.findByCode(RoleEnum.HR.getCode()));
    }
}
