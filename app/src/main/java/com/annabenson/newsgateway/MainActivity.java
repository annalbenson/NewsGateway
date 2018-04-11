package com.annabenson.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private static final String ACTION_NEWS_STORY = "ANS";

    private MainActivity mainActivity = this;

    private NewsReceiver newsReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start Service (News Service)

        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);

        // Create IntentFilter for ACTION_NEWS_STORY messages from the service

        IntentFilter intentFilter = new IntentFilter(ACTION_NEWS_STORY);

        //Register a NewsReceiver broadcast receiver object using the intent filter

        newsReceiver = new NewsReceiver();
        registerReceiver(newsReceiver, intentFilter);

        // Setup drawer, adapter, and toggle

        // Setup supportActionBar

        // Setup PageViewer and Adapter

        // Crete NewsSource Downloader Async Task object (with  no news category parameter) and execute)

        new NewsSourceDownloader(mainActivity, "").execute();

        // End onCreate

    }

    public void reDoFragments(Object [] list){

        // set Activity Title to the string that holds the name of the current news source


        // For each fragment in the PageAdapter (use adapter's getCount()
            // notify hte adapter about the change in position (i)

        // Clear the fragments list

        // For each Articles in the list passed in 0..n
            // make a new Fragment using newsa rticle i
            // notify PageAdapter
        // set ViewPager's current item to item 0
    }


    public void setSources(ArrayList<Source> sources, ArrayList<String> categories){

        // clear the source map (HashMap of source names to Source objects

        // clear the list of source names (used to populate the drawer list

        // fill the list of sources (used to populate the drawer list) using names of sources passed in

        // fill the source map with each new source name (key) and hte source object (value)

        // if the activity's category list is null, set it to a new array using the list of categories passed in
            // (Add "all" as the first item in that list)

        // notify the drawer's array adapter that the dataset has changed

        // END

    }

    // Drawer Item Selected
    public void onItemClick(){

    }

    // Flow Charts page 2/6--> News Receiver "Class inside Main Activity"
    public class NewsReceiver extends BroadcastReceiver {

        private static final String TAG = "NewsReceiver";

        @Override
        public void onReceive(Context context, Intent intent){

            if(intent.getAction().equals(ACTION_NEWS_STORY)){

                // Get the article list from intent's extras
                if(intent.hasExtra("list")){
                    //ArrayList<Article>
                    // get list from intent
                    // call reDoFragments on the list
                }
                else{
                    Log.d(TAG, "onReceive: No list extra");
                }
            }
            else{
                Log.d(TAG, "onReceive: Non recognised action type " + intent.getAction());
            }
        }


    }



}
