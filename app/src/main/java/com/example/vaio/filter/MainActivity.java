package com.example.vaio.filter;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener{
    Intent intent;
    static Context context;
    static TextView riseText, setText, luxText;
    //Textview for Sunrise Time,Sunset Time and ambient Brightness
    final static String TAG = "tag";
    SensorManager sm;
    Sensor lightSensor;
    double brightnessPercent;
    int brightnessValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();

        riseText = (TextView) findViewById(R.id.riseText);
        setText = (TextView) findViewById(R.id.setText);
        luxText = (TextView) findViewById(R.id.luxText);

        intent = new Intent(this, WaitService.class);
        startService(intent);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
        //Unregistering Sensor Manager after pause.
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            Log.i(TAG, "before sensor text chg " + event.values[0]);

            luxText.setText(""+event.values[0]);

            if(event.values[0]<1254) {
                brightnessPercent = ((9.932 * Math.log(event.values[0])) + 27.059);
                brightnessValue = (int) (2.55 * brightnessPercent);
                Log.i(TAG," "+brightnessValue+" "+brightnessPercent);
                setScreenBrightness(brightnessValue);
            }
            else{
                setScreenBrightness(255);
            }
            //This if else is designed in accordance with the formula submitted in Report.

        }

    }

    public void setScreenBrightness(int brightnessValue){
        // Make sure brightness value between 0 to 255
        if(brightnessValue >= 0 && brightnessValue <= 255){
            Settings.System.putInt(
                    this.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightnessValue
            );
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    @Override
    protected void onResume() {
        super.onResume();

        if (lightSensor != null) {
            sm.registerListener(
                    this,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            Log.i(TAG, " sensor regstr");
        }
        //Registering Sensor Manager

    }
}


