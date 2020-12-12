package dadm.scaffold.space;

import java.util.ArrayList;
import java.util.List;

import dadm.scaffold.R;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.ScreenGameObject;
import dadm.scaffold.engine.Sprite;
import dadm.scaffold.sound.GameEvent;

public class Enemy extends Sprite {

    private final GameController gameController;

    List<EnemyBullet> bullets = new ArrayList<EnemyBullet>();
    private static final long TIME_BETWEEN_BULLETS = 700; //250;
    private static final int INITIAL_BULLET_POOL_AMOUNT = 12;
    private long timeSinceLastFire = 0;
    private double speed;
    private int lives = 5;

    public Enemy(GameController gameController, GameEngine gameEngine) {
        super(gameEngine, R.drawable.robot);
        this.speed = - 200d * pixelFactor/1000d;
        this.gameController = gameController;
    }

    public void init(GameEngine gameEngine) {
        // Asteroids initialize in the central 50% of the screen horizontally
        positionY = gameEngine.random.nextInt(gameEngine.height/2)+gameEngine.height/4;
        // They initialize outside of the screen vertically
        positionX = width + gameEngine.width;
        lives = 5;
        timeSinceLastFire = 0;


        for (int i=0; i<INITIAL_BULLET_POOL_AMOUNT; i++) {
            bullets.add(new EnemyBullet(gameEngine));
        }
    }

    @Override
    public void startGame() {
    }

    public void removeObject(GameEngine gameEngine) {
        // Return to the pool
        gameEngine.removeGameObject(this);
        gameController.returnToPool(this);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        positionX += speed * elapsedMillis;
        positionY += 0;
        System.out.println(elapsedMillis);
        // Check of the sprite goes out of the screen and return it to the pool if so
        if (positionX < 0) {
            // Return to the pool
            gameEngine.removeGameObject(this);
            gameController.returnToPool(this);
        }


        if (timeSinceLastFire > TIME_BETWEEN_BULLETS) {
            EnemyBullet bullet = getBullet();
            if (bullet == null) {
                return;
            }
            bullet.init(this, positionX, positionY + height/2);
            gameEngine.addGameObject(bullet);
            //gameEngine.onGameEvent(GameEvent.LaserFired);
            timeSinceLastFire = 0;
        }
        else {
            timeSinceLastFire += elapsedMillis;
        }
    }

    @Override
    public void onCollision(GameEngine gameEngine, ScreenGameObject otherObject) {

    }

    EnemyBullet getBullet(){
        if (bullets.isEmpty()){
            return null;
        }
        return bullets.remove(0);
    }

    void releaseBullet(EnemyBullet eb){
        bullets.add(eb);
    }

    public void takeDamage(GameEngine gameEngine, int amount){
        lives -= amount;
        if(lives <= 0){
            removeObject(gameEngine);
            // TODO APORTAR PUNTOS
        }
    }
}
