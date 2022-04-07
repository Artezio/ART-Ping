package com.artezio.artping.service;

import com.artezio.artping.controller.request.EmployeesPageFilter;
import com.artezio.artping.data.exceptions.NoReferencesException;
import com.artezio.artping.dto.ReferenceResponse;
import com.artezio.artping.dto.RoleEnum;
import com.artezio.artping.dto.office.OfficeDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Сервис для работы со справочниками сотрудников, офисов, календарей и проектов
 */
@Slf4j
@Service
@AllArgsConstructor
public class ReferenceService {

    private final ProjectService projectService;
    private final MapperFacade mapper;
    private final OfficeService officeService;
    private final EmployeeService employeeService;
    private final CalendarService calendarService;
    private final CurrentUserService currentUserService;

    /**
     * Получение выбранного справочника
     *
     * @param refKey ключ, определяющий необходимый справочник
     * @return выбранный справочник, каждый элемент которого содержит иденификатор имя и описание
     */
    @Transactional
    public List<ReferenceResponse> getReference(String refKey) {
        ReferenceType type = ReferenceType.fromName(refKey);
        EmployeesPageFilter filter = new EmployeesPageFilter();
        filter.setPageNumber(0);
        filter.setPageSize(Integer.MAX_VALUE);
        try {
            switch (type) {
                case PROJECTS:
                    return mapper.mapAsList(projectService.getAllAvailableProjects(), ReferenceResponse.class);
                case OFFICES:
                    List<RoleEnum> roles = currentUserService.getCurrentUser().getRoles();
                    Collection<OfficeDto> offices;
                    if (roles.contains(RoleEnum.PM)) {
                        offices = officeService.findAll();
                    } else {
                        offices = officeService.getByFilter(filter).getList();
                    }
                    return mapper.mapAsList(offices, ReferenceResponse.class);
                case EMPLOYEE:
                    return mapper.mapAsList(employeeService.getEmployeesPage(filter).getList(),
                            ReferenceResponse.class);
                case CALENDARS:
                    return mapper.mapAsList(calendarService.getByFilter(filter).getList(),
                            ReferenceResponse.class);
            }
        } catch (SecurityException ex) {
            log.error(ex.getMessage());
        }
        return Collections.emptyList();
    }

    private enum ReferenceType {
        PROJECTS("projects"),
        OFFICES("offices"),
        EMPLOYEE("employee"),
        CALENDARS("calendars");

        private String name;

        ReferenceType(String name) {
            this.name = name;
        }

        public static ReferenceType fromName(String name) {
            for (ReferenceType reference : ReferenceType.values()) {
                if (reference.name.equals(name)) return reference;
            }
            throw new NoReferencesException(name);
        }
    }
}
