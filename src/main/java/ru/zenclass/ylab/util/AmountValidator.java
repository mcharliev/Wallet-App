package ru.zenclass.ylab.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;
import ru.zenclass.ylab.exception.ValidationException;
import ru.zenclass.ylab.model.dto.AmountDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;

import java.util.Set;

@Component
public class AmountValidator implements DTOValidator<AmountDTO> {
    private final Validator validator;

    public AmountValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public Set<ConstraintViolation<AmountDTO>> validate(AmountDTO amountDTO) {
        return validator.validate(amountDTO);
    }
}
