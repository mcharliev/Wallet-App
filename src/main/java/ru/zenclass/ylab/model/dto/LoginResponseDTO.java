package ru.zenclass.ylab.model.dto;

public class LoginResponseDTO {

    private PlayerDTO player;
    private String token;
    private String message;

    public LoginResponseDTO(PlayerDTO player, String token) {
        this.player = player;
        this.token = token;
        this.message = String.format("Пользователь '%s' совершил успешный вход", player.getUsername());
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
