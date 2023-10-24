package ru.zenclass.ylab.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;

import java.util.Set;

public class RegisterPlayerValidator {
    private final Validator validator;

    public RegisterPlayerValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public Set<ConstraintViolation<RegisterPlayerDTO>> validate(RegisterPlayerDTO registerPlayerDTO) {
        return validator.validate(registerPlayerDTO);
    }
}

