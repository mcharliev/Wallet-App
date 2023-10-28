package ru.zenclass.ylab.model.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.zenclass.ylab.model.dto.PlayerDTO;
import ru.zenclass.ylab.model.entity.Player;


public class PlayerValidator implements Validator {
    private final PlayerDTO playerDTO;


    public PlayerValidator(PlayerDTO playerDTO) {
        this.playerDTO = playerDTO;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Player.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Player player = (Player) target;
    }
}
