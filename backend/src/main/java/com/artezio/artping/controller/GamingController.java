package com.artezio.artping.controller;

import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.service.EmployeeService;
import com.artezio.artping.service.MinioService;
import io.minio.messages.Item;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.UUID;

/**
 * Контроллер для получения различных картинок
 */
@RestController
@RequestMapping("/gaming")
@Api(tags = "Сервис геймификации")
@RequiredArgsConstructor
public class GamingController {
    private final MinioService minioService;
    private final EmployeeService employeeService;

    /**
     * Получение случайного файла для сотрудника по его офису
     *
     * @param officeId идентификатор офиса
     * @return случайный файл
     * @throws Exception возможные исключения
     */
    @ApiOperation(value = "Получение случайного файла по Id офиса",
            authorizations = {@Authorization(value = "apiKey")})
    @GetMapping(path = "/randomfile/{officeId}")
    public ResponseEntity<Resource> getRandomFile(
            @ApiParam(value = "Id офиса", required = true) @PathVariable(name = "officeId") String officeId) throws Exception {

        return returnRandomFileResponse(officeId);
    }

    /**
     * Получение случайного файла для сотрудника по его идентификатору
     *
     * @param employeeId идентификатор сотрудника
     * @return случайный файл
     * @throws Exception возможные исключения
     */
    @ApiOperation(value = "Получение случайного файла по Id сотрудника",
            authorizations = {@Authorization(value = "apiKey")})
    @GetMapping(path = "/randomfilebyemployee/{employeeId}")
    public ResponseEntity<Resource> getRandomFileByEmployee(
            @ApiParam(value = "Id сотрудника", required = true) @PathVariable(name = "employeeId") UUID employeeId) throws Exception {
        Employee byId = employeeService.findEmployeeById(employeeId);
        return returnRandomFileResponse(byId.getBaseOffice().getId().toString());
    }

    /**
     * Получение случайного файла для сотрудника
     *
     * @return случайный файл
     * @throws Exception возможные исключения
     */
    @ApiOperation(value = "Получение случайного файла для текущего сотрудника",
            authorizations = {@Authorization(value = "apiKey")})
    @GetMapping(path = "/randomfilebycurrentemployee")
    public ResponseEntity<Resource> getRandomFileByCurrentEmployee() throws Exception {
        Employee currentEmp = employeeService.getCurrentEmployee();

        return returnRandomFileResponse(currentEmp.getBaseOffice().getId().toString());
    }

    private ResponseEntity<Resource> returnRandomFileResponse(String officeId) throws Exception {
        Item item = minioService.getRandomObject(officeId);
        String mimeType = URLConnection.guessContentTypeFromName(item.objectName());
        InputStream in = minioService.getObjectInputStream(item.objectName());

        ByteArrayResource res = new ByteArrayResource(IOUtils.toByteArray(in));

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(mimeType))
                .contentLength(res.contentLength())
                .body(res);
    }
}
