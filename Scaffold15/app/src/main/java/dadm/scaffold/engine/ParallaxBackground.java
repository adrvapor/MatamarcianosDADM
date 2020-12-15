package dadm.scaffold.engine;

import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class ParallaxBackground extends ScreenGameObject{

    //region variables
    protected double mImageHeight;

    protected double mImageWidth;

    protected double mScreenWidth;

    protected double mScreenHeight;

    protected double mTargetWidth;

    protected double mSpeedX;

    protected double mPositionX;

    protected double mPixelFactor;

    protected Rect mSrcRect;

    protected Rect mDstRect;

    private final Matrix mMatrix = new Matrix();

    protected Bitmap mBitmap;
    //endregion

    public ParallaxBackground (GameEngine gameEngine, int speed, int drawableResId){
        Drawable spriteDrawable = gameEngine.getContext().getResources().getDrawable(drawableResId);
        mBitmap = ((BitmapDrawable)spriteDrawable).getBitmap();

        mPixelFactor = gameEngine.pixelFactor* 0.5;
        mSpeedX = speed*mPixelFactor/100d;

        mImageHeight = spriteDrawable.getIntrinsicHeight()*mPixelFactor;
        mImageWidth = spriteDrawable.getIntrinsicWidth()*mPixelFactor;

        mScreenHeight = gameEngine.height;
        mScreenWidth = gameEngine.width;

        mTargetWidth = Math.min(mImageWidth, mScreenWidth);
    }

    @Override
    public void startGame() {

    }

    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        mPositionX -= mSpeedX * elapsedMillis;
    }

    public void onDraw(Canvas canvas) {
        if (mPositionX < 0) { //Cuando esta pintando las 2 imagenes (loop)
            mMatrix.reset();
            mMatrix.postScale((float) (mPixelFactor),
                    (float) (mPixelFactor));
            mMatrix.postTranslate((float) (mPositionX + mImageWidth), 0);
            canvas.drawBitmap(mBitmap, mMatrix, null);
        }
        mMatrix.reset();
        mMatrix.postScale((float) (mPixelFactor),
                (float) (mPixelFactor));
        mMatrix.postTranslate((float) mPositionX, 0);
        canvas.drawBitmap(mBitmap, mMatrix, null);

        if (mPositionX < -mImageWidth) {
            mPositionX = 0;
        }
    }

    private void efficientDraw(Canvas canvas) {
        if (mPositionX < 0) {

            mSrcRect.set(0,
                    (int) (-mPositionX /mPixelFactor),
                    (int) (mTargetWidth/mPixelFactor),
                    (int) ((mScreenHeight - mPositionX)/mPixelFactor));
            mDstRect.set(0,
                    0,
                    (int) mTargetWidth,
                    (int) mScreenHeight);
            canvas.drawBitmap(mBitmap, mSrcRect, mDstRect, null);
        }
        else {
            mSrcRect.set(0,
                    0,
                    (int) (mTargetWidth/mPixelFactor),
                    (int) ((mScreenHeight - mPositionX) / mPixelFactor));
            mDstRect.set(0,
                    (int) mPositionX,
                    (int) mTargetWidth,
                    (int) mScreenHeight);
            canvas.drawBitmap(mBitmap, mSrcRect, mDstRect, null);
// We need to draw the previous block
            mSrcRect.set(0,
                    (int) ((mImageHeight - mPositionX) / mPixelFactor),
                    (int) (mTargetWidth/mPixelFactor),
                    (int) (mImageHeight/mPixelFactor));
            mDstRect.set(0,
                    0,
                    (int) mTargetWidth,
                    (int) mPositionX);
            canvas.drawBitmap(mBitmap, mSrcRect, mDstRect, null);
        }
        if (mPositionX > mScreenHeight) {
            mPositionX -= mImageHeight;
        }
    }

    @Override
    public void onCollision(GameEngine gameEngine, ScreenGameObject otherObject) {

    }
}
