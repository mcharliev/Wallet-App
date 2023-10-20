package ru.zenclass.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.entity.dto.PlayerDTO;
import ru.zenclass.ylab.model.mapper.PlayerMapper;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.PlayerRepositoryImpl;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.PlayerServiceImpl;

import java.io.IOException;

@NoArgsConstructor
@WebServlet(name = "PlayerServlet", urlPatterns = {"/player"})
public class PlayerServlet extends HttpServlet {
    private PlayerService playerService;

    @Override
    public void init() {
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionManager);
        this.playerService = new PlayerServiceImpl(playerRepository);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Парсим JSON из запроса
        PlayerDTO playerDTO = new ObjectMapper().readValue(req.getInputStream(), PlayerDTO.class);
        // Преобразуем DTO в сущность
        Player playerEntity = PlayerMapper.INSTANCE.toEntity(playerDTO);
        // Сохраняем сущность
        playerService.save(playerEntity);
        // Установить статус ответа на 201 Created
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Missing or empty id parameter");
            return;
        }

        try {
            Long id = Long.parseLong(idParam); // Преобразование строки в Long

            // Ищем игрока по id
            Player player = playerService.findPlayerById(id);

            if (player == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Player not found");
                return;
            }

            // Преобразовать игрока в DTO
            PlayerDTO playerDTO = PlayerMapper.INSTANCE.toDTO(player);

            // Преобразовать DTO в JSON
            String jsonResponse = new ObjectMapper().writeValueAsString(playerDTO);

            // Установить тип содержимого ответа и кодировку
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            // Отправить JSON-ответ
            resp.getWriter().write(jsonResponse);

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid id format");
        }
    }
}
