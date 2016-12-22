package com.example.vaio.filter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

//Removing Overlay
public class MyServiceReverse extends BroadcastReceiver {
    final static String TAG ="tag";
    Timer timer;

    public MyServiceReverse() {
    }

    Handler handler = new Handler(){
        int currentCT=1200;
        int opacity=40;
        int RGB[];

        @Override
        public void handleMessage(Message msg) {
            if(currentCT<=6000){
                timer.cancel();
                MyService.wm.removeView(MyService.linearLayout);
            }
            else {
                RGB = colorTemperatureToRGB(currentCT);

                MyService.linearLayout.setBackgroundColor(Color.argb(opacity, RGB[0], RGB[1], RGB[2]));

                Log.i(TAG,"changing reverse");

                currentCT += 60;

                if(opacity>0)
                    opacity-=1;
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
            if(MyService.linearLayout==null && MyService.wm==null)
                return;
            //Dont try to remove overlay if it doesnt exist

            timer = new Timer();

            //Set the schedule function and rate
            timer.scheduleAtFixedRate(new TimerTask() {
                                          @Override
                                          public void run() {
                                              handler.sendEmptyMessage(0);
                                          }
                                      },
                    //Set how long before to start calling the TimerTask (in milliseconds)
                    0,
                    //Set the amount of time between each execution (in milliseconds)
                    60000);
        }


    public int[] colorTemperatureToRGB(double kelvin){
        double temp = kelvin / 100;

        double red, green, blue;

        if( temp <= 66 ){

            red = 255;

            green = temp;
            green = 99.4708025861 * Math.log(green) - 161.1195681661;

            if( temp <= 19)
                blue = 0;
            else {
                blue = temp-10;
                blue = 138.5177312231 * Math.log(blue) - 305.0447927307;
            }
        }
        else {
            red = temp - 60;
            red = 329.698727446 * Math.pow(red, -0.1332047592);

            green = temp - 60;
            green = 288.1221695283 * Math.pow(green, -0.0755148492 );

            blue = 255;
        }

        int r = (int)red;
        int g = (int)green;
        int b = (int)blue;

        r=clamp(r,0,255);
        g=clamp(g,0,255);
        b=clamp(b,0,255);

        int functionReturnRGB[]={r,g,b};

        return functionReturnRGB;
    }


    int clamp(int x,int min,int max ) {
        if(x<min){ return min; }
        if(x>max){ return max; }

        return x;
    }
}
