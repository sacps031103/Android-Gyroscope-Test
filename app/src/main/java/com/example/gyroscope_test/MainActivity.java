package com.example.gyroscope_test;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager mSensorManager;
    private TextView message;
    ImageView img;
    private Button TestButton;
    private ProgressBar mProgressBar; //進度條
    float yGyroscope ;
    //
    float zGyroscope ;
    //
    float xGyroscope ;
    double magnitude = 0;//向量
    int vectorMAX=300;
    private ObjectAnimator anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        message = (TextView) findViewById(R.id.message);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        img = (ImageView) findViewById( R.id.img);
        anim = ObjectAnimator.ofFloat(img,"rotation",0f,45f,-30f,0f);
        anim.setDuration(500);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();
        mProgressBar.setMax(100);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }
    @Override
    public void onResume() {
        super.onResume();
        // 為系統的方向傳感器註冊監聽器
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE ),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        // 取消註冊
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        // 取消註冊
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 獲取觸發event的傳感器類型
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_GYROSCOPE:
                float[] value = event.values;
                xGyroscope = value[0];
                yGyroscope = value[1];
                zGyroscope = value[2];
                magnitude = Math.sqrt(xGyroscope * xGyroscope + yGyroscope * yGyroscope + zGyroscope * zGyroscope);//向量求模
                download((int) ((magnitude*100/vectorMAX)*100));//%
                Log.v(TAG, "\n  向量  "+magnitude*100);
                if (vectorMAX < magnitude*100) {
                    message.setText("OK");
                }else {
                    message.setText("向量 :"+magnitude*100);
                }
        }
        Log.v(TAG, "\n  xGyroscope  "+xGyroscope+ "  yGyroscope  "+ yGyroscope+"  zGyroscope  "+zGyroscope);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:    //返回键
                return true;   //这里由于break会退出，所以我们自己要处理掉 不返回上一层
        }
        return super.onKeyDown(keyCode, event);
    }
    public void download(final int S){
        mProgressBar.setProgress(S);
    }
}
