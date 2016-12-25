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
import android.view.animation.Interpolator;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    Intent intent;
    static Context context;
    static TextView riseText, setText, luxText;
    //Textview for Sunrise Time,Sunset Time and ambient Brightness
    final static String TAG = "tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();

        riseText = (TextView) findViewById(R.id.riseText);
        setText = (TextView) findViewById(R.id.setText);
        luxText = (TextView) findViewById(R.id.luxText);
        Log.i(TAG,"bfore started serviec");
        intent = new Intent(this, WaitService.class);
        startService(intent);

        Log.i(TAG,"started serviec");
        Intent intent1 =new Intent(this,SensorService.class);
        startService(intent1);
    }

    public void onExit(View view){
        stopService(new Intent(this,SensorService.class));
        stopService(new Intent(this,MyServiceReverse.class));
        stopService(new Intent(this,MyService.class));
        stopService(new Intent(this,WaitService.class));
        stopService(new Intent(this,DownloadStarter.class));
        finish();
    }

    public void onBackground(View view){
        finish();
    }

}


