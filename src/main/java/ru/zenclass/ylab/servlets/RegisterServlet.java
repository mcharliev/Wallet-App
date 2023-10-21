package ru.zenclass.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.connection.DatabaseConnectionManager;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.mapper.PlayerMapper;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.repository.PlayerRepositoryImpl;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.service.PlayerServiceImpl;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private PlayerService playerService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() {
        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager();
        PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionManager);
        this.playerService = new PlayerServiceImpl(playerRepository);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        RegisterPlayerDTO registerPlayerDTO = mapper.readValue(req.getReader(), RegisterPlayerDTO.class);
        Player playerEntity = PlayerMapper.INSTANCE.toPlayerEntity(registerPlayerDTO);
        Optional<Player> registeredPlayerOpt = playerService.registerPlayer(playerEntity.getUsername(), playerEntity.getPassword());
        if (registeredPlayerOpt.isPresent()) {
            Player registeredPlayer = registeredPlayerOpt.get();
            PlayerDTO playerDTO = PlayerMapper.INSTANCE.toDTO(registeredPlayer);
            String jsonResponse = String.format(
                    "{ \"message\": \"Пользователь '%s' успешно зарегистрирован\", \"player\": %s }",
                    playerDTO.getUsername(),
                    mapper.writeValueAsString(playerDTO));
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(jsonResponse);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Пользователь с таким именем уже существует. Пожалуйста, выберите другое имя");
        }
    }
}
