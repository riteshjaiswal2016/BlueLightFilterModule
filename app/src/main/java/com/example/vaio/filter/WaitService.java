package com.example.vaio.filter;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//Our aim is to trigger alarm manager on each day at time 00:01 and get location of user
//and repeat this alarm after each 24*60*60*1000 mills (24hrs)
public class WaitService extends Service {
    static final String TAG = "tag";

    public WaitService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);

        Intent intent =new Intent(WaitService.this,DownloadStarter.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(WaitService.this,0,intent,0);
        //pendingIntent open DownloadStarter which find location

        Date date2 =new Date();
        //This is current day date and time

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String dateInString = sdf.format(date2);
        //it is date in yyyy-MM-dd format

        String dateTimeInString = dateInString+" 00:01";
        //it is the current day date with 00:01 time appended.

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date date3=null;
        try {
            date3= sdf2.parse(dateTimeInString);
            //current date with 00:01 time in format yyyy-MM-dd HH:mm
        } catch (ParseException e) {
            e.printStackTrace();
        }

        am.setRepeating(am.RTC_WAKEUP,date3.getTime(),24*60*60*1000,pendingIntent);
        //date3.getTime gives Unix UTC format time of current date 00:01 time
        //It sets alarm at 00:01 and repeats after every 24hr
    }

}
