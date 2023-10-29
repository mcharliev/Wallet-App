package ru.zenclass.ylab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zenclass.ylab.model.dto.LoginResponseDTO;
import ru.zenclass.ylab.model.dto.PlayerBalanceDTO;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.service.PlayerService;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/register")
    public ResponseEntity<PlayerDTO> register(@RequestBody RegisterPlayerDTO registerPlayerDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(playerService.registerNewPlayer(registerPlayerDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody RegisterPlayerDTO registerPlayerDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(playerService.authenticateAndGenerateToken(registerPlayerDTO));
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@RequestAttribute("authenticatedPlayer") Player authenticatedPlayer) {
        PlayerBalanceDTO balanceInfo = playerService.getPlayerBalanceInfo(authenticatedPlayer);
        return ResponseEntity.ok(balanceInfo);
    }
}
