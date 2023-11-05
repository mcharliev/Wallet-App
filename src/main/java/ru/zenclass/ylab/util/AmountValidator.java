package ru.zenclass.ylab.util;

import org.springframework.stereotype.Component;
import ru.zenclass.ylab.aop.annotation.Loggable;
import ru.zenclass.ylab.model.dto.AmountDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

/**
 * Компонент для валидации объектов типа {@link AmountDTO}.
 */
@Component
@Loggable
public class AmountValidator implements DTOValidator<AmountDTO> {
    private final Validator validator;

    /**
     * Конструктор класса {@code AmountValidator}.
     * Создает экземпляр валидатора для валидации объектов типа {@link AmountDTO}.
     */
    public AmountValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * Выполняет валидацию объекта {@link AmountDTO}.
     *
     * @param amountDTO объект, который нужно валидировать, тип {@link AmountDTO}.
     * @return набор нарушений ограничений валидации
     */
    public Set<ConstraintViolation<AmountDTO>> validate(AmountDTO amountDTO) {
        return validator.validate(amountDTO);
    }
}
