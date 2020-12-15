package dadm.scaffold.counter;

import android.graphics.Canvas;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import dadm.scaffold.R;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.GameObject;
import dadm.scaffold.sound.GameEvent;


public class LivesCounter extends GameObject {
    //private final LinearLayout mLayout;
    private final TextView mText;

    public LivesCounter(View view, int viewResId) {
        //mLayout = (LinearLayout) view.findViewById(viewResId);
        mText = (TextView) view.findViewById(viewResId);
    }

    public void startGame(GameEngine gameEngine) {}

    @Override
    public void startGame() {

    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {}
    @Override
    public void onDraw(Canvas canvas) {}
    @Override
    public void onGameEvent(GameEvent gameEvent) {
        if (gameEvent == GameEvent.LifeLost) {
            mText.post(mRemoveLifeRunnable);
        }
        else if (gameEvent == GameEvent.LifeAdded) {
            mText.post(mAddLifeRunnable);
        }
    }
    private Runnable mRemoveLifeRunnable = new Runnable() {
        @Override
        public void run() {
// Remove one life from the layout
            mText.setText(String.format("%1.0f", Double.parseDouble(mText.getText().toString().replaceAll("S\\$|\\.$", "")) - 1));
        }
    };
    private Runnable mAddLifeRunnable = new Runnable() {
        @Override
        public void run() {
            mText.setText(String.format("%1.0f", Double.parseDouble(mText.getText().toString().replaceAll("S\\$|\\.$", "")) + 1));
        }
    };
}