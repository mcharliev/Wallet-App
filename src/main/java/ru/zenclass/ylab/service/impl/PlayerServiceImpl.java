package ru.zenclass.ylab.service.impl;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.validation.ConstraintViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.zenclass.ylab.exception.AuthenticationException;
import ru.zenclass.ylab.exception.PlayerAlreadyExistException;
import ru.zenclass.ylab.exception.PlayerNotFoundException;
import ru.zenclass.ylab.exception.ValidationException;
import ru.zenclass.ylab.model.dto.LoginResponseDTO;
import ru.zenclass.ylab.model.dto.PlayerBalanceDTO;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.dto.RegisterPlayerDTO;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.model.mapper.PlayerMapper;
import ru.zenclass.ylab.repository.PlayerRepository;
import ru.zenclass.ylab.service.PlayerService;
import ru.zenclass.ylab.util.DTOValidator;
import ru.zenclass.ylab.util.JwtUtil;

import java.util.Optional;
import java.util.Set;

/**
 * Сервис для управления данными игрока.
 * Этот сервис предоставляет методы для выполнения основных операций, таких как поиск, обновление, регистрация и вход.
 */
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final DTOValidator<RegisterPlayerDTO> registerPlayerValidator;
    private final JwtUtil jwtUtil;


    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository, DTOValidator<RegisterPlayerDTO> registerPlayerDTOValidator, JwtUtil jwtUtil) {
        this.playerRepository = playerRepository;
        this.registerPlayerValidator = registerPlayerDTOValidator;
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
            throw new PlayerAlreadyExistException();
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
    public LoginResponseDTO authenticateAndGenerateToken(RegisterPlayerDTO registerPlayerDTO) {
        Player player = authenticatePlayer(registerPlayerDTO.getUsername(),
                registerPlayerDTO.getPassword()).orElseThrow(AuthenticationException::new);
        String token = jwtUtil.generateToken(player.getUsername());
        PlayerDTO playerDTO = PlayerMapper.INSTANCE.toDTO(player);
        return new LoginResponseDTO(playerDTO, token);
    }


    @Override
    public PlayerBalanceDTO getPlayerBalanceInfo(Player player) {
        return new PlayerBalanceDTO(player.getUsername(), player.getBalance());
    }


    @Override
    public Optional<Player> validateTokenAndGetPlayer(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Optional.empty();
        }
        token = token.substring(7);
        try {
            String username = jwtUtil.extractUsername(token);
            if (!jwtUtil.validateToken(token, username)) {
                return Optional.empty();
            }
            return findPlayerByUsername(username);
        } catch (SignatureException | ExpiredJwtException ex) {
            return Optional.empty();
        }
    }

    private Optional<Player> authenticatePlayer(String username, String password) {
        return playerRepository.findPlayerByUsername(username)
                .filter(player -> player.getPassword().equals(password));
    }


    private void validate(RegisterPlayerDTO registerPlayerDTO) {
        Set<ConstraintViolation<RegisterPlayerDTO>> violations = registerPlayerValidator.validate(registerPlayerDTO);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<RegisterPlayerDTO> violation : violations) {
                sb.append(violation.getMessage()).append(". ");
            }
            throw new ValidationException(sb.toString(), violations);
        }
    }
}

