package ru.zenclass.ylab.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.mapper.PlayerMapper;

import java.math.BigDecimal;

@RestController
public class MainController {

    private final PlayerMapper playerMapper;

    public MainController(PlayerMapper playerMapper) {
        this.playerMapper = playerMapper;
    }


    @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlayerDTO> sayHello() {
        Player player = new Player();
        player.setUsername("Merdan");
        player.setBalance(new BigDecimal(100));
        player.setPassword("5555666666");
        return ResponseEntity.ok(PlayerMapper.INSTANCE.toDTO(player));
    }
}
