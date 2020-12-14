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

    protected double mSpeedY;

    protected double mPositionY;

    protected double mPixelFactor;

    protected Rect mSrcRect;

    protected Rect mDstRect;

    private final Matrix mMatrix = new Matrix();

    protected Bitmap mBitmap;
    //endregion

    public ParallaxBackground (GameEngine gameEngine, int speed, int drawableResId){
        Drawable spriteDrawable = gameEngine.getContext().getResources().getDrawable(drawableResId);
        mBitmap = ((BitmapDrawable)spriteDrawable).getBitmap();

        mPixelFactor = gameEngine.pixelFactor;
        mSpeedY = speed*mPixelFactor/1000d;

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
        mPositionY += mSpeedY * elapsedMillis;
    }

    public void onDraw(Canvas canvas) {
        if (mPositionY > 0) {
            mMatrix.reset();
            mMatrix.postScale((float) (mPixelFactor),
                    (float) (mPixelFactor));
            mMatrix.postTranslate(0, (float) (mPositionY - mImageHeight));
            canvas.drawBitmap(mBitmap, mMatrix, null);
        }
        mMatrix.reset();
        mMatrix.postScale((float) (mPixelFactor),
                (float) (mPixelFactor));
        mMatrix.postTranslate(0, (float) mPositionY);
        canvas.drawBitmap(mBitmap, mMatrix, null);

        if (mPositionY > mScreenHeight) {
            mPositionY -= mImageHeight;
        }
    }

    private void efficientDraw(Canvas canvas) {
        if (mPositionY < 0) {

            mSrcRect.set(0,
                    (int) (-mPositionY/mPixelFactor),
                    (int) (mTargetWidth/mPixelFactor),
                    (int) ((mScreenHeight - mPositionY)/mPixelFactor));
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
                    (int) ((mScreenHeight - mPositionY) / mPixelFactor));
            mDstRect.set(0,
                    (int) mPositionY,
                    (int) mTargetWidth,
                    (int) mScreenHeight);
            canvas.drawBitmap(mBitmap, mSrcRect, mDstRect, null);
// We need to draw the previous block
            mSrcRect.set(0,
                    (int) ((mImageHeight - mPositionY) / mPixelFactor),
                    (int) (mTargetWidth/mPixelFactor),
                    (int) (mImageHeight/mPixelFactor));
            mDstRect.set(0,
                    0,
                    (int) mTargetWidth,
                    (int) mPositionY);
            canvas.drawBitmap(mBitmap, mSrcRect, mDstRect, null);
        }
        if (mPositionY > mScreenHeight) {
            mPositionY -= mImageHeight;
        }
    }

    @Override
    public void onCollision(GameEngine gameEngine, ScreenGameObject otherObject) {

    }
}
