package com.artezio.artping.service.integration;

import com.artezio.artping.data.exceptions.ArtPingException;
import com.artezio.artping.data.exceptions.InvalidImportDataException;
import com.artezio.artping.data.exceptions.ReadImportFileException;
import com.artezio.artping.dto.RoleEnum;
import com.artezio.artping.entity.calendar.DayType;
import com.artezio.artping.service.integration.dto.CalendarDto;
import com.artezio.artping.service.integration.dto.EmployeeDto;
import com.artezio.artping.service.integration.dto.EventDto;
import com.artezio.artping.service.integration.dto.OfficeDto;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component("xlsxAdapter")
@RequiredArgsConstructor
public class XlsxAdapterImpl implements IXlsxAdapter {

    private static final DataFormatter FORMATTER = new DataFormatter();
    private static final String CALENDAR_LIST_NAME = "calendar";
    private static final String EVENT_LIST_NAME = "event";
    private static final String OFFICE_LIST_NAME = "office";
    private static final String EMPLOYEE_LIST_NAME = "employee";
    private static final String DELIM = ";";

    private final IntegrationFacade integrationFacade;

    @Override
    public String process(MultipartFile mfile) throws ArtPingException {
        try (InputStream is = getInputStream(mfile);
             XSSFWorkbook workbook = getWorkbook(is)) {
            log.info("File read");

            Collection<OfficeDto> offices = getOffices(workbook);
            log.info("Data converted");

            integrationFacade.integrationImport(offices);
            log.info("Data transformed");

            log.info("Import successful");
            return "ok";
        } catch (IOException e) {
            throw new ArtPingException(e.getMessage());
        }
    }

    private static Collection<OfficeDto> getOffices(XSSFWorkbook workbook) {
        Collection<CalendarDto> calendars = getCalendars(workbook);
        attachEventsToCalendar(calendars, workbook);
        Collection<OfficeDto> offices = getOffices(calendars, workbook);
        attachEmployeesToOffice(offices, calendars, workbook);
        return offices;
    }

    private static XSSFWorkbook getWorkbook(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        return new XSSFWorkbook(is);
    }

    private static InputStream getInputStream(MultipartFile mInFile) {
        if (mInFile == null) {
            return null;
        }
        try {
            return mInFile.getInputStream();
        } catch (IOException e) {
            throw new ReadImportFileException(e.getMessage());
        }
    }

    private static Collection<CalendarDto> getCalendars(XSSFWorkbook workbook) {
        if (workbook == null) {
            return Collections.emptySet();
        }
        Collection<CalendarDto> result = new HashSet<>();

        XSSFSheet sheet = workbook.getSheet(CALENDAR_LIST_NAME);
        Iterator<Row> rowItr = sheet.iterator();
        rowItr.next();

        Row row;
        CalendarDto calendar;
        while (rowItr.hasNext()) {
            row = rowItr.next();
            if (isEmptyRow(row)) {
                continue;
            }

            Long id = Long.valueOf(FORMATTER.formatCellValue(row.getCell(0)));
            String name = FORMATTER.formatCellValue(row.getCell(1));
            LocalTime from = LocalTime.parse(FORMATTER.formatCellValue(row.getCell(2)));
            LocalTime to = LocalTime.parse(FORMATTER.formatCellValue(row.getCell(3)));
            int[] mask = parseArray(FORMATTER.formatCellValue(row.getCell(4)));
            Integer startDay = Integer.valueOf(FORMATTER.formatCellValue(row.getCell(5)));
            calendar = new CalendarDto(name, startDay, mask, from, to, new HashSet<>());
            calendar.setId(id);
            //
            result.add(calendar);
        }

        return result;
    }

    private static boolean isEmptyRow(Row row) {
        Cell cell = row.getCell(0);
        return cell == null || cell.getCellType() == CellType.BLANK;
    }

    /**
     * Парсинг страницы с событиями и присоединение к календарям Collection<EventDto>
     *
     * @param calendars коллекция ДТО календарей
     * @param workbook книга с данными импорта
     */
    private static void attachEventsToCalendar(Collection<CalendarDto> calendars, XSSFWorkbook workbook) {
        if (isEmpty(calendars) || workbook == null) {
            return;
        }

        XSSFSheet sheet = workbook.getSheet(EVENT_LIST_NAME);
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isEmptyRow(row)) {
                continue;
            }

