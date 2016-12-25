package com.example.vaio.filter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

//Creating overlay
public class MyService extends Service {
    static LinearLayout linearLayout = null;
    final static String TAG = "tag";
    Timer timer;
    static WindowManager wm = null;


    Handler handler = new Handler() {
        int normalCT = 6000;
        int opacity = 0;
        //Normat Color Temp of overlay for clear screen

        int RGB[];
        //array to recieve data from colorTemperatureToRGB() method

        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1)
                wm.addView(linearLayout, (WindowManager.LayoutParams) msg.obj);

            else {

                if (normalCT <= 1200) {
                    timer.cancel();

                    //Reduce color temp upto 1200K then stop the timer.
                } else {
                    RGB = colorTemperatureToRGB(normalCT);

                    linearLayout.setBackgroundColor(Color.argb(opacity, RGB[0], RGB[1], RGB[2]));
                    //changing background color of overlay

                    Log.i(TAG, "changing" + RGB[0] + " " + RGB[1] + " " + RGB[2]);

                    normalCT -= 60;
                    //Each time reducing color temp by 60K

                    if (opacity < 30)
                        opacity += 1;
                    Log.i(TAG, "" + opacity);
                    //Increasing opacity to 40 max
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        linearLayout = new LinearLayout(MainActivity.context);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT);

        wm = (WindowManager) MainActivity.context.getSystemService(MainActivity.context.WINDOW_SERVICE);

        Message msg = Message.obtain();
        msg.arg1 = 1;
        msg.obj = params;
        handler.sendMessage(msg);

        Log.i(TAG, "overlayed");

        timer = new Timer();
        //Setting timer which creates overlay in 30min gradually as suggested in Report

        //Set the schedule function and rate
        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
                                          handler.sendEmptyMessage(0);
                                          //pinging handler to modify overlay
                                      }
                                  },
                0,
                600);
        //60 Second between each change in overlay opacity and color temp.

    }

    //It converts Color Temp. in kelvin to values of R G B(array)
    public int[] colorTemperatureToRGB(double kelvin) {
        double temp = kelvin / 100;

        double red, green, blue;

        if (temp <= 66) {

            red = 255;

            green = temp;
            green = 99.4708025861 * Math.log(green) - 161.1195681661;

            if (temp <= 19)
                blue = 0;
            else {
                blue = temp - 10;
                blue = 138.5177312231 * Math.log(blue) - 305.0447927307;
            }
        } else {
            red = temp - 60;
            red = 329.698727446 * Math.pow(red, -0.1332047592);

            green = temp - 60;
            green = 288.1221695283 * Math.pow(green, -0.0755148492);

            blue = 255;
        }

        int r = (int) red;
        int g = (int) green;
        int b = (int) blue;

        r = clamp(r, 0, 255);
        g = clamp(g, 0, 255);
        b = clamp(b, 0, 255);

        int functionReturnRGB[] = {r, g, b};

        return functionReturnRGB;
    }


    int clamp(int x, int min, int max) {
        if (x < min) {
            return min;
        }
        if (x > max) {
            return max;
        }

        return x;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        timer.cancel();
    }
}
