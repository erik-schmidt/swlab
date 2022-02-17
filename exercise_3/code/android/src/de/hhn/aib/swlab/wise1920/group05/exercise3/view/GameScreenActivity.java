package de.hhn.aib.swlab.wise1920.group05.exercise3.view;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.gson.Gson;

import java.net.SocketException;
import java.util.Calendar;
import java.util.Iterator;

import de.hhn.aib.swlab.wise1920.group05.exercise3.model.Bomb;
import de.hhn.aib.swlab.wise1920.group05.exercise3.model.GameMessage;
import de.hhn.aib.swlab.wise1920.group05.exercise3.model.Player;
import de.hhn.aib.swlab.wise1920.group05.exercise3.network.MessageListener;
import de.hhn.aib.swlab.wise1920.group05.exercise3.network.WebsocketService;

public class GameScreenActivity extends Game implements InputProcessor, MessageListener {
    private Player player;
    private Player opponent;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private TiledMap tiledMap;
    private final float unitScale = 1 / 20f; //Kachelgröße der Map 20x20
    private OrthogonalTiledMapRenderer renderer;
    private Sprite playerSprite;
    private Bomb bomb1;
    private Bomb bomb2;
    private Bomb bomb3;
    private int bombCounter = 0;
    private final Array<Bomb> bombsPlayer1 = new Array<>();
    private final Array<Bomb> bombsPlayer2 = new Array<>();
    private final Array<ParticleEffect> explosions = new Array<>();
    private WebsocketService websocketService;
    private boolean serviceBound = false;
    private final Gson gson = new Gson();
    private MyGameCallBack myGameCallBack;
    private Viewport viewport;
    private int mapPixelWidth;
    private int mapPixelHeight;
    private Sound bombSound;
    private Music backgroundMusic;

    public interface MyGameCallBack {
        void noConnection(String message);
        void bindServices(ServiceConnection connection);
        void loseGame();
        void winGame();
    }

    public void setMyGameCallBack(MyGameCallBack myGameCallBack) {
        this.myGameCallBack = myGameCallBack;
    }

    @Override
    public void create() {
        //Effect for bomb explosion
        ParticleEffect explosion = new ParticleEffect();
        explosion.load(Gdx.files.internal("effects/Bomb explosion.p"), Gdx.files.internal("effects"));

        // Set Spritebatch to load the Player Sprite
        batch = new SpriteBatch();

        // register Input Processor for touch events
        Gdx.input.setInputProcessor(this);

        //Load Tilted Map from files (1 unit == 20 px)
        tiledMap = new TmxMapLoader().load("bomberman.tmx");
        MapProperties mapProps = tiledMap.getProperties();

        int mapWidth = mapProps.get("width", Integer.class);
        int mapHeight = mapProps.get("height", Integer.class);
        int tilePixelWidth = mapProps.get("tilewidth", Integer.class);
        int tilePixelHeight = mapProps.get("tileheight", Integer.class);

        mapPixelWidth = mapWidth * tilePixelWidth;
        mapPixelHeight = mapHeight * tilePixelHeight;
        renderer = new OrthogonalTiledMapRenderer(tiledMap);

        //Create Orthographic Camera with unit scale of 20x20
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, mapWidth, mapHeight);
        viewport = new StretchViewport(mapPixelWidth, mapPixelHeight, camera);
        viewport.apply();

        //create Bombs
        bomb1 = new Bomb();
        bomb2 = new Bomb();
        bomb3 = new Bomb();

        //create Player
        player = new Player();
        player.setPlayerSprite(new Texture(Gdx.files.internal("Mons 1.png")));
        player.setPosition(0, 0);
        playerSprite = new Sprite(player.getPlayerSprite());

        Player.WIDTH = unitScale * playerSprite.getWidth();
        Player.HEIGHT = unitScale * playerSprite.getHeight();

        //create Player 2
        opponent = new Player();
        opponent.setPlayerSprite(new Texture(Gdx.files.internal("Mons 2.png")));
        opponent.setPosition(0, 0);

        bombSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("orbital.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.play();
        myGameCallBack.bindServices(connection);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 20, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set Cameraview to whole map and render it
        renderer.setView(camera);
        renderer.render();
        camera.update();

        //Update Bombs and Players
        checkBombExplosion(bombsPlayer1);
        checkBombExplosion(bombsPlayer2);
        if (websocketService != null) {
            updatePlayer();
        }

        // Render Batch to map
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        Iterator<ParticleEffect> effectIterator = explosions.iterator();
        while (effectIterator.hasNext()) {
            ParticleEffect explosion = effectIterator.next();
            explosion.draw(batch, Gdx.graphics.getDeltaTime());
            bombSound.play();
            if (explosion.isComplete()) {
                effectIterator.remove();
            }
        }
        batch.draw(playerSprite, player.getPosition().x, player.getPosition().y, Player.WIDTH, Player.HEIGHT);
        batch.draw(opponent.getPlayerSprite(), opponent.getPosition().x, opponent.getPosition().y, Player.WIDTH, Player.HEIGHT);
        for (Bomb bomb : bombsPlayer1) {
            if (bombsPlayer1.size != 0) {
                batch.draw(bomb.getBombSprite(), bomb.getPosition().x, bomb.getPosition().y, Bomb.WIDTH, Bomb.HEIGHT);
            }
        }
        for (Bomb bomb : bombsPlayer2) {
            if (bombsPlayer2.size != 0) {
                batch.draw(bomb.getBombSprite(), bomb.getPosition().x, bomb.getPosition().y, Bomb.WIDTH, Bomb.HEIGHT);
            }
        }
        batch.end();
    }


    private void updatePlayer() {
        if (!player.isAlive()) {
            try {
                GameMessage gameOverMessage = new GameMessage();
                gameOverMessage.setMessageType(GameMessage.MessageType.END_GAME);
                websocketService.sendMessage(gson.toJson(gameOverMessage));
                backgroundMusic.stop();
                Gdx.app.exit();
                myGameCallBack.loseGame();
            } catch (SocketException e) {
                myGameCallBack.noConnection(e.getMessage());
            }
        }


        float locX = player.getPosition().x;
        float locY = player.getPosition().y;

        locX += Gdx.input.getAccelerometerY();
        locY += -Gdx.input.getAccelerometerX();

        float screenWidth = viewport.getWorldWidth();
        float screenHeight = viewport.getWorldHeight();

        if (locX <= 0) {
            locX = 0;
        }
        if (locX >= (screenWidth - Player.WIDTH)) {
            locX = (int) (screenWidth - Player.WIDTH);
        }
        if (locY <= 0) {
            locY = 0;
        }
        if (locY >= (screenHeight - Player.HEIGHT)) {
            locY = (int) (screenHeight - Player.HEIGHT);
        }

        player.setPosition(locX, locY);
        sendPOS();
    }

    private void sendPOS() {
        if (player.isAlive()) {
            try {
                GameMessage gameMessage = new GameMessage();
                gameMessage.setMessageType(GameMessage.MessageType.POSITION_UPDATE);
                gameMessage.setX(player.getPosition().x);
                gameMessage.setY(player.getPosition().y);
                websocketService.sendMessage(gson.toJson(gameMessage));
            } catch (SocketException e) {
                myGameCallBack.noConnection(e.getMessage());
            }
        }
    }

    private void bombExplode(Bomb bomb) {
        float explosionXStart = bomb.getPosition().x - Bomb.RADIUS;
        float explosionXEnd = bomb.getPosition().x + Bomb.WIDTH + Bomb.RADIUS;
        float explosionYStart = bomb.getPosition().y - Bomb.RADIUS;
        float explosionYEnd = bomb.getPosition().y + Bomb.HEIGHT + Bomb.RADIUS;

        if (((player.getPosition().x + Player.WIDTH) >= explosionXStart) && (player.getPosition().x <= explosionXEnd) && ((player.getPosition().y + Player.HEIGHT) >= explosionYStart) && (player.getPosition().y <= explosionYEnd)) {
            player.setAlive(false);
        }
    }

