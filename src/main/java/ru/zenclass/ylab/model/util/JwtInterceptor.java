package ru.zenclass.ylab.model.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.zenclass.ylab.exception.JwtException;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.service.PlayerService;

import java.util.Optional;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final PlayerService playerService;

    public JwtInterceptor(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)   {
        Optional<Player> playerOpt = playerService.validateTokenAndGetPlayer(request.getHeader("Authorization"));
        if (playerOpt.isEmpty()) {
          throw new JwtException();
        }
        request.setAttribute("authenticatedPlayer", playerOpt.get());
        return true;
    }
}

