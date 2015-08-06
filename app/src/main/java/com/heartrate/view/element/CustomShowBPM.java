package com.heartrate.view.element;

import com.heartrate.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import android.util.AttributeSet;

import android.view.View;

import java.util.Calendar;


public class CustomShowBPM extends View {


    private int mWidth, mHeight, mXDigitalCenter, mYDigitalCenter;
    private Bitmap mBmHeartScale, mBmArrow;
    private Paint paint;
    private int mValue;
    private long mAngle = 0;
    private float mXTextCenter;
    private float mYTextCenter;
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
    private int mGreenStep;
    private int mYelloStep;
    private int mOrangStep;
    private int mDigitalTextSize;
    private int mTextSize;

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
        initSteps();
    }

    private void initSteps() {
        SharedPreferences sharedPref = getContext().getSharedPreferences(getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int sexe = R.id.sexe_male;
        sexe = sharedPref.getInt(getResources().getString(R.string.value_sexe), sexe);
        int year = 1984;
        year = sharedPref.getInt(getResources().getString(R.string.value_year), year);

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int age = curYear - year;

        int bpm;
        if (sexe == R.id.sexe_male)
            bpm = 220 - age;
        else
            bpm = 226 - age;

        int bpmMin = 0;
        int bpmMax = 0;
        bpmMin = sharedPref.getInt(getResources().getString(R.string.value_bpm_min), bpmMin);
        bpmMax = sharedPref.getInt(getResources().getString(R.string.value_bpm_max), bpmMax);
        int diff = bpmMax - bpmMin;

        if (bpmMin != 0 && bpmMax != 0 && diff > 50) {
            mGreenStep = bpmMin + 65 * (bpmMax - bpmMin) / 100;
            mYelloStep = bpmMin + 85 * (bpmMax - bpmMin) / 100;
            mOrangStep = bpmMin + 90 * (bpmMax - bpmMin) / 100;
        } else {
            mGreenStep = 65 * bpm / 100;
            mYelloStep = 85 * bpm / 100;
            mOrangStep = 90 * bpm / 100;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //get Dimension of the screen to have a plastic chronometer
        mWidth = getWidth();
        mHeight = getHeight();
        mXDigitalCenter = mWidth * 33 / 100;
        mYDigitalCenter = 4 * mHeight / 7;
        mXTextCenter = mWidth * 47 / 100;
        mYTextCenter = 5 * mHeight / 7;
        mDigitalTextSize = mWidth / 4;
        mTextSize = mWidth / 15;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(mBmHeartScale, 0, 0, paint);

        if (mValue < mGreenStep)
            paint.setColor(getResources().getColor(R.color.green));
        else if (mValue < mYelloStep)
            paint.setColor(getResources().getColor(R.color.yellow));
        else if (mValue < mOrangStep)
            paint.setColor(getResources().getColor(R.color.orange));
        else
            paint.setColor(getResources().getColor(R.color.red));

        paint.setTextSize(mDigitalTextSize);

        if (mValue > 100)
            canvas.drawText(String.valueOf(mValue), mXDigitalCenter, mYDigitalCenter, paint);
        else
            canvas.drawText(String.format("0%d", mValue), mXDigitalCenter, mYDigitalCenter, paint);

        paint.setTextSize(mTextSize);

        canvas.drawText("BPM", mXTextCenter, mYTextCenter, paint);

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
