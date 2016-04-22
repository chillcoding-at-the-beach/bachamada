package fr.machada.bpm.pro.view.element;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import fr.machada.bpm.pro.R;
import fr.machada.bpm.pro.model.Effort;
import fr.machada.bpm.pro.utils.FCIndicator;


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

    private int mDigitalTextSize;
    private int mTextSize;
    private Effort mStep;

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
        mXDigitalCenter = mWidth * 34 / 100;
        mYDigitalCenter = 54 * mHeight / 100;
        mXTextCenter = mWidth * 45 / 100;
        mYTextCenter = 62 * mHeight / 100;
        mDigitalTextSize = mWidth / 5;
        mTextSize = mWidth / 15;
        mBmHeartScale = Bitmap.createScaledBitmap(mBmHeartScale, mWidth, mWidth, false);
        mBmArrow = Bitmap.createScaledBitmap(mBmArrow, mWidth, mWidth, false);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(mBmHeartScale, 0, 0, paint);

        switch (mStep) {
            case GURU:
                paint.setColor(getResources().getColor(R.color.green));
                break;
            case WALKING:
                paint.setColor(getResources().getColor(R.color.yellow));
                break;
            case INTERVAL:
                paint.setColor(getResources().getColor(R.color.orange));
                break;
            case EXERCISE:
                paint.setColor(getResources().getColor(R.color.reed));
        }

        paint.setTextSize(mDigitalTextSize);

        if (mValue > 100)
            canvas.drawText(String.valueOf(mValue), mXDigitalCenter, mYDigitalCenter, paint);
        else
            canvas.drawText(String.format("0%d", mValue), mXDigitalCenter, mYDigitalCenter, paint);

        paint.setTextSize(mTextSize);

        canvas.drawText(getResources().getString(R.string.bpm_text), mXTextCenter, mYTextCenter, paint);

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


    public void setStep(Effort step) {
        this.mStep = step;
    }
}
