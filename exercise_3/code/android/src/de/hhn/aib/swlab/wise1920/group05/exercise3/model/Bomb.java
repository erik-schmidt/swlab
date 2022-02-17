package de.hhn.aib.swlab.wise1920.group05.exercise3.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.Calendar;

public class Bomb {

    public static final int RADIUS = 20;                //Explosion Radius
    private long time;                                  //Time in milliseconds when bomb was placed
    public static final int EXPLOSION_DELAY = 5000;     //Time between bomb setting and explosion in milliseconds
    private final Vector2 position = new Vector2();
    public static float HEIGHT;
    public static float WIDTH;
    private final Sprite bombSprite;

    public Bomb() {
        setTime();
        bombSprite = new Sprite(new Texture(Gdx.files.internal("bomb.png")));
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public long getTime() {
        return time;
    }

    public void setTime() {
        this.time = Calendar.getInstance().getTimeInMillis();
    }

    public Sprite getBombSprite() {
        return bombSprite;
    }

}
