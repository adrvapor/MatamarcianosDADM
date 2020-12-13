package dadm.scaffold.space;

import dadm.scaffold.R;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.ScreenGameObject;
import dadm.scaffold.engine.Sprite;
import dadm.scaffold.sound.GameEvent;

public class EnemyBullet extends Sprite {

    private double speedFactor;

    private Enemy parent;

    public EnemyBullet(GameEngine gameEngine){
        super(gameEngine, R.drawable.enemybullet);

        speedFactor = - gameEngine.pixelFactor * 600d/1000d;
    }

    @Override
    public void startGame() {}

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        positionX += speedFactor * elapsedMillis;
        System.out.println(elapsedMillis);
        if (positionX < 0) {
            gameEngine.removeGameObject(this);
            // And return it to the pool
            parent.releaseBullet(this);
        }


    }

    public void init(Enemy parentPlayer, double initPositionX, double initPositionY) {
        positionX = initPositionX - width;
        positionY = initPositionY - height;
        parent = parentPlayer;
    }

    public void removeObject(GameEngine gameEngine) {
        gameEngine.removeGameObject(this);
        // And return it to the pool
        parent.releaseBullet(this);
    }

    @Override
    public void onCollision(GameEngine gameEngine, ScreenGameObject otherObject) {

    }
}
