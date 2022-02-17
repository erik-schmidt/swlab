package de.hhn.aib.swlab.wise1920.group05.exercise3.model;

import java.util.List;

public class GameMessage {

    private String action;
    private String authentication;
    private String gameId;
    private List<String> games;
    private float x, y;
    private MessageType messageType;
    private String playerName;

    public GameMessage(String action) {
        this.action = action;
    }

    public GameMessage() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public enum MessageType {
        POSITION_UPDATE, OTHER_POSITION_UPDATE, BOMB_UPDATE,
        START_GAME, END_GAME, JOINED, ERROR, LEAVE_LOBBY, START_POS
    }

    public List<String> getGames() {
        return games;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
