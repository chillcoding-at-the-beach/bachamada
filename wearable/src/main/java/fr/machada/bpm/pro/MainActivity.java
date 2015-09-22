package fr.machada.bpm.pro;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                initText();
            }
        });
        initHeartRateSensor();

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
        mTextView.setText(String.format("%d\nBPM", 80));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int value = 0;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            if ((int) sensorEvent.values[0] > 0) {
                value = (int) sensorEvent.values[0];
                changeText(value);

            }
        }
    }

    private void changeText(int value) {
        int greenStep = 90;
        int yelloStep = 150;
        int orangStep = 170;
        int lowStep = 50;
        if (value < lowStep)
            mTextView.setTextColor(getResources().getColor(R.color.yellow));
        else if (value < greenStep)
            mTextView.setTextColor(getResources().getColor(R.color.green));
        else if (value < yelloStep)
            mTextView.setTextColor(getResources().getColor(R.color.yellow));
        else if (value < orangStep)
            mTextView.setTextColor(getResources().getColor(R.color.orange));
        else
            mTextView.setTextColor(getResources().getColor(R.color.reed));
        mTextView.setText(String.format("%d\nBPM", value));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
