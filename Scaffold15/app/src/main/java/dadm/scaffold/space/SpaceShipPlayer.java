package dadm.scaffold.space;

import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

import dadm.scaffold.R;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.ScreenGameObject;
import dadm.scaffold.engine.Sprite;
import dadm.scaffold.input.InputController;
import dadm.scaffold.sound.GameEvent;

public class SpaceShipPlayer extends Sprite {

    private static final int INITIAL_BULLET_POOL_AMOUNT = 18;//6;
    private static final long TIME_BETWEEN_BULLETS = 300; //250;
    private static final long TIME_BETWEEN_SPECIAL_BULLETS = 5000;
    private int lives = 5;
    List<Bullet> bullets = new ArrayList<Bullet>();
    List<SpecialBullet> specialBullets = new ArrayList<SpecialBullet>();
    private long timeSinceLastFire = 0;
    private long timeSinceLastSpecialFire = TIME_BETWEEN_SPECIAL_BULLETS;

    private int maxX;
    private int maxY;
    private double speedFactor;


    public SpaceShipPlayer(GameEngine gameEngine, int color){
        super(gameEngine, R.drawable.customship);
        switch (color){
            case 1:
                changeBitmap(gameEngine, R.drawable.customshipred);
                break;
            case 2:
                changeBitmap(gameEngine, R.drawable.customshipwhite);
                break;
            default:
                break;
        }
        speedFactor = pixelFactor * 300d / 1000d; //1000d; // We want to move at 100px per second on a 400px tall screen
        maxX = gameEngine.width - width;
        maxY = gameEngine.height - height;

        initBulletPool(gameEngine);
    }

    private void initBulletPool(GameEngine gameEngine) {
        for (int i=0; i<INITIAL_BULLET_POOL_AMOUNT; i++) {
            bullets.add(new Bullet(gameEngine));
        }
        specialBullets.add(new SpecialBullet(gameEngine));
    }

    private Bullet getBullet() {
        if (bullets.isEmpty()) {
            return null;
        }
        return bullets.remove(0);
    }

    void releaseBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    private SpecialBullet getSpecialBullet() {
        if (specialBullets.isEmpty()) {
            return null;
        }
        return specialBullets.remove(0);
    }

    void releaseSpecialBullet(SpecialBullet specialBullet) {
        specialBullets.add(specialBullet);
    }


    @Override
    public void startGame() {
        positionX = maxX / 5;
        positionY = maxY / 2;
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        // Get the info from the inputController
        updatePosition(elapsedMillis, gameEngine.theInputController);
        checkFiring(elapsedMillis, gameEngine);
    }

    private void updatePosition(long elapsedMillis, InputController inputController) {
        positionX += speedFactor * inputController.horizontalFactor * elapsedMillis;
        if (positionX < 0) {
            positionX = 0;
        }
        if (positionX > maxX) {
            positionX = maxX;
        }
        positionY += speedFactor * inputController.verticalFactor * elapsedMillis;
        if (positionY < 0) {
            positionY = 0;
        }
        if (positionY > maxY) {
            positionY = maxY;
        }
    }

    private void checkFiring(long elapsedMillis, GameEngine gameEngine) {

        if (timeSinceLastFire > TIME_BETWEEN_BULLETS) {
            Bullet bullet = getBullet();
            if (bullet == null) {
                return;
            }
            bullet.init(this, positionX + width, positionY + height);
            gameEngine.addGameObject(bullet);
            gameEngine.onGameEvent(GameEvent.LaserFired);

            timeSinceLastFire = 0;
        }

        else {
            timeSinceLastFire += elapsedMillis;
        }

        if(/*timeSinceLastSpecialFire > TIME_BETWEEN_SPECIAL_BULLETS && */gameEngine.theInputController.isFiring){
            SpecialBullet sb = getSpecialBullet();
            if (sb == null){
                return;
            }

            sb.init(this, positionX + width, positionY + height);
            gameEngine.addGameObject(sb);
            gameEngine.onGameEvent(GameEvent.LaserFired);

            timeSinceLastSpecialFire = 0;
        }

        else {
            timeSinceLastSpecialFire += elapsedMillis;
        }

    }

    public void handleHit(GameEngine gameEngine){
        lives--;
        if(lives <= 0){
            gameEngine.removeGameObject(this);
            gameEngine.endGame();
            //TODO triggerear que se termine la partida y todo eso
        }
    }

    @Override
    public void onCollision(GameEngine gameEngine, ScreenGameObject otherObject) {
        if (otherObject instanceof Asteroid) {
            Asteroid a = (Asteroid) otherObject;
            a.removeObject(gameEngine);
            gameEngine.onGameEvent(GameEvent.SpaceshipHit);

            handleHit(gameEngine);
        }

        if (otherObject instanceof Enemy) {
            Enemy e = (Enemy) otherObject;
            e.removeObject(gameEngine);
            gameEngine.onGameEvent(GameEvent.SpaceshipHit);

            handleHit(gameEngine);
        }

        if (otherObject instanceof EnemyBullet) {
            EnemyBullet eb = (EnemyBullet) otherObject;
            eb.removeObject(gameEngine);
            gameEngine.onGameEvent(GameEvent.SpaceshipHit);

            handleHit(gameEngine);
        }
    }
}
