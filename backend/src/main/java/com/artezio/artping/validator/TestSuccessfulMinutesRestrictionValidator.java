package com.artezio.artping.validator;

import com.artezio.artping.dto.SystemSettingsDto;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class TestSuccessfulMinutesRestrictionValidator implements ConstraintValidator<TestSuccessfulMinutesRestriction, SystemSettingsDto> {

    @Override
    public boolean isValid(SystemSettingsDto dto, ConstraintValidatorContext cvc) {
        return dto.getTestNoResponseMinutes() > dto.getTestSuccessfulMinutes();
    }
}

