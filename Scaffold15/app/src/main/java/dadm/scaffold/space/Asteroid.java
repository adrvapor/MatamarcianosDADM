package dadm.scaffold.space;

import android.widget.GridLayout;

import dadm.scaffold.R;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.ScreenGameObject;
import dadm.scaffold.engine.Sprite;
import dadm.scaffold.sound.GameEvent;

public class Asteroid extends Sprite {

    private final GameController gameController;

    private double speed;
    private double speedX;
    private double speedY;

    public Asteroid(GameController gameController, GameEngine gameEngine) {
        super(gameEngine, R.drawable.glitch);
        this.speed = 200d * pixelFactor/1000d;
        this.gameController = gameController;
    }

    public void init(GameEngine gameEngine) {
        // They initialize in a [-30, 30] degrees angle
        double angle = (gameEngine.random.nextDouble()*Math.PI/3d-Math.PI/6d)/2;
        speedX = - speed * Math.cos(angle);
        speedY = speed * Math.sin(angle);
        // Asteroids initialize in the central 50% of the screen horizontally
        positionY = gameEngine.random.nextInt(gameEngine.height/2)+gameEngine.height/4;
        // They initialize outside of the screen vertically
        positionX = width + gameEngine.width;
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
        positionX += speedX * elapsedMillis;
        positionY += speedY * elapsedMillis;
        System.out.println(elapsedMillis);
        // Check of the sprite goes out of the screen and return it to the pool if so
        if (positionX < 0) {
            // Return to the pool
            gameEngine.removeGameObject(this);
            gameController.returnToPool(this);
            gameEngine.onGameEvent(GameEvent.AsteroidOut);
        }
    }

    @Override
    public void onCollision(GameEngine gameEngine, ScreenGameObject otherObject) {

    }
}
