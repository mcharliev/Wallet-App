package ru.zenclass.ylab.util;

import javax.validation.ConstraintViolation;

import java.util.Set;

public interface DTOValidator<T> {
    Set<ConstraintViolation<T>> validate(T dto);
}