    private void checkBombExplosion(Array<Bomb> bombs) {
        if (bombs.notEmpty()) {
            Iterator<Bomb> bombIterator = bombs.iterator();
            while (bombIterator.hasNext()) {
                Bomb bomb = bombIterator.next();
                if (Calendar.getInstance().getTimeInMillis() >= (bomb.getTime() + Bomb.EXPLOSION_DELAY)) {
                    bombExplode(bomb);
                    updateExplosionEffects(bomb);
                    bombIterator.remove();
                }
            }
        }
    }


    private void updateExplosionEffects(Bomb bomb) {
        ParticleEffect explEffect = new ParticleEffect();
        explEffect.load(Gdx.files.internal("effects/Bomb explosion.p"), Gdx.files.internal("effects"));
        explEffect.setPosition(bomb.getPosition().x + Bomb.WIDTH / 2, bomb.getPosition().y + Bomb.HEIGHT / 2);
        explEffect.start();
        explosions.add(explEffect);
    }

    @Override
    public void dispose() {
        batch.dispose();
        tiledMap.dispose();
        bombSound.dispose();
        backgroundMusic.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            Gdx.input.setCatchKey(Input.Keys.BACK, true);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (bombsPlayer1.size <= 1) {
            Bomb bomb = new Bomb();
            bomb.setPosition(player.getPosition().x, player.getPosition().y);
            Bomb.WIDTH = unitScale * bomb.getBombSprite().getWidth();
            Bomb.HEIGHT = unitScale * bomb.getBombSprite().getHeight();
            bombsPlayer1.add(bomb);
            GameMessage bombMessage = new GameMessage();
            bombMessage.setMessageType(GameMessage.MessageType.BOMB_UPDATE);
            bombMessage.setX(bomb.getPosition().x);
            bombMessage.setY(bomb.getPosition().y);
            try {
                websocketService.sendMessage(gson.toJson(bombMessage));
            } catch (SocketException e) {
                myGameCallBack.noConnection(e.getMessage());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebsocketService.WebsocketServiceBinder binder = (WebsocketService.WebsocketServiceBinder) service;
            websocketService = binder.getService();
            serviceBound = true;
            websocketService.registerListener(GameScreenActivity.this); //Setze diese Klasse als Listener fuer neue Nachrichten
            GameMessage startPosMessage = new GameMessage();
            startPosMessage.setMessageType(GameMessage.MessageType.START_POS);
            try {
                websocketService.sendMessage(gson.toJson(startPosMessage));
            } catch (SocketException e) {
                myGameCallBack.noConnection(e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    public void onMessageReceived(String message) {
        GameMessage gameMessage = gson.fromJson(message, GameMessage.class);
        if (gameMessage.getMessageType() == GameMessage.MessageType.OTHER_POSITION_UPDATE) {
            if (opponent != null) {
                opponent.setPosition(gameMessage.getX(), gameMessage.getY());
            }
        }
        if (gameMessage.getMessageType() == GameMessage.MessageType.BOMB_UPDATE) {
            Bomb newBomb;
            if (bombCounter <= 0) {
                newBomb = bomb1;
                bombCounter = 1;
            } else if (bombCounter <= 1) {
                newBomb = bomb2;
                bombCounter = 2;
            } else {
                newBomb = bomb3;
                bombCounter = 0;
            }
            newBomb.setTime();
            newBomb.setPosition(gameMessage.getX(), gameMessage.getY());
            Bomb.WIDTH = unitScale * newBomb.getBombSprite().getWidth();
            Bomb.HEIGHT = unitScale * newBomb.getBombSprite().getHeight();
            bombsPlayer2.add(newBomb);
        }
        if (gameMessage.getMessageType() == GameMessage.MessageType.END_GAME) {
            backgroundMusic.stop();
            Gdx.app.exit();
            myGameCallBack.winGame();
        }
        if (gameMessage.getMessageType() == GameMessage.MessageType.START_POS) {
            if (player != null) {
                player.setPosition(mapPixelWidth, mapPixelHeight);
            }
        }
    }
}