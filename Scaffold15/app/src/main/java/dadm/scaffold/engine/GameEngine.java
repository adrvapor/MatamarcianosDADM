package dadm.scaffold.engine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dadm.scaffold.ScaffoldActivity;
import dadm.scaffold.input.InputController;
import dadm.scaffold.sound.GameEvent;
import dadm.scaffold.sound.SoundManager;

public class GameEngine {


    private List<GameObject> gameObjects = new ArrayList<GameObject>();
    private List<GameObject> objectsToAdd = new ArrayList<GameObject>();
    private List<GameObject> objectsToRemove = new ArrayList<GameObject>();
    /*
    private List<Collision> detectedCollisions = new ArrayList<Collision>();
    private QuadTree quadTree = new QuadTree();
    */

    private UpdateThread theUpdateThread;
    private DrawThread theDrawThread;
    public InputController theInputController;
    private final GameView theGameView;

    public Random random = new Random();

    private SoundManager soundManager;

    private static final long GAME_TIME = 60000;

    public int width;
    public int height;
    public double pixelFactor;
    public int totalTime = 0;
    public double score = 0;

    private Activity mainActivity;

    public GameEngine(Activity activity, GameView gameView) {
        mainActivity = activity;

        theGameView = gameView;
        theGameView.setGameObjects(this.gameObjects);

        QuadTree.init();

        this.width = theGameView.getWidth()
                - theGameView.getPaddingRight() - theGameView.getPaddingLeft();
        this.height = theGameView.getHeight()
                - theGameView.getPaddingTop() - theGameView.getPaddingTop();

        //quadTree.setArea(new Rect(0, 0, width, height));

        this.pixelFactor = this.height / 400d;
    }

    public void setTheInputController(InputController inputController) {
        theInputController = inputController;
    }

    public void startGame() {
        // Stop a game if it is running
        stopGame();

        // Setup the game objects
        int nugameObjects = gameObjects.size();
        for (int i = 0; i < nugameObjects; i++) {
            gameObjects.get(i).startGame();
        }

        // Start the update thread
        theUpdateThread = new UpdateThread(this);
        theUpdateThread.start();

        // Start the drawing thread
        //theDrawThread = new DrawThread(this);
        //theDrawThread.start();
    }

    public void endGame() {
        this.stopGame();
        ((ScaffoldActivity)mainActivity).endGame(score, true);
    }

    public void endGame(double d, boolean b) {
        this.stopGame();
        ((ScaffoldActivity)mainActivity).endGame(d, b);
    }

    public void stopGame() {
        if (theUpdateThread != null) {
            theUpdateThread.stopGame();
        }
        /*
        if (theDrawThread != null) {
            theDrawThread.stopGame();
        }
        */
    }

    public void pauseGame() {
        if (theUpdateThread != null) {
            theUpdateThread.pauseGame();
        }
        if (theDrawThread != null) {
            theDrawThread.pauseGame();
        }
    }

    public void resumeGame() {
        if (theUpdateThread != null) {
            theUpdateThread.resumeGame();
        }
        if (theDrawThread != null) {
            theDrawThread.resumeGame();
        }
    }

    public void addGameObject(GameObject gameObject) {
        if (isRunning()) {
            objectsToAdd.add(gameObject);
        } else {
            addGameObjectNow(gameObject);
        }
        mainActivity.runOnUiThread(gameObject.onAddedRunnable);
    }

    public void removeGameObject(GameObject gameObject) {
        objectsToRemove.add(gameObject);
        mainActivity.runOnUiThread(gameObject.onRemovedRunnable);
    }

    public void onUpdate(long elapsedMillis) {

        totalTime += elapsedMillis;

        if(totalTime > GAME_TIME)
            this.endGame();


        int nugameObjects = gameObjects.size();
        for (int i = 0; i < nugameObjects; i++) {
            GameObject go =  gameObjects.get(i);
            go.onUpdate(elapsedMillis, this);
            if(go instanceof ScreenGameObject) {
                ((ScreenGameObject) go).onPostUpdate(this);
            }
        }
        checkCollisions();
        synchronized (gameObjects) {
            while (!objectsToRemove.isEmpty()) {
                GameObject objectToRemove = objectsToRemove.remove(0);
                gameObjects.remove(objectToRemove);
                /*
                if (objectToRemove instanceof  ScreenGameObject) {
                    quadTree.removeGameObject((ScreenGameObject) objectToRemove);
                }
                */
            }
            while (!objectsToAdd.isEmpty()) {
                GameObject gameObject = objectsToAdd.remove(0);
                addGameObjectNow(gameObject);
            }
        }
    }

    public void onDraw() {
        theGameView.draw();
    }

    public boolean isRunning() {
        return theUpdateThread != null && theUpdateThread.isGameRunning();
    }

    public boolean isPaused() {
        return theUpdateThread != null && theUpdateThread.isGamePaused();
    }

    public Context getContext() {
        return theGameView.getContext();
    }

    private void checkCollisions() {
        // Release the collisions from the previous step
        int numObjects = gameObjects.size();
        for (int i = 0; i < numObjects; i++) {
            if (gameObjects.get(i) instanceof ScreenGameObject) {
                ScreenGameObject objectA = (ScreenGameObject) gameObjects.get(i);
                for (int j = i + 1; j < numObjects; j++) {
                    if (gameObjects.get(j) instanceof ScreenGameObject) {
                        ScreenGameObject objectB = (ScreenGameObject) gameObjects.get(j);
                        if (objectA.checkCollision(objectB)) {
                            objectA.onCollision(this, objectB);
                            objectB.onCollision(this, objectA);
                        }
                    }
                }
            }
        }

        /*
        while (!detectedCollisions.isEmpty()) {
            Collision.release(detectedCollisions.remove(0));
        }
        quadTree.checkCollisions(this, detectedCollisions);
        */
    }

    private void addGameObjectNow (GameObject object) {
        gameObjects.add(object);
        /*
        if (object instanceof ScreenGameObject) {
            ScreenGameObject sgo = (ScreenGameObject) object;

            //quadTree.addGameObject(sgo);
        }
        */
    }

    public void setSoundManager(SoundManager soundManager) {
        this.soundManager = soundManager;
        soundManager.loadLevelMusic();
    }

    public void onGameEvent (GameEvent gameEvent) {
        // We notify all the GameObjects
        int numObjects = gameObjects.size();
        for (int i=0; i<numObjects; i++) {
            gameObjects.get(i).onGameEvent(gameEvent);
        }
        // Also the sound manager
        soundManager.playSoundForGameEvent(gameEvent);
    }
}
