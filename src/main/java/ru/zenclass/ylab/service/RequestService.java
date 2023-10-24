package ru.zenclass.ylab.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.zenclass.ylab.model.dto.AmountDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public class RequestService {

    private ObjectMapper mapper = new ObjectMapper();
    private Validator validator;

    public RequestService() {
        this.validator = initValidator();
    }

    public Optional<BigDecimal> getAmountFromRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            AmountDTO amountDTO = mapper.readValue(req.getReader(), AmountDTO.class);
            Set<ConstraintViolation<AmountDTO>> violations = validator.validate(amountDTO);
            if (!violations.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Некорректное значение суммы\"}");
                return Optional.empty();
            }
            return Optional.of(amountDTO.getAmount());
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Ошибка чтения данных\"}");
            return Optional.empty();
        }
    }

    private Validator initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }
}
