package ru.zenclass.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;

import java.io.IOException;
import java.util.Set;

/**
 * Базовый сервлет для обработки регистрации и авторизации игрока.
 * Этот абстрактный класс содержит общую логику и утилиты для работы с данными регистрации и авторизации игроков.
 */
public abstract class BasicRegLogServlet extends HttpServlet {
    protected final ObjectMapper mapper = new ObjectMapper();
    protected Validator validator;

    /**
     * Инициализирует сервлет, настраивая валидатор.
     */
    @Override
    public void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Извлекает и валидирует данные для регистрации игрока из запроса.
     * @param req  Запрос от клиента.
     * @param resp Ответ клиенту.
     * @return Данные для регистрации игрока {@link RegisterPlayerDTO}, если валидация прошла успешно; иначе {@code null}.
     * @throws IOException В случае ошибок ввода-вывода.
     */
    protected RegisterPlayerDTO getAndValidatePlayerDTO(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        RegisterPlayerDTO registerPlayerDTO = mapper.readValue(req.getReader(), RegisterPlayerDTO.class);
        Set<ConstraintViolation<RegisterPlayerDTO>> violations = validator.validate(registerPlayerDTO);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Ошибка валидации: ");
            for (ConstraintViolation<RegisterPlayerDTO> violation : violations) {
                errorMessage.append(violation.getMessage()).append(". ");
            }
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(errorMessage.toString());
            return null;
        }
        return registerPlayerDTO;
    }
}

