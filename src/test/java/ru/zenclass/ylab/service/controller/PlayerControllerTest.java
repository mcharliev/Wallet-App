package ru.zenclass.ylab.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.zenclass.ylab.configuration.AppConfig;
import ru.zenclass.ylab.controller.PlayerController;
import ru.zenclass.ylab.controller.advice.ExceptionHandlerAdvice;
import ru.zenclass.ylab.exception.PlayerAlreadyExistException;
import ru.zenclass.ylab.model.dto.LoginResponseDTO;
import ru.zenclass.ylab.model.dto.PlayerBalanceDTO;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.service.PlayerService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {AppConfig.class})
public class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    private MockMvc mockMvc;
    private RegisterPlayerDTO registerPlayerDTO;
    private PlayerDTO playerDTO;
    private LoginResponseDTO loginResponseDTO;
    private Player authenticatedPlayer;
    private PlayerBalanceDTO playerBalanceDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(playerController)
                .setControllerAdvice(new ExceptionHandlerAdvice())
                .build();

        registerPlayerDTO = new RegisterPlayerDTO();
        registerPlayerDTO.setUsername("user");
        registerPlayerDTO.setPassword("pass");

        playerDTO = new PlayerDTO();
        playerDTO.setUsername(registerPlayerDTO.getUsername());

        loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setPlayer(playerDTO);
        loginResponseDTO.setToken("token");

        authenticatedPlayer = new Player("user", "pass");
        playerBalanceDTO = new PlayerBalanceDTO("user", new BigDecimal(100));
    }

    @Test
    void registerNewPlayer() throws Exception {
        given(playerService.registerNewPlayer(any(RegisterPlayerDTO.class))).willReturn(playerDTO);

        mockMvc.perform(post("/players/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerPlayerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(playerDTO.getUsername()));
    }

    @Test
    void registerExistingPlayer() throws Exception {
        doThrow(new PlayerAlreadyExistException()).when(playerService).registerNewPlayer(any(RegisterPlayerDTO.class));

        mockMvc.perform(post("/players/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerPlayerDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void authenticatePlayerAndGenerateToken() throws Exception {
        given(playerService.authenticateAndGenerateToken(any(RegisterPlayerDTO.class))).willReturn(loginResponseDTO);

        mockMvc.perform(post("/players/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerPlayerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(loginResponseDTO.getToken()));
    }

    @Test
    void getBalanceForAuthenticatedPlayer() throws Exception {
        given(playerService.getPlayerBalanceInfo(authenticatedPlayer)).willReturn(playerBalanceDTO);

        mockMvc.perform(get("/players/balance")
                        .requestAttr("authenticatedPlayer", authenticatedPlayer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(playerBalanceDTO.getBalance()));
    }
}