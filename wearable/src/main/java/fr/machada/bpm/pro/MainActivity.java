package fr.machada.bpm.pro;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private TextView mTextView;
    private RelativeLayout mBackground;
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    private int mValue;

    int mGreenStep = 90;
    int mYelloStep = 150;
    int mOrangStep = 170;
    int mLowStep = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mBackground = (RelativeLayout) stub.findViewById(R.id.wear_main_background);
                initText();
            }
        });
        initHeartRateSensor();
        setAmbientEnabled();
    }

    private void initHeartRateSensor() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensorManager != null)
            mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initText() {
        mTextView.setTextColor(getResources().getColor(R.color.green));
        mTextView.setText(String.format("%d", 80));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            if ((int) sensorEvent.values[0] > 0) {
                mValue = (int) sensorEvent.values[0];
                if (!isAmbient())
                    changeText(false);
            }
        }
    }

    private void changeText(boolean ambient) {
        if (!ambient) {
            if (mValue < mLowStep)
                mTextView.setTextColor(Color.BLUE);
            else if (mValue < mGreenStep)
                mTextView.setTextColor(Color.GREEN);
            else if (mValue < mYelloStep)
                mTextView.setTextColor(Color.YELLOW);
            else if (mValue < mOrangStep)
                mTextView.setTextColor(Color.YELLOW);
            else
                mTextView.setTextColor(Color.RED);
        }
        mTextView.setText(String.format("%d", mValue));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        mBackground.setBackgroundResource(R.drawable.ic_heart_scale_ambient);
        mTextView.setTextColor(Color.WHITE);
        mTextView.getPaint().setAntiAlias(false);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        mBackground.setBackgroundResource(R.drawable.ic_heart_scale);
        mTextView.setTextColor(Color.GREEN);
        mTextView.getPaint().setAntiAlias(true);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        changeText(true);
    }
}
