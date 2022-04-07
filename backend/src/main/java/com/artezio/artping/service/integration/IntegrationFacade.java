package com.artezio.artping.service.integration;

import com.artezio.artping.data.exceptions.ArtPingException;
import com.artezio.artping.data.exceptions.InvalidImportDataException;
import com.artezio.artping.dto.calendar.request.CalendarRequest;
import com.artezio.artping.dto.calendar.response.CreatedCalendarResponse;
import com.artezio.artping.dto.office.request.CreateOfficeRequest;
import com.artezio.artping.dto.office.response.OfficeResponse;
import com.artezio.artping.dto.user.request.UserCreationRequest;
import com.artezio.artping.entity.calendar.Calendar;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.service.CalendarService;
import com.artezio.artping.service.EmployeeService;
import com.artezio.artping.service.OfficeService;
import com.artezio.artping.service.UserService;
import com.artezio.artping.service.integration.dto.CalendarDto;
import com.artezio.artping.service.integration.dto.EmployeeDto;
import com.artezio.artping.service.integration.dto.OfficeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
public class IntegrationFacade {

    private final MapperFacade mapper;
    private final CalendarService calendarService;
    private final OfficeService officeService;
    private final UserService userService;
    private final EmployeeService employeeService;

    @Transactional
    public void integrationImport(Collection<OfficeDto> offices) throws ArtPingException {
        Set<CalendarDto> calendars = offices.stream()
                                            .map(OfficeDto::getCalendar)
                                            .collect(Collectors.toSet());
        List<EmployeeDto> employees = offices.stream()
                                             .map(OfficeDto::getEmployees)
                                             .flatMap(Collection::stream)
                                             .collect(Collectors.toList());
        calendars.addAll(employees.stream().map(EmployeeDto::getCalendar).collect(Collectors.toSet()));

        validateData(offices, employees, calendars);
        log.info("Data validated");

        try {
            Map<OfficeDto, String> officeIdsForEmployees = new HashMap<>();
            Map<CalendarDto, CreatedCalendarResponse> calendarResponsesForEmployees = new HashMap<>();

            calendars.forEach(calendarDto -> {
                if (!calendarResponsesForEmployees.containsKey(calendarDto)) {
                    calendarResponsesForEmployees.put(calendarDto,
                                                      calendarService.save(mapper.map(calendarDto, CalendarRequest.class)));
                }
            });

            offices.forEach(office -> {
                CreateOfficeRequest officeRequest = mapper.map(office, CreateOfficeRequest.class);
                officeRequest.setCalendarId(calendarResponsesForEmployees.get(office.getCalendar()).getId());
                OfficeResponse createdOffice = officeService.create(officeRequest);
                officeIdsForEmployees.put(office, createdOffice.getId());
            });

            Set<CalendarDto> officeCalendars = offices.stream()
                                                      .map(OfficeDto::getCalendar).collect(Collectors.toSet());
            for (EmployeeDto employeeDto : employees) {
                UserCreationRequest userCreationRequest = mapper.map(employeeDto, UserCreationRequest.class);
                userCreationRequest.setBaseOffice(officeIdsForEmployees.get(employeeDto.getOffice()));

                CreatedCalendarResponse employeeCalendar = calendarResponsesForEmployees.get(employeeDto.getCalendar());
                Employee employeeById = employeeService.findEmployeeById(
                        UUID.fromString(userService.registerUser(userCreationRequest).getId()));
                Calendar calendar = calendarService.getById(employeeCalendar.getId());
                if (!officeCalendars.contains(employeeDto.getCalendar())) {
                    calendar.setCustomCalendar(true);
                }
                employeeById.setCalendar(calendar);
            }
        } catch (Exception e) {
            throw new ArtPingException(e.getMessage());
        }
    }

    private void validateData(Collection<OfficeDto> offices, Collection<EmployeeDto> employees,
                              Collection<CalendarDto> calendars) {
        validateOffices(offices);
        validateReqFields(employees);
        validateLogins(employees);
        validateEmails(employees);
        validateCalendars(calendars);
        validateEvents(calendars);
    }

    private static void validateEvents(Collection<CalendarDto> calendars) {
        long invalidData = calendars.stream()
                                    .map(CalendarDto::getEvents)
                                    .flatMap(Collection::stream)
                                    .filter(e -> e.getDate() == null)
                                    .count();
        if (invalidData > 0) {
            throw new InvalidImportDataException("Не заполнена дата у события");
        }
    }

    private void validateCalendars(Collection<CalendarDto> calendars) {
        List<String> names = calendars.stream()
                .map(CalendarDto::getName)
                .collect(Collectors.toList());
        List<String> notUniqueNames = findNotUniqueNames(names);
        if (!notUniqueNames.isEmpty()) {
            throw new InvalidImportDataException("Наименования календарей должны быть уникальными. " +
                    "Следующие наименования не уникальны в файле импорта: " + notUniqueNames.toString());
        }
        names.addAll(calendarService.getAllCalendarNames());
        notUniqueNames = findNotUniqueNames(names);
        if (!notUniqueNames.isEmpty()) {
            throw new InvalidImportDataException("Некоторые названия календарей уже содержатся в БД: " +
                    notUniqueNames.toString());
        }
        for (CalendarDto calendar : calendars) {
            Integer startWeekDay = calendar.getStartWeekDay();
            if (calendar.getWorkHoursFrom() == null || calendar.getWorkHoursTo() == null ||
                    calendar.getWeekendMask() == null) {
                throw new InvalidImportDataException("Не заполнены поля календаря");
            }

            for (int i : calendar.getWeekendMask()) {
                validateDay(i);
            }
            validateDay(startWeekDay);
        }
    }

