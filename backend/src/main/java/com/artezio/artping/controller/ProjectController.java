package com.artezio.artping.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.artezio.artping.controller.request.SearchStringFilter;
import com.artezio.artping.dto.CreateProjectDto;
import com.artezio.artping.dto.ProjectDto;
import com.artezio.artping.dto.UpdateProjectDto;
import com.artezio.artping.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.UUID;

import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с проектами
 */
@RestController
@RequestMapping("/projects")
@Api(tags = "Эндпоинты для работы с проектами")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Создание нового проекта
     *
     * @param createProjectDto информация о создаваемом проекте
     * @return информация о созданном проекте с id
     */
    @PostMapping(produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для создания проекта", response = ProjectDto.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<ProjectDto> create(@RequestBody CreateProjectDto createProjectDto) {
        ProjectDto project = projectService.createProject(createProjectDto);
        return ResponseEntity.ok().body(project);
    }

    /**
     * Получение информации о проекте по id
     *
     * @param id идентификатор проекта
     * @return информация о проекте
     */
    @GetMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для получения инфо о проекте по id", response = ProjectDto.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable String id) {
        ProjectDto project = projectService.findById(id);
        return ResponseEntity.ok().body(project);
    }

    /**
     * Изменение информации о проекте по id
     *
     * @param updateProjectDto информация о проекте вместе с id изменяемого
     * @return изменённая информация
     */
    @PutMapping(produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для редактирования проекта", response = ProjectDto.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<ProjectDto> update(@RequestBody UpdateProjectDto updateProjectDto) {
        ProjectDto t = projectService.updateProject(updateProjectDto);
        return ResponseEntity.ok().body(t);
    }

    /**
     * Получение списка проектов постранично
     *
     * @param filter информация о пагинации, фильтрации и сортировке
     * @return
     */
    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для получения списка проектов постранично",
            response = ProjectDto.class, authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<PageData<ProjectDto>> get(SearchStringFilter filter) {
        PageData<ProjectDto> page = projectService.getByFilter(filter);
        return ResponseEntity.ok()
                .header(PageData.HEADER_TOTAL_COUNT, String.valueOf(page.totalCount))
                .body(page);
    }

    /**
     * Получение всех проектов сотрудников в переданном офисе
     *
     * @param officeId идентификатор офиса
     * @return список проектов сотрудников в переданном офисе
     */
    @GetMapping(value = "/office/{officeId}", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Сервис для получения всех проектов для офиса", response = ProjectDto.class,
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<List<ProjectDto>> getAllForOffice(@PathVariable UUID officeId) {
        return ResponseEntity.ok().body(projectService.getProjectsByOfficeId(officeId));
    }

    /**
     * Удаление проекта по id
     *
     * @param projectId идентификатор проекта
     */
    @DeleteMapping(value = "/{projectId}")
    @ApiOperation(value = "Сервис для удаления проекта", authorizations = {@Authorization(value = "apiKey")})
    public void deleteProject(@PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
    }
}
