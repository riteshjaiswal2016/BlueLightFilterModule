package com.example.vaio.filter;


import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//This deals with JSON result to find sunrise and sunset time in HH:mm format
public class DownloadTask extends AsyncTask<String,Void,String> {
    static final String TAG ="tag";
    static String riseTime=null,setTime=null;
    AlarmManager alarmManager;
    //sunrise and sunset time in HH:mm format

    @Override
    protected String doInBackground(String... urls) {
        URL url;
        HttpURLConnection httpURLConnection;
        int data;

        String result ="";

        try {
            url = new URL(urls[0]);

            httpURLConnection =(HttpURLConnection)url.openConnection();
            httpURLConnection.setReadTimeout(10000); // millis
            httpURLConnection.setConnectTimeout(15000); // millis
            Log.i(TAG,"htt ");

            InputStream is = httpURLConnection.getInputStream();

            InputStreamReader isr = new InputStreamReader(is);

            data = isr.read();

            while(data != -1) {
                char c = (char) data;
                result += c;
                data = isr.read();
            }

            return result;

        } catch (Exception e) {
            return  null;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPostExecute(String downloadData) {
        super.onPostExecute(downloadData);

        if(downloadData==null) {
            Toast.makeText(DownloadStarter.DownloadStarterContext, "Data Download Failed", Toast.LENGTH_SHORT).show();
            new DownloadTask().execute("http://api.openweathermap.org/data/2.5/weather?lat=" + DownloadStarter.lat.toString() + "&lon=" + DownloadStarter.lon.toString() + "&appid=da0bb2167b5ecda81fa4009668f0a970");
        }
        else {
            try {
                JSONObject jsonObject = new JSONObject(downloadData);

                JSONObject suntimesObject = new JSONObject(jsonObject.getString("sys"));

                Date date = new Date();

                SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
                String time = sdf1.format(date);

                long sunRiseLong;
                long sunSetLong;

                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

                alarmManager = (AlarmManager) MainActivity.context.getSystemService(Context.ALARM_SERVICE);

                sunRiseLong = suntimesObject.getLong("sunrise");
                Date date1 = new Date(sunRiseLong * 1000L);
                riseTime = sdf2.format(date1);
                MainActivity.riseText.setText(riseTime);

                sunSetLong = suntimesObject.getLong("sunset");
                Date date2 = new Date(sunSetLong * 1000L);
                setTime = sdf2.format(date2);
                MainActivity.setText.setText(setTime);


                //JSON RESPONSE-
                //{"coord":{"lon":138.93,"lat":34.97},"weather":[{"id":521,"main":"Rain","description":"shower rain","icon":"09d"}
                // ,{"id":701,"main":"Mist","description":"mist","icon":"50d"}]
                // ,"base":"stations","main":{"temp":288.1,"pressure":1016,"humidity":93,"temp_min":284.15,"temp_max":293.15}
                // ,"visibility":14484,"wind":{"speed":3.6,"deg":200},"clouds":{"all":90},"dt":1482376380
                // ,"sys":{"type":1,"id":7618,"message":0.0095,"country":"JP","sunrise":1482356929,"sunset":1482392227},"id":1851632,"name":"Shuzenji","cod":200}


                if ((System.currentTimeMillis() <= date1.getTime() - 60 * 60 * 1000)) {
                    Log.i(TAG, "in if");
                    if (MyService.linearLayout != null && MyService.wm != null) {
                        Intent intent = new Intent(MainActivity.context, MyService.class);
                        MainActivity.context.startService(intent);
                    }

                    Intent intent1 = new Intent(MainActivity.context, MyServiceReverse.class);
                    PendingIntent pendingIntent1 = PendingIntent.getService(MainActivity.context, 0, intent1, 0);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, date1.getTime() - 30 * 60 * 1000, pendingIntent1);

                    Intent intent2 = new Intent(MainActivity.context, MyService.class);
                    PendingIntent pendingIntent2 = PendingIntent.getService(MainActivity.context, 0, intent2, 0);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, date2.getTime() - 30 * 60 * 1000, pendingIntent2);
                } else if (System.currentTimeMillis() <= date2.getTime() - 60 * 60 * 1000 && System.currentTimeMillis() >= date1.getTime()) {
                    Intent intent2 = new Intent(MainActivity.context, MyService.class);
                    PendingIntent pendingIntent2 = PendingIntent.getService(MainActivity.context, 0, intent2, 0);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, date2.getTime() - 30 * 60 * 1000, pendingIntent2);
                } else if (System.currentTimeMillis() >= date2.getTime()) {
                    Intent intent = new Intent(MainActivity.context, MyService.class);
                    MainActivity.context.startService(intent);
                }

            } catch (JSONException e) {
                Toast.makeText(DownloadStarter.DownloadStarterContext, "Data Incompatible", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        alarmManager=null;

    }


}
