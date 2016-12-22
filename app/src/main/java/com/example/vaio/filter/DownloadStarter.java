package com.example.vaio.filter;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
//This class is finding user location data longitude and lattitude
public class DownloadStarter extends BroadcastReceiver {
    final String TAG ="tag";
    String provider;
    LocationManager lm;
    Double lat,lon;
    static Context context2;

    public DownloadStarter() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        provider = lm.getBestProvider(new Criteria(), false);

        context2 = context;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute("http://api.openweathermap.org/data/2.5/weather?lat="+lat.toString()+"&lon="+lon.toString()+"&appid=da0bb2167b5ecda81fa4009668f0a970");

    }
}
