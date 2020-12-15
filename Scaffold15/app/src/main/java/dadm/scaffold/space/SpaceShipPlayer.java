package dadm.scaffold.space;

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
    private long TIME_BETWEEN_BULLETS = 300; //250;
    private static final long TIME_BETWEEN_SPECIAL_BULLETS = 5000;
    private static final long SHIELD_TIME = 5000;
    private static final long BULLET_TIME = 5000;
    private int lives = 5;
    List<Bullet> bullets = new ArrayList<Bullet>();
    List<SpecialBullet> specialBullets = new ArrayList<SpecialBullet>();
    private long timeSinceLastFire = 0;
    private long timeSinceLastSpecialFire = TIME_BETWEEN_SPECIAL_BULLETS;

    private int maxX;
    private int maxY;
    private double speedFactor;
    private boolean shield;
    private boolean bullet;
    private long shieldTime;
    private long bulletTime;
    private int color;

    public SpaceShipPlayer(GameEngine gameEngine, int c){
        super(gameEngine, R.drawable.customship);
        color = c;
        changeToColor(gameEngine);

        speedFactor = pixelFactor * 300d / 1000d; //1000d; // We want to move at 100px per second on a 400px tall screen
        maxX = gameEngine.width - width;
        maxY = gameEngine.height - height;
        shield = false;
        bullet = false;
        shieldTime = 0;
        bulletTime = 0;

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

        if(shield){
            shieldTime += elapsedMillis;
            if(shieldTime >= SHIELD_TIME){
                shield = false;
                shieldTime = 0;
                changeToColor(gameEngine);
            }
        }

        if(bullet){
            bulletTime += elapsedMillis;
            if(bulletTime >= BULLET_TIME){
                bullet = false;
                bulletTime = 0;
                TIME_BETWEEN_BULLETS = 300;
            }
        }
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
            gameEngine.onGameEvent(GameEvent.SpecialLaserFired);

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
            gameEngine.endGame(0, false);
        }
    }

    public void changeToColor(GameEngine gameEngine){
        switch (color){
            case 0:
                changeBitmap(gameEngine, R.drawable.customship);
                break;
            case 1:
                changeBitmap(gameEngine, R.drawable.customshipred);
                break;
            case 2:
                changeBitmap(gameEngine, R.drawable.customshipwhite);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCollision(final GameEngine gameEngine, ScreenGameObject otherObject) {
        if (otherObject instanceof Asteroid && !shield) {
            Asteroid a = (Asteroid) otherObject;
            a.removeObject(gameEngine);
            gameEngine.onGameEvent(GameEvent.SpaceshipHit);

            handleHit(gameEngine);
            gameEngine.onGameEvent(GameEvent.LifeLost);
        }

        if (otherObject instanceof Enemy && !shield) {
            Enemy e = (Enemy) otherObject;
            e.removeObject(gameEngine);
            gameEngine.onGameEvent(GameEvent.SpaceshipHit);

            handleHit(gameEngine);
            gameEngine.onGameEvent(GameEvent.LifeLost);
        }

        if (otherObject instanceof EnemyBullet && !shield) {

            EnemyBullet eb = (EnemyBullet) otherObject;
            eb.removeObject(gameEngine);
            gameEngine.onGameEvent(GameEvent.SpaceshipHit);

            handleHit(gameEngine);
            gameEngine.onGameEvent(GameEvent.LifeLost);
        }

        if (otherObject instanceof Powerup){
            Powerup p = (Powerup) otherObject;

            switch (p.type){
                case Bullet:
                    bullet = true;
                    TIME_BETWEEN_BULLETS = 200;
                    break;

                case Shield:
                    shield = true;
                    changeBitmap(gameEngine, R.drawable.customshipshield);
                    break;

                case Health:
                    lives++;
                    gameEngine.onGameEvent(GameEvent.LifeAdded);
                    break;
            }

            p.removeObject(gameEngine);
        }
    }
}
