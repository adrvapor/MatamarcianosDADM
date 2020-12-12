package dadm.scaffold.engine;

public class UpdateThread extends Thread {

    private final GameEngine theGameEngine;
    private boolean isGameRunning = true;
    private boolean isGamePaused = false;

    private Object synchroLock = new Object();

    public UpdateThread(GameEngine gameEngine) {
        theGameEngine = gameEngine;
    }

    @Override
    public void start() {
        isGameRunning = true;
        isGamePaused = false;
        super.start();
    }

    public void stopGame() {
        isGameRunning = false;
        resumeGame();
    }

    @Override
    public void run() {
        long previousTimeMillis;
        long currentTimeMillis;
        long elapsedMillis;
        previousTimeMillis = System.currentTimeMillis();

        while (isGameRunning) {
            currentTimeMillis = System.currentTimeMillis();
            elapsedMillis = currentTimeMillis - previousTimeMillis;
            if (isGamePaused) {
                while (isGamePaused) {
                    try {
                        synchronized (synchroLock) {
                            synchroLock.wait();
                        }
                    } catch (InterruptedException e) {
                        // We stay on the loop
                    }
                }
                currentTimeMillis = System.currentTimeMillis();
            }
            if (elapsedMillis < 17) { // This is 50 fps
                try {
                    Thread.sleep(17 -elapsedMillis);
                } catch (InterruptedException e) {
                    // We just continue
                }
            }

            theGameEngine.onUpdate(elapsedMillis);
            theGameEngine.onDraw();
            previousTimeMillis = currentTimeMillis;
        }
    }

    public void pauseGame() {
        isGamePaused = true;
    }

    public void resumeGame() {
        if (isGamePaused == true) {
            isGamePaused = false;
            synchronized (synchroLock) {
                synchroLock.notify();
            }
        }
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public boolean isGamePaused() {
        return isGamePaused;
    }
}
