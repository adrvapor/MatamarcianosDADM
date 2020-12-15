package dadm.scaffold.space;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.GameObject;
import dadm.scaffold.sound.GameEvent;

public class GameController extends GameObject {

    private static final int TIME_BETWEEN_ASTEROIDS = 500;
    private static final int TIME_BETWEEN_ENEMIES = 3000;
    private static final int TIME_BETWEEN_POWERUPS = 4000;
    private long currentMillis;
    private List<Asteroid> asteroidPool = new ArrayList<Asteroid>();
    private List<Enemy> enemyPool = new ArrayList<Enemy>();
    private List<Powerup> powerupPool = new ArrayList<>();
    private int asteroidsSpawned;
    private int enemiesSpawned;
    private int powerupSpawned;

    public GameController(GameEngine gameEngine) {
        // We initialize the pool of items now
        for (int i=0; i<10; i++) {
            asteroidPool.add(new Asteroid(this, gameEngine));
        }
        for (int i=0; i<2; i++) {
            enemyPool.add(new Enemy(this, gameEngine));
        }
        for (int i=0; i<3; i++) {
            powerupPool.add(new Powerup(this, gameEngine));
        }
        for (int i =0; i<5; i++){
            gameEngine.onGameEvent(GameEvent.LifeAdded);
        }
    }

    @Override
    public void startGame() {
        currentMillis = 0;
        asteroidsSpawned = 0;
        enemiesSpawned = 0;
        powerupSpawned = 0;
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        currentMillis += elapsedMillis;

        long waveTimestamp = asteroidsSpawned*TIME_BETWEEN_ASTEROIDS;
        if (currentMillis > waveTimestamp) {
            // Spawn a new enemy
            Asteroid a = asteroidPool.remove(0);
            a.init(gameEngine);
            gameEngine.addGameObject(a);
            asteroidsSpawned++;
            return;
        }

        waveTimestamp = enemiesSpawned*TIME_BETWEEN_ENEMIES;
        if (currentMillis > waveTimestamp) {
            // Spawn a new enemy
            Enemy e = enemyPool.remove(0);
            e.init(gameEngine);
            gameEngine.addGameObject(e);
            enemiesSpawned++;
            return;
        }

        waveTimestamp = powerupSpawned*TIME_BETWEEN_POWERUPS;
        if (currentMillis > waveTimestamp) {
            // Spawn a new powerup
            Powerup p = powerupPool.remove(0);
            p.init(gameEngine);
            gameEngine.addGameObject(p);
            powerupSpawned++;
            return;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        // This game object does not draw anything
    }

    public void returnToPool(Asteroid asteroid) {
        asteroidPool.add(asteroid);
    }

    public void returnToPool(Powerup powerup) {
        powerupPool.add(powerup);
    }

    public void returnToPool(Enemy enemy) {
        enemyPool.add(enemy);
    }
}
