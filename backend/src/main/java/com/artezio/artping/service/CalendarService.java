package com.artezio.artping.service;

import com.artezio.artping.controller.PageData;
import com.artezio.artping.controller.PageHelper;
import com.artezio.artping.controller.request.SearchStringFilter;
import com.artezio.artping.controller.request.SortDto;
import com.artezio.artping.data.exceptions.*;
import com.artezio.artping.data.repository.CalendarRepository;
import com.artezio.artping.data.repository.EventRepository;
import com.artezio.artping.dto.RoleEnum;
import com.artezio.artping.dto.calendar.CalendarDto;
import com.artezio.artping.dto.calendar.request.CalendarRequest;
import com.artezio.artping.dto.calendar.request.UpdateCalendarRequest;
import com.artezio.artping.dto.calendar.response.CalendarResponse;
import com.artezio.artping.dto.calendar.response.CalendarResponseDto;
import com.artezio.artping.dto.calendar.response.CreatedCalendarResponse;
import com.artezio.artping.dto.calendar.response.EventResponse;
import com.artezio.artping.entity.calendar.Calendar;
import com.artezio.artping.entity.calendar.DayType;
import com.artezio.artping.entity.calendar.Event;
import com.artezio.artping.entity.office.Office;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.entity.user.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы с календарями
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarService {

    private final MapperFacade mapper;
    private final CalendarRepository repository;
    private final EventRepository eventRepository;
    private final PageHelper pageHelper;
    private final RoleService roleService;
    @Autowired(required = false)
    private EmployeeService employeeService;

    /**
     * Создание календаря
     *
     * @param calendar информация о создаваемом календаре
     * @return информация о созданном календаре с идентификатором
     */
    @Transactional
    public CreatedCalendarResponse save(CalendarRequest calendar) {
        Calendar save = repository.saveAndFlush(createCalendarEntity(calendar));
        return mapper.map(save, CreatedCalendarResponse.class);
    }

    /**
     * Создание сущности календаря без его сохранения
     *
     * @param calendar инфомрация о календаре
     * @return сущность календаря
     */
    @Transactional
    public Calendar createCalendarEntity(CalendarRequest calendar) {
        Calendar entity = new Calendar();
        entity.setName(calendar.getName());
        if (repository.findByNameIgnoreCase(entity.getName()).isPresent()) {
            throw new CalendarWithThisNameAlreadyExist();
        }
        entity.setStartWeekDay(calendar.getStartWeekDay());
        String[] workHoursFrom = calendar.getWorkHoursFrom().split(":");
        String[] workHoursTo = calendar.getWorkHoursTo().split(":");
        entity.setWorkHoursFrom(LocalTime.of(Integer.parseInt(workHoursFrom[0]), Integer.parseInt(workHoursFrom[1])));
        entity.setWorkHoursTo(LocalTime.of(Integer.parseInt(workHoursTo[0]), Integer.parseInt(workHoursTo[1])));
        LocalDateTime now = LocalDateTime.now();
        entity.setCreationTime(now);
        entity.setActive(true);

        Set<Event> events = new HashSet<>(mapper.mapAsList(calendar.getEvents(), Event.class));
        events.forEach(day -> day.setCalendar(entity));

        Integer[] weekends = calendar.getWeekendDays();
        if (weekends != null && weekends.length != 0) {
            entity.setWeekendDays(StringUtils.arrayToDelimitedString(weekends, ";"));
        }
        entity.setEvents(events);

        return entity;
    }

    /**
     * Сохранение календаря по его сущности
     *
     * @param calendar сущность календаря
     * @return созданная сущность календаря
     */
    @Transactional
    public Calendar saveFromEntity(Calendar calendar) {
        return repository.save(calendar);
    }

    /**
     * Изменение календаря
     *
     * @param calendarRequest новая информация о календаре
     * @param id              идентификатор календаря
     * @return изменённая информация о календаре
     */
    @Transactional
    public CalendarDto update(UpdateCalendarRequest calendarRequest, String id) {
        Calendar calendarToUpdate = repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new CalendarNotFound(id));
        checkDoesCurrentEmployeeCanWorkWithThisCalendar(calendarToUpdate);

        if (isExistCalendarWithName(calendarToUpdate, calendarRequest.getName())) {
            throw new CalendarWithThisNameAlreadyExist();
        }

        if (calendarRequest.getName() != null) {
            calendarToUpdate.setName(calendarRequest.getName());
        }
        if (calendarRequest.getStartWeekDay() != null) {
            calendarToUpdate.setStartWeekDay(calendarRequest.getStartWeekDay());
        }
        if (calendarRequest.getWorkHoursFrom() != null) {
            String[] workHoursFrom = calendarRequest.getWorkHoursFrom().split(":");
            calendarToUpdate.setWorkHoursFrom(
                    LocalTime.of(Integer.parseInt(workHoursFrom[0]), Integer.parseInt(workHoursFrom[1]))
            );
        }
        if (calendarRequest.getWorkHoursTo() != null) {
            String[] workHoursTo = calendarRequest.getWorkHoursTo().split(":");
            calendarToUpdate.setWorkHoursTo(
                    LocalTime.of(Integer.parseInt(workHoursTo[0]), Integer.parseInt(workHoursTo[1]))
            );
        }

        Set<Event> eventsInBase = calendarToUpdate.getEvents();
        Set<Event> eventSet;
        if (calendarRequest.getEvents() != null) {
            eventSet = mapper.mapAsSet(calendarRequest.getEvents(), Event.class);
            eventSet.forEach(day -> day.setCalendar(calendarToUpdate));
        } else {
            eventSet = Collections.emptySet();
        }

        Integer[] weekendDays = parseWeekendsFromString(calendarToUpdate.getWeekendDays());
        Integer[] weekendDaysRequest = calendarRequest.getWeekendDays();
        if (!Arrays.equals(weekendDays, weekendDaysRequest) && weekendDaysRequest != null) {
            if (weekendDaysRequest.length != 0) {
                calendarToUpdate.setWeekendDays(StringUtils.arrayToDelimitedString(weekendDaysRequest, ";"));
            } else {
                calendarToUpdate.setWeekendDays(null);
            }
        }

        Iterator<Event> eventsIterator = eventSet.iterator();
        while (eventsIterator.hasNext()) {
            Event event = eventsIterator.next();
            UUID eventId = event.getId();
            if (eventId != null) {
                Event updatedDay = eventRepository.findById(eventId)
                        .orElseThrow(() -> new EventNotFoundException(eventId.toString()));
                if (event.getType() == DayType.WORKING_DAY && updatedDay.getType() != DayType.WORKING_DAY ||
                        event.getType() == DayType.WEEKEND && updatedDay.getType() == DayType.WORKING_DAY) {
                    eventsInBase.remove(updatedDay);
                } else {
                    LocalDate date = event.getDate();
                    if (date != null) {
                        updatedDay.setDate(date);
                    }
                    DayType type = event.getType();
                    if (type != null) {
                        updatedDay.setType(event.getType());
                    }
                    String description = event.getDescription();
                    if (description != null) {
                        updatedDay.setDescription(description);
                    }
                }
                eventsIterator.remove();
            }
        }

        eventsInBase.addAll(eventSet);
        Set<Calendar> customCalendars = calendarToUpdate.getOffices().stream()
                .map(Office::getOfficeEmployees)
                .flatMap(Collection::stream)
                .map(Employee::getCalendar)
                .filter(Calendar::isCustomCalendar)
                .collect(Collectors.toSet());
        for (Calendar customCalendar : customCalendars) {
            Set<Event> customCalendarEvents = eventSet.stream()
                    .map(event -> new Event(event.getDate(), event.getType(), event.getDescription(), customCalendar))
                    .collect(Collectors.toSet());
            customCalendar.getEvents().addAll(customCalendarEvents);
        }
        Calendar savedCalendar = repository.saveAndFlush(calendarToUpdate);
        return mapper.map(savedCalendar, CalendarDto.class);
    }

    private Integer[] parseWeekendsFromString(String calendarWeekendDays) {
        Integer[] weekends = new Integer[0];
        if (calendarWeekendDays != null) {
            String[] weekendDaysInString = calendarWeekendDays.split(";");
            weekends = new Integer[weekendDaysInString.length];
            for (int i = 0; i < weekendDaysInString.length; i++) {
                weekends[i] = Integer.parseInt(weekendDaysInString[i]);
            }
        }
        return weekends;
    }

    private boolean isExistCalendarWithName(Calendar calendarToUpdate, String calendarName) {
        return !calendarToUpdate.getName().equals(calendarName) && repository.findByNameIgnoreCase(calendarName).isPresent();
    }

    /**
     * Получение сущности активного календаря по идентификатору
     *
     * @param id идентификатор календаря
     * @return сущность календаря
     */
    @Transactional
    public Calendar getById(String id) {
        return repository.findByIdAndActiveTrue(UUID.fromString(id))
                .orElseThrow(() -> new CalendarNotFound(id));
    }

    /**
     * Получение инфомрации о календаре по идентификатору с проверкой на доступ к просмотру этого календаря сотрудником
     *
     * @param id идентификатор календаря
     * @return информация о календаре
     */
    @Transactional
    public CalendarResponse getResponseById(String id) {
        Calendar calendar = getById(id);
        checkDoesCurrentEmployeeCanWorkWithThisCalendar(calendar);
        CalendarResponse response = mapper.map(calendar, CalendarResponse.class);
        response.setEvents(mapper.mapAsSet(calendar.getEvents(), EventResponse.class));
        return response;
    }

    /**
     * Получить все календари, используется для отображения значений в выпадающем списке
     *
     * @return календари
     */
    @Transactional
    public List<CalendarResponseDto> findAll() {
        return mapper.mapAsList(repository.findAllByActiveTrueAndCustomCalendarFalse(), CalendarResponseDto.class);
    }

    /**
     * Постраничное получение календарей с фильтрацией по имени календаря
     *
     * @return страница с результатами поиска
     */
    @Transactional
    public PageData<CalendarResponseDto> getByFilter(SearchStringFilter filter) {
        SortDto[] sorting = filter.getSorting();
        Pageable pageable;
        if (sorting == null) {
            pageable = pageHelper.processFilter(filter, SortDto.of("name"));
        } else {
            pageable = pageHelper.processFilter(filter, sorting);
        }

        Page<Calendar> calendars;
        Employee currentEmployee = employeeService.getCurrentEmployee();
        Set<Role> currentEmployeeRoles = currentEmployee.getRoles();
        if (!currentEmployeeRoles.contains(roleService.findByCode(RoleEnum.ADMIN.getCode())) &&
                currentEmployeeRoles.contains(roleService.findByCode(RoleEnum.HR.getCode()))) {
            calendars = repository.findAllByActiveTrueAndCustomCalendarFalseAndNameContainingIgnoreCaseAndOfficesIn(
                    filter.getSearchString(), currentEmployee.getOffices(), pageable);
        } else {
            calendars = repository.findAllByActiveTrueAndCustomCalendarFalseAndNameContainingIgnoreCase(filter.getSearchString(), pageable);
        }

        List<CalendarResponseDto> result = mapper.mapAsList(calendars, CalendarResponseDto.class);
        return new PageData<>(result, calendars.getTotalElements());
    }

    /**
     * Отметить календарь как не активный
     *
     * @param id идентификатор календаря
     * @return информация о не активном календаре
     */
    @Transactional
    public CalendarResponseDto markDelete(String id) {
        Calendar calendar = repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new CalendarNotFound(id));
        checkDoesCurrentEmployeeCanWorkWithThisCalendar(calendar);
        UUID calendarId = calendar.getId();
        if (repository.hasLinkedOfficesOrEmployees(calendarId)) {
            throw new CalendarHasDependenciesException(calendar.getName());
        }
        calendar.setActive(false);
        return mapper.map(repository.save(calendar), CalendarResponseDto.class);
    }

    /**
     * Получение типов дней на определённый период (рабочий, выходной и т.д.)
     *
     * @param id        идентификатор календаря
     * @param startDate дата начала периода
     * @param endDate   дата конца периода
     * @return список дата - тип дня
     */
    @Transactional
    public Map<LocalDate, Event> getDayEventsForPeriod(UUID id, LocalDate startDate, LocalDate endDate) {
        return eventRepository.findEventByCalendarIdAndDateBetween(id, startDate, endDate).stream()
                .collect(Collectors.toMap(Event::getDate, event -> event));
    }

    /**
     * Получение всех наименований календарей
     *
     * @return список наименований календарей
     */
    @Transactional
    public List<String> getAllCalendarNames() {
        return repository.findAllCalendarNames();
    }

    /**
     * Получение инфомрации о календаре с событиями за год по идентификатору с проверкой на доступ к просмотру
     * этого календаря сотрудником
     *
     * @param id   идентификатор календаря
     * @param year за какой год получаем события
     * @return информация о календаре с событиями за год
     */
    @Transactional
    public CalendarResponse getCalendarByIdAndYear(String id, Integer year) {
        Calendar calendar = getById(id);
        checkDoesCurrentEmployeeCanWorkWithThisCalendar(calendar);
        CalendarResponse response = mapper.map(calendar, CalendarResponse.class);
        Set<EventResponse> events = mapper.mapAsSet(eventRepository.findEventByCalendarIdAndDateBetween(
                calendar.getId(), LocalDate.of(year, 1, 1),
                LocalDate.of(year, 12, 31)), EventResponse.class);
        response.setEvents(events);
        return response;
    }

    private void checkDoesCurrentEmployeeCanWorkWithThisCalendar(Calendar calendar) {
        Employee employee = employeeService.getCurrentEmployee();
        Role adminRole = roleService.findByCode(RoleEnum.ADMIN.getCode());
        if (!employee.getRoles().contains(adminRole)) {
            Set<Calendar> availableCalendars = findAvailableCalendarsForEmployee(employee);
            if (!availableCalendars.contains(calendar)) {
                throw new SecurityException("Сотрудник не может работать с данным календарём");
            }
        }
    }

    private Set<Calendar> findAvailableCalendarsForEmployee(Employee employee) {
        List<Office> hrOffices = employee.getOffices();
        Set<Calendar> availableCalendars = hrOffices.stream()
                .map(Office::getCalendar).collect(Collectors.toSet());
        Set<Employee> employees = new HashSet<>();
        hrOffices.forEach(office -> employees.addAll(employeeService.findAllByOfficeId(office.getId())));
        availableCalendars.addAll(employees.stream()
                .map(Employee::getCalendar)
                .collect(Collectors.toSet())
        );
        return availableCalendars;
    }
}
