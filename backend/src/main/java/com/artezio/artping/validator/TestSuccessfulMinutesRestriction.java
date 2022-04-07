package com.artezio.artping.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TestSuccessfulMinutesRestrictionValidator.class)
public @interface TestSuccessfulMinutesRestriction {

    String message() default "Допустимое время ответа на проверку должно быть меньше времени ожидания ответа на проверку";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
