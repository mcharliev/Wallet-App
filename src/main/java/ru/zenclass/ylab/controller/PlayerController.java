package ru.zenclass.ylab.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zenclass.ylab.aop.annotation.Loggable;
import ru.zenclass.ylab.model.dto.LoginResponseDTO;
import ru.zenclass.ylab.model.dto.PlayerBalanceDTO;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.service.PlayerService;

@RestController
@RequestMapping("/players")
@Tag(name = "Управление игроками")
@Loggable
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового игрока")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Игрок успешно создан"),
            @ApiResponse(responseCode = "409", description = "Игрок с таким именем уже существует"),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные (не прошли валидацию)"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<PlayerDTO> register(
            @Parameter(description = "Детали регистрации игрока", required = true)
            @RequestBody RegisterPlayerDTO registerPlayerDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(playerService.registerNewPlayer(registerPlayerDTO));
    }

    @PostMapping("/login")
    @Operation(summary = "Аутентификация игрока и генерация токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Игрок успешно аутентифицирован"),
            @ApiResponse(responseCode = "401", description = "Учетные данные игрока неверны, или токен JWT неверен или истек"),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные (не прошли валидацию)"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<LoginResponseDTO> login(
            @Parameter(description = "Данные для входа игрока", required = true)
            @RequestBody RegisterPlayerDTO registerPlayerDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(playerService.authenticateAndGenerateToken(registerPlayerDTO));
    }

    @GetMapping("/balance")
    @Operation(summary = "Получение баланса аутентифицированного игрока")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс игрока успешно получен"),
            @ApiResponse(responseCode = "401", description = "Учетные данные игрока неверны, или токен JWT неверен или истек"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<PlayerBalanceDTO> getBalance(
            @Parameter(description = "Аутентифицированный игрок", required = true)
            @RequestAttribute("authenticatedPlayer") Player authenticatedPlayer) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(playerService.getPlayerBalanceInfo(authenticatedPlayer));
    }
}
