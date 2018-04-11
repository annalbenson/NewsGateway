package com.annabenson.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.ArrayList;

/**
 * Created by Anna on 4/10/2018.
 */

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private static final String ACTION_MSG_TO_SERVICE = "AMTS";
    private static final String ACTION_NEWS_STORY = "ANS";
    private boolean running = true;

    private ArrayList<Article> storylist;

    @Override
    public IBinder onBind(Intent intent){ return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        // Create IntentFilter for ACTION_MSG_TO_SERVICE
        IntentFilter intentFilter = new IntentFilter(ACTION_MSG_TO_SERVICE);

        // Register Service Receiver using intentFilter
        ServiceReceiver serviceReceiver = new ServiceReceiver();
        registerReceiver(serviceReceiver,intentFilter);

        // Create and start services thread
        new Thread(new Runnable() {
            @Override
            public void run() {

                while(running){
                    while(storylist.isEmpty()){
                        // sleep 250 millis
                        try {Thread.sleep(250);}
                        catch(InterruptedException e){
                            e.printStackTrace();}
                    }

                    // Not Empty: Make a new intent object with ACTION_NEWS_STORY action
                    Intent intent1 = new Intent();
                    intent1.setAction(ACTION_NEWS_STORY);
                    intent1.putExtra("storylist", storylist);
                    // broadcast the intent (Continue with Pg 2 "A"

                    sendBroadcast(intent1);
                    // clear the storylist
                }
            }
        }).start();

        return  Service.START_STICKY;
    }


    class ServiceReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            //
        }
    }

}
