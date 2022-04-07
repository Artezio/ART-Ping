package com.artezio.artping.service;

import com.artezio.artping.data.exceptions.ArtPingException;
import com.artezio.artping.dto.EmployeeTestStatisticDto;
import com.artezio.artping.service.integration.ExportEmployeeTestRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Сервис для экспорта данных
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private static final String TABLE_NAME = "Employees";
    private static final String HEADER_NAME = "Фамилия/Имя";
    private static final String HEADER_PERCENTAGE = "Процент успешных проверок";

    private final EmployeeTestService employeeTestService;

    /**
     * Экспорт данных о сотрудниках со статистикой их проверок за определённый период
     *
     * @param request список сотрудников и период
     * @param outputStream поток, в который будут записыватся данные о сотрудниках
     */
    @Transactional
    public void exportData(ExportEmployeeTestRequest request, OutputStream outputStream) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle defaultColorCellStyle = workbook.createCellStyle();
            CellStyle lightBlueColorCellStyle = workbook.createCellStyle();
            lightBlueColorCellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            lightBlueColorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBordersToCellStyle(defaultColorCellStyle);
            setBordersToCellStyle(lightBlueColorCellStyle);

            LocalDate startPeriod = request.getStartPeriod();
            LocalDate endPeriod = request.getEndPeriod();

            XSSFSheet sheet = workbook.createSheet(TABLE_NAME);
            int rowNum = createHeader(sheet, 0, defaultColorCellStyle, startPeriod, endPeriod);
            int cellNum = 0;

            List<EmployeeTestStatisticDto> employeeTestStatistic = employeeTestService
                    .getEmployeesTestStatisticForPeriod(request.getEmployeeIds(), startPeriod, endPeriod);
            for (EmployeeTestStatisticDto statisticDto : employeeTestStatistic) {
                cellNum = 0;
                CellStyle cellStyle = defaultColorCellStyle;
                if (rowNum % 2 == 0) {
                    cellStyle = lightBlueColorCellStyle;
                }

                XSSFRow row = sheet.createRow(rowNum++);
                createSellWithStyleAndValue(row, cellNum++, cellStyle, String.format("%s %s.",
                        statisticDto.getLastName(), statisticDto.getFirstName().charAt(0)));

                Map<LocalDate, String> employeeTestStatisticForPeriod = statisticDto.getDayResult();
                LocalDate currentDate = startPeriod;
                while (!currentDate.isAfter(endPeriod)) {
                    String allAndSuccessTests = employeeTestStatisticForPeriod.get(currentDate);
                    if (allAndSuccessTests == null) {
                        allAndSuccessTests = "";
                    }
                    createSellWithStyleAndValue(row, cellNum, cellStyle, allAndSuccessTests);
                    cellNum++;
                    currentDate = currentDate.plusDays(1);
                }
                createSellWithStyleAndValue(row, cellNum++, cellStyle, statisticDto.getPercentage() + "%");
            }

            for (int i = 0; i < cellNum; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new ArtPingException("Возникла проблема с генерацией файла: " + e.getMessage());
        }
    }

    /**
     * Создание заголовочной информации.
     *
     * @param sheet       таблица
     * @param rowNum      номер строки, с которого начинается заголовок
     * @param cellStyle   стиль ячеек
     * @param startPeriod начало периода
     * @param endPeriod   конец периода
     * @return номер строки, на котором заканчивается заголовок
     */
    private int createHeader(XSSFSheet sheet, int rowNum, CellStyle cellStyle,
                             LocalDate startPeriod, LocalDate endPeriod) {
        int cellNum = 0;
        XSSFRow row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Числитель - все проверки");
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Знаменатель - успешные проверки");
        sheet.createRow(rowNum++);
        row = sheet.createRow(rowNum++);
        sheet.createFreezePane(1, rowNum);

        createSellWithStyleAndValue(row, cellNum++, cellStyle, HEADER_NAME);
        if (startPeriod.isAfter(endPeriod)) {
            throw new ArtPingException("Стартовая дата не может быть больше конечной");
        }
        LocalDate currentDate = startPeriod;
        while (!currentDate.isAfter(endPeriod)) {
            createSellWithStyleAndValue(row, cellNum++, cellStyle,
                    currentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            currentDate = currentDate.plusDays(1);
        }
        createSellWithStyleAndValue(row, cellNum, cellStyle, HEADER_PERCENTAGE);
        return rowNum;
    }

    private void createSellWithStyleAndValue(XSSFRow row, int cellNum, CellStyle cellStyle, String value) {
        XSSFCell cell = row.createCell(cellNum);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
    }

    private void setBordersToCellStyle(CellStyle cellStyle) {
        BorderStyle borderStyle = BorderStyle.THIN;
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderRight(borderStyle);
    }
}