            LocalDate date = LocalDate.parse(FORMATTER.formatCellValue(row.getCell(0)));
            DayType type = DayType.getDayType(FORMATTER.formatCellValue(row.getCell(1)));
            Long calendarId = Long.valueOf(FORMATTER.formatCellValue(row.getCell(2)));
            calendars.stream()
                     .filter(calendarDto -> calendarDto.getId().equals(calendarId))
                     .forEach(calendar -> {
                         EventDto event = new EventDto(calendar, date, type);
                         event.setCalendar(calendar);
                         calendar.addEvent(event);
                     });
        }
    }

    /**
     * Парсинг страницы с оффисами
     *
     * @param calendars коллекция ДТО календарей
     * @param workbook книга с данными импорта
     *
     * @return Collection<OfficeDto>
     */
    private static Collection<OfficeDto> getOffices(Collection<CalendarDto> calendars, XSSFWorkbook workbook) {
        if (workbook == null) {
            return Collections.emptySet();
        }
        Collection<OfficeDto> result = new HashSet<>();

        XSSFSheet sheet = workbook.getSheet(OFFICE_LIST_NAME);
        Iterator<Row> rowItr = sheet.iterator();
        rowItr.next();

        Row row;
        while (rowItr.hasNext()) {
            row = rowItr.next();
            if (isEmptyRow(row)) {
                continue;
            }

            String name = FORMATTER.formatCellValue(row.getCell(0));
            Long calendarId = Long.valueOf(FORMATTER.formatCellValue(row.getCell(1)));
            Long id = Long.valueOf(FORMATTER.formatCellValue(row.getCell(2)));
            String timezone = FORMATTER.formatCellValue(row.getCell(3));
            calendars.stream()
                     .filter(calendarDto -> calendarDto.getId().equals(calendarId))
                     .forEach(calendar -> {
                         OfficeDto office = new OfficeDto(name, calendar, new HashSet<>(), timezone);
                         office.setId(id);
                         office.setCalendar(calendar);
                         result.add(office);
                     });
        }
        return result;
    }

    /**
     * Парсинг страницы с сотрудниками и присоединение к офисам Collection<EmployeeDto>
     *
     * @param calendars коллекция ДТО календарей
     * @param workbook книга с данными импорта
     * @param offices коллекция с ДТО офисов
     */
    private static void attachEmployeesToOffice(Collection<OfficeDto> offices, Collection<CalendarDto> calendars, XSSFWorkbook workbook) {
        if (isEmpty(offices) || workbook == null) {
            return;
        }

        XSSFSheet sheet = workbook.getSheet(EMPLOYEE_LIST_NAME);
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isEmptyRow(row)) {
                continue;
            }

            String firstName = FORMATTER.formatCellValue(row.getCell(0));
            String lastName = FORMATTER.formatCellValue(row.getCell(1));
            String login = FORMATTER.formatCellValue(row.getCell(2));
            String password = FORMATTER.formatCellValue(row.getCell(3));
            Long calendarId = Long.valueOf(FORMATTER.formatCellValue(row.getCell(4)));
            Long officeId = Long.valueOf(FORMATTER.formatCellValue(row.getCell(5)));
            String email = FORMATTER.formatCellValue(row.getCell(6));
            String[] rolesArray = FORMATTER.formatCellValue(row.getCell(7)).split(DELIM);
            List<RoleEnum> roles = new ArrayList<>();
            try {
                for (String role : rolesArray) {
                    roles.add(RoleEnum.fromCode(role));
                }
            } catch (IllegalArgumentException ex) {
                throw new InvalidImportDataException("Неверное значение роли у сотрудника " + login +
                        ". Возможные значения: " + Arrays.toString(RoleEnum.values()));
            }
            EmployeeDto employee = new EmployeeDto();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setLogin(login);
            employee.setPassword(password);
            employee.setEmail(email);
            calendars.stream()
                     .filter(calendarDto -> calendarDto.getId().equals(calendarId))
                     .forEach(employee::setCalendar);
            offices.stream()
                   .filter(officeDto -> officeDto.getId().equals(officeId))
                   .forEach(office -> {
                       employee.setOffice(office);
                       office.addEmployee(employee);
                   });
            employee.setRoles(roles);
        }
    }

    private static int[] parseArray(String str) {
        if (isBlank(str)) {
            return null;
        }
        return Stream.of(str.split(DELIM)).mapToInt(Integer::valueOf).toArray();
    }

}

