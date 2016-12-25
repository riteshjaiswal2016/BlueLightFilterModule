package com.example.vaio.filter;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class DownloadStarter extends Service {
    public DownloadStarter() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    final String TAG ="tag";
    String provider;
    LocationManager lm;
    Double lat,lon;
    DownloadTask downloadTask;
    //static Context context2;

    @Override
    public void onCreate() {
        super.onCreate();

        lm = (LocationManager) MainActivity.context.getSystemService(MainActivity.context.LOCATION_SERVICE);
        provider = lm.getBestProvider(new Criteria(), false);

        //context2 = context;

        if (ActivityCompat.checkSelfPermission(MainActivity.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = lm.getLastKnownLocation(provider);

        lat = location.getLatitude();
        lon = location.getLongitude();
        Log.i(TAG,"before any if else");
        downloadTask = new DownloadTask();
        downloadTask.execute("http://api.openweathermap.org/data/2.5/weather?lat="+lat.toString()+"&lon="+lon.toString()+"&appid=da0bb2167b5ecda81fa4009668f0a970");
        Log.i(TAG,"after exe");

    }


    @Override
    public void onDestroy() {
        downloadTask.onCancelled();
        downloadTask=null;
    }
}
