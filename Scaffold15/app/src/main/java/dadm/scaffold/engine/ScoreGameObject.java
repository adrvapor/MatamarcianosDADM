package dadm.scaffold.engine;

import android.graphics.Canvas;
import android.view.View;
import android.widget.TextView;

import dadm.scaffold.sound.GameEvent;


public class ScoreGameObject extends GameObject{
    //region Variables
    private final TextView mText;

    private double mPoints;
    private boolean mPointsHaveChanged;
    private static final double PUNTOS_GANADOS_POR_GLITCH = 0.1;
    private static final double PUNTOS_GANADOS_POR_VIRUS = 0.3;
    private static final double PUNTOS_PERDIDOS_POR_GOLPE = 0.4;
    private static final double PUNTOS_PERDIDOS_POR_EVITAR_GLITCH = 0.3;
    private static final double PUNTOS_PERDIDOS_POR_EVITAR_VIRUS = 0.5;
    //endregion


    public ScoreGameObject(View view, int viewResId) {
        mText = (TextView) view.findViewById(viewResId);
    }

    @Override
    public void startGame() {
        mPoints = 0;
        mText.post(mUpdateTextRunnable);
    }

    public void onGameEvent(GameEvent gameEvent){
        if (gameEvent == GameEvent.AsteroidHit){
            mPoints += PUNTOS_GANADOS_POR_GLITCH;
            mPointsHaveChanged = true;
        }else if (gameEvent == GameEvent.VirusHit){
            mPoints += PUNTOS_GANADOS_POR_VIRUS;
            mPointsHaveChanged = true;
        }else if (gameEvent == GameEvent.SpaceshipHit){
            mPoints -= PUNTOS_PERDIDOS_POR_GOLPE;
            mPointsHaveChanged = true;
        }else if (gameEvent == GameEvent.AsteroidOut){
            mPoints -= PUNTOS_PERDIDOS_POR_EVITAR_GLITCH;
            mPointsHaveChanged = true;
        }else if (gameEvent == GameEvent.EnemyOut){
            mPoints -= PUNTOS_PERDIDOS_POR_EVITAR_VIRUS;
            mPointsHaveChanged = true;
        }

        if (mPoints < 0) mPoints = 0;
    }

    private Runnable mUpdateTextRunnable = new Runnable() {
        @Override
        public void run() {
            String text = String.format("%03.1f", mPoints);
            mText.setText(text);
        }
    };

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        if(mPointsHaveChanged)
            gameEngine.score = mPoints;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mPointsHaveChanged) {
            mText.post(mUpdateTextRunnable);
            mPointsHaveChanged = false;
        }
    }
}
