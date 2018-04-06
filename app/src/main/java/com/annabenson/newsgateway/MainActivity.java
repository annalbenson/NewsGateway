package com.annabenson.newsgateway;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start Service (News Service)

    }


    // Flow Charts page 2/6--> News Receiver "Class inside Main Activity"
    public class NewsReceiver extends Service {

        private static final String TAG = "NewsReceiver";


        @Override
        public IBinder onBind(Intent arg0){
            Log.i(TAG, "onBind: Service onBind");
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId){
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        }


        //onReceive(){

        //}



    }



}
