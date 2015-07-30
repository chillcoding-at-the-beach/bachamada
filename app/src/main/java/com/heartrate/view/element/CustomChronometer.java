package com.heartrate.view.element;

import com.heartrate.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Chronometer;

public class CustomChronometer extends Chronometer {
    private boolean start = false;
    private boolean stop = false;
    private long startTime, elapsedTime;
    private float angle = 0f;
    private Paint paint;
    private RectF r, rectBtnRefresh;
    private int mMargin = 0;
    private int mXCenter = 0;
    private int mYCenter = 0;
    private String mTxtStart;
    private int mTxtSize;
    private int mTime = 15000;
    private int mX2Center;
    private MediaPlayer mMediaPlayer;
    private String mTxtStop;
    private Bitmap mBtnRefresh;
    private int mYDown;
    private int mXDown;
    private int mXRight;
    private int mTxtSizeS;


    public CustomChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);

        //initiate of variable textual
        mTxtStart = context.getString(R.string.text_start);
        mTxtStop = context.getString(R.string.text_stop);

        paint = new Paint();

        //initiate of paint
        paint.setStrokeWidth(10);
        //paint.setTextSize(mTxtSize);
        paint.setAntiAlias(true);

        //initiate of media player
        mMediaPlayer = MediaPlayer.create(context, R.raw.ding);

        //initiate of refresh button
        mBtnRefresh = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_rotate);


    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //get Dimension of the screen to have a plastic chronometer
        int width = getWidth();
        mMargin = width / 10;
        mXCenter = width / 5;
        mX2Center = 2 * width / 6;
        mYCenter = (getHeight() / 2);
        mXRight = mXCenter + 43 * width / 100;
        mTxtSize = 3 * width / 11;
        mTxtSizeS = width / 10;
        mYDown = getHeight() * 4 / 5;
        mXDown = width / 2 - mBtnRefresh.getWidth() / 2;
        r = new RectF(left + mMargin, top + mMargin, left + width - mMargin, top + width - mMargin);
        rectBtnRefresh = new RectF(mXDown, mYDown, mXDown + mBtnRefresh.getWidth(), mYDown + mBtnRefresh.getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (start == false && stop == false && r.contains(event.getX(), event.getY())) {
                    start = true;
                    start();
                }
                if (start == true || start == false && stop == true) {
                    //not the beginning of the measurement there is refresh button
                    if (rectBtnRefresh.contains(event.getX(), event.getY()))
                        refresh();

                }
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            default:
                return true;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {

        //initiate paint for the base circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.gray));
        canvas.drawArc(r, 270f, 360, false, paint);
        if (start) {
            //for the dynamic circle
            paint.setColor(getResources().getColor(R.color.red));
            //calculate angle of dynamic circle
            elapsedTime = System.currentTimeMillis();
            angle = (elapsedTime - startTime) * 360f / mTime;
            canvas.drawArc(r, 270f, angle, false, paint);

            //to stp the chrono
            if ((elapsedTime - startTime) > (mTime + 2000))
                stop();

            //for the counter
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(android.R.color.white));
            long v = (mTime - (elapsedTime - startTime)) / 1000;
            if (v < 10)
                canvas.drawText("0" + v, mX2Center, mYCenter, paint);
            else
                canvas.drawText("" + v, mX2Center, mYCenter, paint);
            //second info
            paint.setTextSize(mTxtSizeS);
            canvas.drawText("s.", mXRight, mYCenter, paint);
            paint.setTextSize(mTxtSize);
            //restart button
            canvas.drawBitmap(mBtnRefresh, mXDown, mYDown, null);

            if (angle >= 360) {
                //play the alarm sound
                mMediaPlayer.start();

                start = false;
                stop = true;
            }
            invalidate();
        } else {
            if (stop == true) {
                //for the stop button
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);
                paint.setColor(getResources().getColor(android.R.color.white));
                canvas.drawText(mTxtStop, mXCenter, mYCenter, paint);
                //refresh button
                canvas.drawBitmap(mBtnRefresh, mXDown, mYDown, null);
            } else {
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);
                paint.setColor(getResources().getColor(android.R.color.white));
                paint.setStrokeWidth(10);
                paint.setTextSize(mTxtSize);
                paint.setAntiAlias(true);
                canvas.drawText(mTxtStart, mXCenter, mYCenter, paint);

            }
            invalidate();
        }

    }

    @Override
    public void start() {
        super.start();
        start = true;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void stop() {
        super.stop();
        start = false;
    }

    public boolean measurementIsFinished() {
        if (start == false && stop == true)
            return true;
        else
            return false;
    }

    public void refresh() {
        //re init start and stop variable like at the beginning
        start = false;
        stop = false;
    }

    public void setTime(int t) {
        mTime = t;
    }


}

