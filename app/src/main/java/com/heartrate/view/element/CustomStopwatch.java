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

public class CustomStopwatch extends Chronometer {
    private boolean start = false;
    private boolean stop = false;
    private long startTime, elapsedTime;
    private float angle = 0f;
    private Paint paint;
    private RectF rectMainCircle, rectBtnRefresh;
    private int mXCenter = 0;
    private int mYTextCenter = 0;
    private String mTxtStart;
    private int mTxtSize;
    private int mTime = 15000;
    private int mXSecondCenter;
    private MediaPlayer mMediaPlayer;
    private String mTxtStop;
    private Bitmap mBtnRefresh;
    private int mYDown;
    private int mXDown;


    public CustomStopwatch(Context context, AttributeSet attrs) {
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
        //get Dimension of the screen to have a plastic stopwatch
        int width = getWidth();
        int height = getHeight();

        mTxtSize = 3 * width / 11;

        mXCenter = width / 5;

        int diamCircle = 4 * width / 5;
        int marginXCircle = width / 10;
        int marginYCircle = (height - diamCircle) / 2;

        rectMainCircle = new RectF(marginXCircle, marginYCircle, marginXCircle + diamCircle, marginYCircle + diamCircle);

        int halfWidthTxtSize = 56 * mTxtSize / 100;
        int halfHeightTxtSize = 35 * mTxtSize / 100;

        mYTextCenter = marginYCircle + diamCircle / 2 + halfHeightTxtSize;
        mXSecondCenter = width / 2 - halfWidthTxtSize;

        mYDown = marginYCircle + diamCircle + marginYCircle / 7;
        mXDown = width / 2 - mBtnRefresh.getWidth() / 2;
        rectBtnRefresh = new RectF(mXDown, mYDown, mXDown + mBtnRefresh.getWidth(), mYDown + mBtnRefresh.getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (start == false && stop == false && rectMainCircle.contains(event.getX(), event.getY())) {
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
        canvas.drawArc(rectMainCircle, 270f, 360, false, paint);
        if (start) {
            //for the dynamic circle
            paint.setColor(getResources().getColor(R.color.red));
            //calculate angle of dynamic circle
            elapsedTime = System.currentTimeMillis();
            angle = (elapsedTime - startTime) * 360f / mTime;
            canvas.drawArc(rectMainCircle, 270f, angle, false, paint);

            //to stop count down
            if ((elapsedTime - startTime) > (mTime + 2000))
                stop();

            //for the counter
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(android.R.color.white));
            long v = (mTime - (elapsedTime - startTime)) / 1000;
            if (v < 10)
                canvas.drawText("0" + v, mXSecondCenter, mYTextCenter, paint);
            else
                canvas.drawText("" + v, mXSecondCenter, mYTextCenter, paint);
            //second info
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
                canvas.drawText(mTxtStop, mXCenter, mYTextCenter, paint);
                //refresh button
                canvas.drawBitmap(mBtnRefresh, mXDown, mYDown, null);
            } else {
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);
                paint.setColor(getResources().getColor(android.R.color.white));
                paint.setStrokeWidth(10);
                paint.setTextSize(mTxtSize);
                paint.setAntiAlias(true);
                canvas.drawText(mTxtStart, mXCenter, mYTextCenter, paint);

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