    private static void validateDay(Integer testDayNum) {
        if (testDayNum > 6 || testDayNum < 0) {
            throw new InvalidImportDataException("Некорректное значение дня, с которого начинается неделя");
        }
    }

    private void validateLogins(Collection<EmployeeDto> employees) {
        List<String> names = employees.stream()
                .map(EmployeeDto::getLogin)
                .collect(Collectors.toList());
        List<String> notUniqueNames = findNotUniqueNames(names);
        if (!notUniqueNames.isEmpty()) {
            throw new InvalidImportDataException("Логины сотрудников должны быть уникальными. " +
                    "Следующие логины не уникальны в файле импорта: " + notUniqueNames.toString());
        }
        names.addAll(employeeService.getAllEmployeesLogin());
        notUniqueNames = findNotUniqueNames(names);
        if (!notUniqueNames.isEmpty()) {
            throw new InvalidImportDataException("Некоторые логины сотрудников уже содержатся в БД: " +
                    notUniqueNames.toString());
        }
    }

    private void validateEmails(Collection<EmployeeDto> employees) {
        List<String> names = employees.stream()
                .map(EmployeeDto::getEmail)
                .collect(Collectors.toList());
        List<String> notUniqueNames = findNotUniqueNames(names);
        if (!notUniqueNames.isEmpty()) {
            throw new InvalidImportDataException("Email сотрудников должны быть уникальными. " +
                    "Следующие email не уникальны в файле импорта: " + notUniqueNames.toString());
        }

        //  проверка на дубли входного файла с тем, что уже есть в БД
        names.addAll(new HashSet<>(employeeService.getAllEmployeeEmails()));
        notUniqueNames = findNotUniqueNames(names);
        if (!notUniqueNames.isEmpty()) {
            throw new InvalidImportDataException("Некоторые email сотрудников уже содержатся в БД: " +
                    notUniqueNames.toString());
        }
    }

    private static void validateReqFields(Collection<EmployeeDto> employees) {
        for (EmployeeDto employee : employees) {
            String login = employee.getLogin();
            OfficeDto office = employee.getOffice();
            if (isEmpty(employee.getFirstName()) || isEmpty(employee.getLastName()) || isEmpty(login) ||
                    isEmpty(employee.getPassword()) || isEmpty(employee.getEmail()) || office == null) {
                throw new InvalidImportDataException("Не заполнены обязательные поля сотрудника");
            }
        }
    }

    private void validateOffices(Collection<OfficeDto> offices) {
        List<String> databaseOfficeNames = officeService.findAllOfficeNames();
        List<String> notUniqueNames = findNotUniqueNames(offices.stream()
                .map(OfficeDto::getName)
                .collect(Collectors.toList()));
        if (!notUniqueNames.isEmpty()) {
            throw new InvalidImportDataException("Имена офисов должны быть уникальными. " +
                    "Следующие офисы не уникальны в файле импорта: " + notUniqueNames.toString());
        }
        List<String> officeNames = offices.stream()
                .map(OfficeDto::getName)
                .collect(Collectors.toList());
        officeNames.addAll(databaseOfficeNames);
        notUniqueNames = findNotUniqueNames(officeNames);
        if (!notUniqueNames.isEmpty()) {
            throw new InvalidImportDataException("Некоторые имена офисов уже содержатся в БД: " +
                    notUniqueNames.toString());
        }
        Pattern timezonePattern = Pattern.compile("UTC[+-]([0-9]|1[0-2])");
        for (OfficeDto office : offices) {
            if (isEmpty(office.getName())) {
                throw new InvalidImportDataException("Имя офиса не должно быть пустым");
            }
            if (office.getCalendar() == null) {
                throw new InvalidImportDataException("У офиса обязательно должен быть календарь");
            }
            if (!timezonePattern.matcher(office.getTimezone()).matches()) {
                throw new InvalidImportDataException("Таймзона в офисах должна быть в формате UTC[+-](0-12) (UTC+3)");
            }
        }
    }

    private static List<String> findNotUniqueNames(List<String> names) {
        List<String> notUniqueNames = new ArrayList<>();
        Set<String> nameSet = new HashSet<>(names);
        if (names.size() != nameSet.size()) {
            for (String name : nameSet) {
                List<String> foundNames = names.stream()
                        .filter(n -> n.equals(name))
                        .collect(Collectors.toList());
                if (foundNames.size() > 1) {
                    notUniqueNames.add(foundNames.get(0));
                }
            }
        }
        return notUniqueNames;
    }

}
