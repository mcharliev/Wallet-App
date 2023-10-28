package ru.zenclass.ylab.service.impl;


import jakarta.validation.ConstraintViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.model.util.JwtUtil;
import ru.zenclass.ylab.validator.RegisterPlayerValidator;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для управления данными игрока.
 * Этот сервис предоставляет методы для выполнения основных операций, таких как поиск, обновление, регистрация и вход.
 */
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final RegisterPlayerValidator registerPlayerValidator;
    private final JwtUtil jwtUtil;

    /**
     * Конструктор класса PlayerServiceImpl.
     *
     * @param playerRepository        Репозиторий игроков, см. {@link PlayerRepository}.
     * @param registerPlayerValidator Валидатор регистрации игрока, см. {@link RegisterPlayerValidator}.
     * @param jwtUtil                 Утилита для работы с JWT-токенами, см. {@link JwtUtil}.
     */
    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository, RegisterPlayerValidator registerPlayerValidator, JwtUtil jwtUtil) {
        this.playerRepository = playerRepository;
        this.registerPlayerValidator = registerPlayerValidator;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public Player findPlayerById(Long id) {
        return playerRepository.findPlayerById(id).orElseThrow(PlayerNotFoundException::new);
    }

    @Override
    public void updatePlayer(Player updatedPlayer) {
        playerRepository.updatePlayer(updatedPlayer);
    }


    @Override
    public PlayerDTO registerNewPlayer(RegisterPlayerDTO registerPlayerDTO) {
        validate(registerPlayerDTO);

        Optional<Player> existingPlayer = playerRepository.findPlayerByUsername(registerPlayerDTO.getUsername());
        if (existingPlayer.isPresent()) {
            throw new ConflictException("Игрок уже существует");
        }

        Player player = new Player(registerPlayerDTO.getUsername(), registerPlayerDTO.getPassword());
        playerRepository.addPlayer(player);
        return PlayerMapper.INSTANCE.toDTO(player);
    }


    @Override
    public Optional<Player> findPlayerByUsername(String username) {
        return playerRepository.findPlayerByUsername(username);
    }


    @Override
    public String getPlayerBalanceInfo(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Игрок не должен быть null");
        }
        BigDecimal playerBalance = player.getBalance();
        return String.format("{\"username\": \"%s\", \"balance\": \"%s\"}", player.getUsername(), playerBalance.toPlainString());
    }


    @Override
    public LoginResponseDTO authenticateAndGenerateToken(String username, String password) {
        Player player = authenticatePlayer(username, password).orElseThrow(() ->
                new AuthenticationException("Неверный логин или пароль, попробуйте ввести данные снова"));
        String token = jwtUtil.generateToken(player.getUsername());
        PlayerDTO playerDTO = PlayerMapper.INSTANCE.toDTO(player);
        return new LoginResponseDTO(playerDTO, token);
    }

    /**
     * Валидирует данные регистрации игрока.
     *
     * @param registerPlayerDTO Данные регистрации игрока, см. {@link RegisterPlayerDTO}.
     * @throws ValidationException если данные не соответствуют правилам валидации
     */
    private void validate(RegisterPlayerDTO registerPlayerDTO) {
        Set<ConstraintViolation<RegisterPlayerDTO>> violations = registerPlayerValidator.validate(registerPlayerDTO);
        if (!violations.isEmpty()) {
            throw new ValidationException(formatViolations(violations));
        }
    }

    /**
     * Форматирует сообщения об ошибках валидации.
     *
     * @param violations набор нарушений правил валидации, см. {@link ConstraintViolation}
     * @return строка с объединенными сообщениями об ошибках
     */
    private String formatViolations(Set<ConstraintViolation<RegisterPlayerDTO>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(". "));
    }

    /**
     * Аутентифицирует игрока по его логину и паролю.
     *
     * @param username логин игрока
     * @param password пароль игрока
     * @return {@link Optional} объекта игрока, если аутентификация прошла успешно, иначе пустой {@link Optional}
     * @throws AuthenticationException если аутентификация не удалась
     */
    private Optional<Player> authenticatePlayer(String username, String password) {
        return playerRepository.findPlayerByUsername(username)
                .filter(player -> player.getPassword().equals(password));
    }
}
