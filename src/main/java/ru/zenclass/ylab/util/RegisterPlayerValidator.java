package ru.zenclass.ylab.util;

import org.springframework.stereotype.Component;
import ru.zenclass.ylab.aop.annotation.Loggable;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

/**
 * Класс-валидатор для проверки данных, предоставляемых при регистрации игрока.
 * Использует валидацию, основанную на аннотациях, для проверки корректности данных.
 */
@Component
@Loggable
public class RegisterPlayerValidator implements DTOValidator<RegisterPlayerDTO> {
    private final Validator validator;

    /**
     * Конструктор создает экземпляр валидатора с использованием настроек по умолчанию.
     */
    public RegisterPlayerValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * Выполняет валидацию объекта {@link RegisterPlayerDTO}, проверяя корректность данных
     * согласно определенным аннотациям в классе {@link RegisterPlayerDTO}.
     *
     * @param registerPlayerDTO объект, который требуется проверить на валидность
     * @return множество нарушений ограничений (constraints), если они есть, иначе пустое множество
     */
    public Set<ConstraintViolation<RegisterPlayerDTO>> validate(RegisterPlayerDTO registerPlayerDTO) {
        return validator.validate(registerPlayerDTO);
    }
}

