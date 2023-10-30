package ru.zenclass.ylab.util;

import jakarta.validation.ConstraintViolation;

import java.util.Set;

public interface DTOValidator<T> {
    Set<ConstraintViolation<T>> validate(T dto);
}
