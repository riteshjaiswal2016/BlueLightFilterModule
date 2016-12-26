package com.example.vaio.filter;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

public class SensorService extends Service implements SensorEventListener{
    SensorManager sm;
    Sensor lightSensor;
    final static String TAG ="tag";
    double brightnessPercent;
    int brightnessValue;

    public SensorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor != null) {
            sm.registerListener(
                    this,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            Log.i(TAG, " sensor regstr");
        }
        //Registering Sensor Manager
    }

    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MainActivity.luxText.setText(""+msg.arg1);
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            Log.i(TAG, "before sensor text chg " + event.values[0]);

            Message msg = Message.obtain();
            msg.arg1= (int) event.values[0];
            handler.sendMessage(msg);


            if((WeatherService.temp<=295 && WeatherService.temp !=0) || WeatherService.temp >=305 || WeatherService.windSpeed >=6)
                weatherMethod((int)event.values[0]);

            else
                normalMethod((int)event.values[0]);


            //This if else is designed in accordance with the formula submitted in Report.

        }

    }


    public void normalMethod(int ambientLight){
        if(ambientLight<4064) {
            if(ambientLight==0)
                brightnessPercent=25;
            else
                brightnessPercent = ((9.62 * Math.log(ambientLight) + 20));

            brightnessValue = (int) (2.55 * brightnessPercent);
            Log.i(TAG," "+brightnessValue+" "+brightnessPercent);
            setScreenBrightness(brightnessValue);
            Log.i(TAG,"Normal Method Used");
        }
        else{
            setScreenBrightness(255);
        }

    }


    public void weatherMethod(int ambientLight){
        if(ambientLight<99111) {
            if(ambientLight==0)
                brightnessPercent=20;
            else
                brightnessPercent = ((6.78 * Math.log(ambientLight) + 22));

            brightnessValue = (int) (2.55 * brightnessPercent);
            Log.i(TAG," "+brightnessValue+" "+brightnessPercent);
            setScreenBrightness(brightnessValue);
            Log.i(TAG,"Weather Method Used");
        }
        else{
            setScreenBrightness(255);
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
    public void onDestroy() {
        sm.unregisterListener(this);
        lightSensor=null;
        sm=null;
    }
}
