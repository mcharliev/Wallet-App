package ru.zenclass.ylab.service;


import jakarta.validation.ConstraintViolation;
import ru.zenclass.ylab.exception.AuthenticationException;
import ru.zenclass.ylab.exception.ConflictException;
import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.exception.ValidationException;
import ru.zenclass.ylab.model.dto.LoginResponseDTO;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.mapper.PlayerMapper;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.util.JwtUtil;
import ru.zenclass.ylab.validator.RegisterPlayerValidator;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для управления данными игрока.
 * Этот сервис предоставляет методы для выполнения основных операций, таких как поиск, обновление, регистрация и вход.
 */

public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final RegisterPlayerValidator registerPlayerValidator = new RegisterPlayerValidator();
    private final JwtUtil jwtUtil = new JwtUtil();

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player findPlayerById(Long id) {
        return playerRepository.findPlayerById(id).orElseThrow(PlayerNotFoundException::new);
    }

    public void updatePlayer(Player updatedPlayer) {
        playerRepository.updatePlayer(updatedPlayer);
    }

    public Optional<Player> registerPlayer(String username, String password) {
        return playerRepository.findPlayerByUsername(username)
                .or(() -> {
                    Player player = new Player(username, password);
                    playerRepository.addPlayer(player);
                    return Optional.of(player);
                });
    }

    public Optional<Player> login(String username, String password) {
        return authenticatePlayer(username, password);
    }

    @Override
    public Optional<Player> findPlayerByUsername(String username) {
        return playerRepository.findPlayerByUsername(username);
    }

    @Override
    public String getPlayerBalanceInfo(Player player) {
        return null;
    }

    private Optional<Player> authenticatePlayer(String username, String password) {
        return playerRepository.findPlayerByUsername(username)
                .filter(player -> player.getPassword().equals(password));
    }

    public PlayerDTO registerNewPlayer(RegisterPlayerDTO registerPlayerDTO) {
        validate(registerPlayerDTO);
        return registerPlayer(registerPlayerDTO.getUsername(), registerPlayerDTO.getPassword())
                .map(PlayerMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new ConflictException("Игрок уже существует"));
    }

    private void validate(RegisterPlayerDTO registerPlayerDTO) {
        Set<ConstraintViolation<RegisterPlayerDTO>> violations = registerPlayerValidator.validate(registerPlayerDTO);
        if (!violations.isEmpty()) {
            throw new ValidationException(formatViolations(violations));
        }
    }

    private String formatViolations(Set<ConstraintViolation<RegisterPlayerDTO>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(". "));
    }

    public LoginResponseDTO authenticateAndGenerateToken(String username, String password) {
        Player player = authenticatePlayer(username, password).orElseThrow(() ->
                new AuthenticationException("Неверный логин или пароль, попробуйте ввести данные снова"));
        String token = jwtUtil.generateToken(player.getUsername());
        PlayerDTO playerDTO = PlayerMapper.INSTANCE.toDTO(player);
        return new LoginResponseDTO(playerDTO, token);
    }
}

