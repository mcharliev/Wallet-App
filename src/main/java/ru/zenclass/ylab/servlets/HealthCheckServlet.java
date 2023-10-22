package ru.zenclass.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.aop.annotations.Loggable;
import ru.zenclass.ylab.model.dto.HealthResponseDto;
import ru.zenclass.ylab.service.HealthCheckService;

import java.io.IOException;

@Loggable
@WebServlet("/health")
public class HealthCheckServlet extends HttpServlet {

    private final ObjectMapper objectMapper;
    private final HealthCheckService healthCheckService;

    public HealthCheckServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.healthCheckService = new HealthCheckService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(this.objectMapper.writeValueAsBytes(new HealthResponseDto(healthCheckService.getApplicationStatus())));
    }
}
