package com.artezio.artping.service.utils;

import java.time.LocalTime;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class LocalTimeConverter implements Converter<String, LocalTime> {

  @Override
  public LocalTime convert(@NonNull String source) {
    return LocalTime.parse(source);
  }
}
