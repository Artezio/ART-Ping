package com.artezio.artping.service.mapper;


import com.artezio.artping.dto.office.OfficeDto;
import com.artezio.artping.dto.office.request.CreateOfficeRequest;
import com.artezio.artping.dto.office.response.OfficeResponse;
import com.artezio.artping.entity.office.Office;
import com.artezio.artping.service.mapper.converter.StringToUuidConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;


@Component
public class OfficeMapping implements OrikaMapperFactoryConfigurer {
  public static final String STRING_TO_UUID_CONVERTER = "stringToUuidConverter";

  @Override
  public void configure(MapperFactory orikaMapperFactory) {
    ConverterFactory converterFactory = orikaMapperFactory.getConverterFactory();

    converterFactory.registerConverter(STRING_TO_UUID_CONVERTER, new StringToUuidConverter());

    orikaMapperFactory.classMap(CreateOfficeRequest.class, Office.class)
        .constructorB("name", "calendarId", "timezone")
        .fieldMap("name", "name").add()
        .fieldMap("calendarId", "calendarId").converter(STRING_TO_UUID_CONVERTER).add()
        .fieldMap("timezone", "timezone").add()
        .register();
    orikaMapperFactory.classMap(Office.class, OfficeResponse.class)
        .constructorB("name", "calendarId", "timezone")

        .fieldMap("name", "name").add()
        .fieldMap("id", "id").converter(STRING_TO_UUID_CONVERTER).add()
        .fieldMap("calendarId", "calendarId").converter(STRING_TO_UUID_CONVERTER).add()
        .fieldMap("timezone", "timezone").add()
        .register();

    orikaMapperFactory.classMap(Office.class, OfficeDto.class)
        .constructorA("name", "calendarId", "timezone", "id")
        .constructorB("name", "calendarId", "timezone", "id")
        .fieldMap("id", "id").converter(STRING_TO_UUID_CONVERTER).add()
        .fieldMap("calendarId", "calendarId").converter(STRING_TO_UUID_CONVERTER).add()
        .fieldMap("name", "name").add()
        .fieldMap("timezone", "timezone").add()
        .fieldMap("managingEmployees{id}", "directorIds{}").converter(STRING_TO_UUID_CONVERTER).add()
        .register();
  }
}
