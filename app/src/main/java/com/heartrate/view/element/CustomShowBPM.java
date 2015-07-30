package com.heartrate.view.element;

import com.heartrate.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import android.util.AttributeSet;

import android.view.View;


public class CustomShowBPM extends View {


    private int mWidth, mHeight, mXCenter, mYCenter;
    private Bitmap mBmHeartScale, mBmArrow;
    private Paint paint;
    private int mValue;
    private long mAngle = 0;
    private float mX2Center;
    private float mY2Center;
    private Matrix mRotator;
    private int mAnimTime = 60;

    private Runnable animator = new Runnable() {
        @Override
        public void run() {
            update();
            invalidate();
            if (!isAtRest()) {
                postDelayed(this, mAnimTime);
            }
        }
    };

    public CustomShowBPM(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStrokeWidth(10);
        paint.setColor(getResources().getColor(android.R.color.black));
        paint.setAntiAlias(true);

        mBmHeartScale = BitmapFactory.decodeResource(getResources(), R.drawable.heart_scale);
        mBmArrow = BitmapFactory.decodeResource(getResources(), R.drawable.im_arrow);
        mValue = 83;
        mRotator = new Matrix();
        removeCallbacks(animator);
        post(animator);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //get Dimension of the screen to have a plastic chronometer
        mWidth = getWidth();
        mHeight = getHeight();
        mXCenter = mWidth * 33 / 100;
        mYCenter = 4 * mHeight / 7;
        mX2Center = mWidth * 47 / 100;
        mY2Center = 5 * mHeight / 7;
        paint.setTextSize(mWidth / 4);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(mBmHeartScale, 0, 0, paint);
        if (mValue > 100)
            canvas.drawText("" + mValue, mXCenter, mYCenter, paint);
        else
            canvas.drawText("0" + mValue, mXCenter, mYCenter, paint);
        paint.setTextSize(mWidth / 15);
        canvas.drawText("BPM", mX2Center, mY2Center, paint);
        paint.setTextSize(mWidth / 4);
        canvas.drawBitmap(mBmArrow, mRotator, paint);


    }

    protected boolean isAtRest() {
        if (mAngle < (100 * mValue / 80))
            return false;
        else
            return true;
    }

    protected void update() {
        mAngle += 2;
        mRotator.postRotate(-2, mWidth / 2, mHeight / 2);
    }


    public void setValue(int v) {
        mValue = v;
    }
}
