package dadm.scaffold.space;

import dadm.scaffold.R;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.ScreenGameObject;
import dadm.scaffold.engine.Sprite;
import dadm.scaffold.sound.GameEvent;

public class SpecialBullet extends Sprite {

    private double speedFactor;
    private double axis;

    private SpaceShipPlayer parent;

    public SpecialBullet(GameEngine gameEngine){
        super(gameEngine, R.drawable.specialbullet);

        speedFactor = gameEngine.pixelFactor * 300d/1000d;
    }

    @Override
    public void startGame() {}

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        positionX += speedFactor * elapsedMillis;
        positionY = axis + 300 * Math.sin(positionX*0.02);
        System.out.println(elapsedMillis);
        if (positionX > gameEngine.width) {
            gameEngine.removeGameObject(this);
            // And return it to the pool
            parent.releaseSpecialBullet(this);
        }
    }

    public void init(SpaceShipPlayer parentPlayer, double initPositionX, double initPositionY) {
        positionX = initPositionX - width;
        positionY = initPositionY - height;
        axis = positionY;
        parent = parentPlayer;
    }

    private void removeObject(GameEngine gameEngine) {
        gameEngine.removeGameObject(this);
        // And return it to the pool
        parent.releaseSpecialBullet(this);
    }

    @Override
    public void onCollision(GameEngine gameEngine, ScreenGameObject otherObject) {
        if (otherObject instanceof Asteroid) {
            // Remove both from the game (and return them to their pools)
            Asteroid a = (Asteroid) otherObject;
            a.removeObject(gameEngine);
            gameEngine.onGameEvent(GameEvent.AsteroidHit);
            // Add some score
        }
        if (otherObject instanceof Enemy) {
            // Remove both from the game (and return them to their pools)
            Enemy e = (Enemy) otherObject;
            e.takeDamage(gameEngine, 2);
            gameEngine.onGameEvent(GameEvent.VirusHit);
            // Add some score
        }
    }
}
