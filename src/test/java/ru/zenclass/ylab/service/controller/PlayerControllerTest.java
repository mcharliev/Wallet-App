package ru.zenclass.ylab.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.zenclass.ylab.controller.PlayerController;
import ru.zenclass.ylab.controller.advice.ExceptionHandlerAdvice;
import ru.zenclass.ylab.exception.PlayerAlreadyExistException;
import ru.zenclass.ylab.model.dto.LoginResponseDTO;
import ru.zenclass.ylab.model.dto.PlayerBalanceDTO;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.util.JwtInterceptor;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
@ExtendWith(SpringExtension.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;


    @MockBean
    private JwtInterceptor jwtInterceptor;

    private RegisterPlayerDTO registerPlayerDTO;
    private PlayerDTO playerDTO;
    private LoginResponseDTO loginResponseDTO;
    private Player authenticatedPlayer;
    private PlayerBalanceDTO playerBalanceDTO;


    @BeforeEach
    public void setUp() {
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


        Mockito.when(jwtInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Object.class)))
                .thenAnswer(invocation -> {
                    HttpServletRequest request = invocation.getArgument(0);
                    request.setAttribute("authenticatedPlayer", authenticatedPlayer);
                    return true;
                });

        mockMvc = MockMvcBuilders
                .standaloneSetup(new PlayerController(playerService))
                .setControllerAdvice(new ExceptionHandlerAdvice())
                .addInterceptors(jwtInterceptor)
                .build();
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