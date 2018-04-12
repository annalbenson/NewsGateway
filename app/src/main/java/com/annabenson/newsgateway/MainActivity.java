package com.annabenson.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private static final String ACTION_NEWS_STORY = "ANS";

    private MainActivity mainActivity = this;

    private NewsReceiver newsReceiver;


    // HashMap for Source Names --> Source Objects
    private HashMap<String,Source> sourceHashMap ; // <K is key type, V is value type>
    // ArrayList of Source Names and ArrayList of Sources (used to populate drawer list)
    private ArrayList<String> sourceNamesList;
    //
    private ArrayList<String> categoryList;


    // drawer attributes
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private ArrayAdapter arrayAdapter;

    private ActionBarDrawerToggle drawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sourceHashMap = new HashMap<>();
        sourceNamesList = new ArrayList<>();
        categoryList = new ArrayList<>();



        // Start Service (News Service)
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);

        // Create IntentFilter for ACTION_NEWS_STORY messages from the service
        IntentFilter intentFilter = new IntentFilter(ACTION_NEWS_STORY);

        //Register a NewsReceiver broadcast receiver object using the intent filter

        newsReceiver = new NewsReceiver();
        registerReceiver(newsReceiver, intentFilter);

        // Setup drawer, adapter, and toggle

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.drawer_list);

        /*
        drawerList.setOnItemClickListener( onItemClick();
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Country c = countryList.get(position);
                        //Intent intent = new Intent(MainActivity.this, CountryDetailActivity.class);
                        //intent.putExtra(Country.class.getName(), c);
                        //startActivity(intent);
                        Log.d(TAG, "onItemClick: Drawer Clicked");
                        drawerLayout.closeDrawer(drawerList);
                    }
                }
        );
        */

        // may have to recreate this when data is updated
        //arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, sourceNamesList);
        //drawerList.setAdapter(arrayAdapter);

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        // Setup supportActionBar

        // Setup PageViewer and Adapter

        // Crete NewsSource Downloader Async Task object (with  no news category parameter) and execute)

        new NewsSourceDownloader(mainActivity, "").execute();

        // End onCreate

    }


    // You need the 2 below to make the drawer-toggle work properly:

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    // You need the below to open the drawer when the toggle is clicked
    // Same method is called when an options menu item is selected.

    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        countryList.clear();
        countryList.addAll(countryData.get(item.getTitle()));

        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        //mDrawerList.setAdapter(new ArrayAdapter<>(this,  R.layout.drawer_item, countryData.get(selection)));

        return super.onOptionsItemSelected(item);

    }

    // You need this to set up the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        opt_menu = menu;
        return true;
    }


    public void reDoFragments(ArrayList<Article> articleList){

        // set Activity Title to the string that holds the name of the current news source

        //mainActivity.setTitle(current news source);

        // For each fragment in the PageAdapter (use adapter's getCount()
            // notify hte adapter about the change in position (i)

        // Clear the fragments list

        // For each Articles in the list passed in 0..n
            // make a new Fragment using news article i
            // notify PageAdapter
        // set ViewPager's current item to item 0
    }


    public void setSources(ArrayList<Source> sources, ArrayList<String> categories){

        // clear the source map (HashMap of source names to Source objects
        // clear the list of source names (used to populate the drawer list)
        sourceHashMap.clear();
        sourceNamesList.clear();

        // fill the list of sources (used to populate the drawer list) using names of sources passed in
        // fill the source map with each new source name (key) and the source object (value)
        for (int i = 0; i < sources.size(); i++ ){
            sourceNamesList.add(sources.get(i).getName());
            sourceHashMap.put(sources.get(i).getName(), sources.get(i));
        }

        // if the activity's category list is null, set it to a new array using the list of categories passed in
            // (Add "all" as the first item in that list)

        if(categoryList == null){
            categoryList = new ArrayList<>();
            categoryList.add("all");
            for(int i = 0; i < categories.size(); i++){
                categoryList.add(categories.get(i));
            }
        }

        // notify the drawer's array adapter that the dataset has changed
        //arrayAdapter.notifyDataSetChanged();
        // END

    }

    // Drawer Item Selected
    public void onItemClick(){

        // set ViewPager's background to null

        // set the current news source name to the selected source (using the list of source names and the selected index

        // create an intent ACTION_MSG_TO_SVC

        // add the selected source object (use the source map and the source name to get the object) as an extra to the intent

        // broadcast the intent

        // close the drawer
        drawerLayout.closeDrawer(drawerList);

        // END


    }

    // Flow Charts page 2/6--> News Receiver "Class inside Main Activity"
    public class NewsReceiver extends BroadcastReceiver {

        private static final String TAG = "NewsReceiver";

        @Override
        public void onReceive(Context context, Intent intent){

            if(intent.getAction().equals(ACTION_NEWS_STORY)){

                try{
                    // get article list from intent's extras
                        // getSerializable? Bundle?
                    // call reDoFragments
                }
                catch (Exception e){
                    Log.d(TAG, "onReceive: " + e.getMessage());
                    e.printStackTrace();
                }

            }
            else{
                Log.d(TAG, "onReceive: Non recognised action type " + intent.getAction());
            }
        }


    }



}
