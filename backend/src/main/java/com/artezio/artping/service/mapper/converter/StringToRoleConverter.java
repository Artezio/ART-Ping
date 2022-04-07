package com.artezio.artping.service.mapper.converter;

import com.artezio.artping.data.repository.RoleRepository;
import com.artezio.artping.entity.user.Role;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component("stringToRoleConverter")
@AllArgsConstructor
public final class StringToRoleConverter extends BidirectionalConverter<String, Set<Role>> {
    private static final String DELIMITER = ";";

    private final RoleRepository roleRepository;

    @Override
    public Set<Role> convertTo(String source, Type<Set<Role>> destinationType, MappingContext mappingContext) {
        if (isBlank(source)) return Collections.emptySet();
        return Stream.of(source.split(DELIMITER))
                .map(roleRepository::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public String convertFrom(Set<Role> source, Type<String> destinationType, MappingContext mappingContext) {
        if (isEmpty(source)) return "";
        return source.stream()
                .map(Role::getName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(DELIMITER));
    }
}
