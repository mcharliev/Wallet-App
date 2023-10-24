package ru.zenclass.ylab.validator;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;

import java.io.IOException;
import java.util.Set;

public class ErrorResponseHandler {

    public void handleValidationErrors(Set<ConstraintViolation<RegisterPlayerDTO>> violations, HttpServletResponse resp) throws IOException {
        StringBuilder errorMessage = new StringBuilder("Ошибка валидации: ");
        for (ConstraintViolation<RegisterPlayerDTO> violation : violations) {
            errorMessage.append(violation.getMessage()).append(". ");
        }
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().write(errorMessage.toString());
    }
}

