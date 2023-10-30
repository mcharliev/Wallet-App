package ru.zenclass.ylab.util;

import org.springframework.stereotype.Component;
import ru.zenclass.ylab.model.dto.AmountDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
