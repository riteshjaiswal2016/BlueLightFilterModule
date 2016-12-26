package com.example.vaio.filter;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherService extends Service {
    Timer timer;
    static String TAG ="tag";
    static int temp=0;
    static double windSpeed =0;
    PowerManager.WakeLock wakeLock;

    public WeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    Handler handler =new Handler(){
    @Override
    public void handleMessage(Message msg) {
        Toast.makeText(MainActivity.context,"Internet Connection Problem",Toast.LENGTH_SHORT).show();
    }
};


    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();


        timer = new Timer();

        //Set the schedule function and rate
        timer.scheduleAtFixedRate(new TimerTask() {

        @Override
        public void run() {

            URL url;
            HttpURLConnection httpURLConnection;
            int data;

            String result ="";

            try {
                url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + DownloadStarter.lat.toString() + "&lon=" + DownloadStarter.lon.toString() + "&appid=da0bb2167b5ecda81fa4009668f0a970");

                httpURLConnection =(HttpURLConnection)url.openConnection();
                httpURLConnection.setReadTimeout(10000); // millis
                httpURLConnection.setConnectTimeout(15000); // millis
                Log.i(TAG,"htt ");

                InputStream is = httpURLConnection.getInputStream();
                Log.i(TAG,"got is");
                InputStreamReader isr = new InputStreamReader(is);

                data = isr.read();

                while(data != -1) {
                    char c = (char) data;
                    result += c;
                    data = isr.read();
                }
                Log.i(TAG,"result ready");

                JSONObject jsonObject =new JSONObject(result);
                JSONObject mainObject = new JSONObject(jsonObject.getString("main"));

                JSONObject windObject = new JSONObject(jsonObject.getString("wind"));

                windSpeed = windObject.getDouble("speed");
                temp = mainObject.getInt("temp");

                Log.i(TAG,"temp="+temp+" and speed="+windSpeed);


            } catch (Exception e) {
                handler.sendEmptyMessage(0);
                Log.i(TAG,"error");
            }

        }
        },
        //Set how long before to start calling the TimerTask (in milliseconds)
        0,
        //Set the amount of time between each execution (in milliseconds)
        20*1000);
    }


    @Override
    public void onDestroy() {
        timer.cancel();
        wakeLock.release();
    }
}
