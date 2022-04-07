package com.artezio.artping.service;

import com.artezio.artping.controller.PageData;
import com.artezio.artping.controller.PageHelper;
import com.artezio.artping.controller.request.SearchStringFilter;
import com.artezio.artping.controller.request.SortDto;
import com.artezio.artping.data.exceptions.NoOfficesFoundException;
import com.artezio.artping.data.exceptions.OfficeHasEmployeesException;
import com.artezio.artping.data.exceptions.OfficeWithThisNameAlreadyExist;
import com.artezio.artping.data.repository.OfficeRepository;
import com.artezio.artping.dto.RoleEnum;
import com.artezio.artping.dto.office.OfficeDto;
import com.artezio.artping.dto.office.request.CreateOfficeRequest;
import com.artezio.artping.dto.office.response.OfficeResponse;
import com.artezio.artping.dto.office.request.UpdateOfficeRequest;
import com.artezio.artping.entity.calendar.Calendar;
import com.artezio.artping.entity.office.Office;

import java.util.*;
import java.util.stream.Collectors;

import com.artezio.artping.entity.user.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для работы с офисами
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OfficeService {

    private final OfficeRepository repository;
    private final CurrentUserService currentUserService;
    private final MapperFacade mapper;
    private CalendarService calendarService;
    @Autowired(required = false)
    private EmployeeService employeeService;
    private final PageHelper pageHelper;

    @Autowired(required = false)
    public void setCalendarService(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    /**
     * Создание офиса
     *
     * @param officeRequest информация о создаваемом офисе
     * @return информация об офисе с его идентификатором
     */
    @Transactional
    public OfficeResponse create(CreateOfficeRequest officeRequest) {
        if (repository.findByNameIgnoreCase(officeRequest.getName()).isPresent()) {
            throw new OfficeWithThisNameAlreadyExist();
        }
        Calendar calendar = calendarService.getById(officeRequest.getCalendarId());
        Office toSave = mapper.map(officeRequest, Office.class);
        toSave.setCalendar(calendar);
        Office savedOffice = repository.save(toSave);
        return mapper.map(savedOffice, OfficeResponse.class);
    }

    /**
     * Изменение информации об офисе
     *
     * @param office   новая информация офиса
     * @param officeId идентификатор офиса
     * @return информация об изменённом офисе
     */
    @Transactional
    public OfficeResponse update(UpdateOfficeRequest office, String officeId) {
        Office officeToUpdate = repository.findById(UUID.fromString(officeId)).orElseThrow(NoOfficesFoundException::new);
        if (isExistOfficeWithName(officeToUpdate, office.getName())) {
            throw new OfficeWithThisNameAlreadyExist();
        }
        officeToUpdate.setName(office.getName());
        officeToUpdate.setTimezone(office.getTimezone());
        Calendar currentCalendar = officeToUpdate.getCalendar();
        if (!office.getCalendarId().equals(currentCalendar.getId().toString())) {
            Calendar requestCalendar = calendarService.getById(office.getCalendarId());
            officeToUpdate.setCalendarId(UUID.fromString(office.getCalendarId()));
            for (Employee officeEmployee : officeToUpdate.getOfficeEmployees()) {
                Calendar employeeCalendar = officeEmployee.getCalendar();
                if (!employeeCalendar.isCustomCalendar()) {
                    officeEmployee.setCalendar(requestCalendar);
                } else {
                    employeeCalendar.getEvents().addAll(requestCalendar.getEvents());
                }
            }
        }
        Office savedOffice = repository.save(officeToUpdate);
        return mapper.map(savedOffice, OfficeResponse.class);
    }

    /**
     * Удаление офиса при условии, что в нём не содержатся сотрудники
     *
     * @param officeId идентификатор офиса
     */
    @Transactional
    public void deleteById(String officeId) {
        Office office = findEntityById(officeId);
        List<Employee> employees = employeeService.findAllByOfficeId(UUID.fromString(officeId));
        if (!employees.isEmpty()) {
            throw new OfficeHasEmployeesException(office.getName());
        }
        repository.deleteById(UUID.fromString(officeId));
    }

    /**
     * Получение всех офисов в отсортированном виде
     *
     * @return список офисов
     */
    public List<OfficeDto> findAll() {
        List<Office> offices = repository.findAll();
        return !offices.isEmpty() ? mapAndSort(offices) : Collections.emptyList();
    }

    /**
     * Получение всех офисов с фильтрацией Пользователь с ролью admin может видеть все офисы
     * Пользователь с ролью director только свой офис
     *
     * @return страница с результатами поиска
     */
    @Transactional
    public PageData<OfficeDto> getByFilter(SearchStringFilter filter) {
        SortDto[] sorting = filter.getSorting();
        Pageable pageable;
        if (sorting == null) {
            pageable = pageHelper.processFilter(filter, SortDto.of("name"));
        } else {
            pageable = pageHelper.processFilter(filter, sorting);
        }

        List<RoleEnum> userRoles = currentUserService.getCurrentUser().getRoles();

        Page<Office> page;
        if (userRoles.contains(RoleEnum.ADMIN)) {
            page = repository.findByNameContainingIgnoreCase(filter.getSearchString(), pageable);
        } else {
            Employee employee = employeeService.getCurrentEmployee();
            Set<UUID> officeIds = new HashSet<>();
            if (userRoles.contains(RoleEnum.DIRECTOR)) {
                officeIds.addAll(employee.getManagedOffices().stream().map(Office::getId).collect(Collectors.toList()));
            }
            if (userRoles.contains(RoleEnum.HR)) {
                officeIds.addAll(employee.getOffices().stream().map(Office::getId).collect(Collectors.toList()));
            }
            page = repository.findByNameContainingIgnoreCaseAndIdIn(filter.getSearchString(), officeIds, pageable);
        }

        List<OfficeDto> result = mapper.mapAsList(page.getContent(), OfficeDto.class);
        return new PageData<>(result, page.getTotalElements());
    }

    /**
     * Получение сущности офиса по его идентификатору
     *
     * @param id идентификатор офиса
     * @return сущность офиса
     */
    public Office findEntityById(String id) {
        UUID uuid = UUID.fromString(id);
        return repository.findById(uuid).orElseThrow(NoOfficesFoundException::new);
    }

    /**
     * Получение информации об офисе по идентификатору при условии, что сотрудник имеет права на его просмотр
     *
     * @param id идентификатор офиса
     * @return информация об офисе
     */
    @Transactional
    public OfficeDto findById(String id) {
        Office office = repository.findById(UUID.fromString(id)).orElseThrow(NoOfficesFoundException::new);
        List<RoleEnum> currentUserRoles = currentUserService.getCurrentUser().getRoles();
        Employee currentEmployee = employeeService.getCurrentEmployee();
        if (!currentUserRoles.contains(RoleEnum.ADMIN)) {
            Set<Office> availableOffices = new HashSet<>();
            if (currentUserRoles.contains(RoleEnum.DIRECTOR)) {
                availableOffices.addAll(currentEmployee.getManagedOffices());
            }
            if (currentUserRoles.contains(RoleEnum.HR)) {
                availableOffices.addAll(currentEmployee.getOffices());
            }
            if (!availableOffices.contains(office)) {
                throw new SecurityException("Текущий сотрудник не может просматривать данный офис");
            }
        }
        return mapper.map(office, OfficeDto.class);
    }

    /**
     * Получение всех имён офисов
     *
     * @return список имён офисов
     */
    @Transactional
    public List<String> findAllOfficeNames() {
        return repository.findAllOfficeNames();
    }

    private List<OfficeDto> mapAndSort(Iterable<Office> offices) {
        List<OfficeDto> result = mapper.mapAsList(offices, OfficeDto.class);
        result.sort(Comparator.comparing(OfficeDto::getName));
        return result;
    }

    private boolean isExistOfficeWithName(Office officeToUpdate, String officeName) {
        return !officeToUpdate.getName().equals(officeName) && repository.findByNameIgnoreCase(officeName).isPresent();
    }


}
