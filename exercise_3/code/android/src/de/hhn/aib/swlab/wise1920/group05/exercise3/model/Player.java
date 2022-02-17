package de.hhn.aib.swlab.wise1920.group05.exercise3.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Player extends User{

    public static float HEIGHT;
    public static float WIDTH;
    private final Vector2 position = new Vector2();
    private boolean alive = true;
    private Texture playerSprite;


    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Texture getPlayerSprite() {
        return playerSprite;
    }

    public void setPlayerSprite(Texture playerSprite) {
        this.playerSprite = playerSprite;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }
}

