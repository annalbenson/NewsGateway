package com.annabenson.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private static final String ACTION_NEWS_STORY = "ANS";
    private static final String ACTION_MSG_TO_SVC = "AMTS";

    private MainActivity mainActivity = this;

    private NewsReceiver newsReceiver;


    // HashMap for Source Names --> Source Objects
    private HashMap<String,Source> sourceHashMap = new HashMap<>(); // <K is key type, V is value type>
    // ArrayList of Source Names (used to populate drawer list)
        // upper left hand
    private ArrayList<String> sourceNamesList = new ArrayList<>();

    // HashMap for Category --> ArrayList of Source Name String
    private HashMap<String, ArrayList<String> > categoryHashMap = new HashMap<>();


    // PageViewer & Fragments
    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;


    // drawer attributes
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private Menu opt_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("News Gateway");

        //categoryList = new ArrayList<>();


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

        drawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_item, sourceNamesList));


        // Drawer Item Selected
        drawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // set ViewPager's background to null
                        pager.setBackground(null);

                        // set the current news source name to the selected source (using the list of source names and the selected index
                        String current = sourceNamesList.get(position);
                        getSupportActionBar().setTitle(current);

                        // create an intent ACTION_MSG_TO_SVC
                        Intent intentClick = new Intent();
                        intentClick.setAction(ACTION_MSG_TO_SVC);

                        // add the selected source object (use the source map and the source name to get the object) as an extra to the intent
                        Source source = sourceHashMap.get(sourceNamesList.get(position));

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("source", source);
                        intentClick.putExtras(bundle);

                        // broadcast the intent
                        sendBroadcast(intentClick);

                        // close the drawer
                        drawerLayout.closeDrawer(drawerList);

                        // END
                        Log.d(TAG, "onItemClick: Source Clicked: " + sourceNamesList.get(position));
                    }
                } // end new
        );



        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        // Setup supportActionBar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Setup PageViewer and Adapter

        fragments = new ArrayList<>();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);


        // Crete NewsSource Downloader Async Task object (with  no news category parameter) and execute)

        new NewsSourceDownloader(mainActivity, "").execute();

        // End onCreate

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        //unregisterReceiver(newsReceiver);

        super.onDestroy();
    }

    // You need the 2 below to make the drawer-toggle work properly:

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        drawerToggle.onConfigurationChanged(newConfig);
    }



    // You need the below to open the drawer when the toggle is clicked
    // Same method is called when an options menu item is selected.

    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: drawerToggle " + item);
            return true;
        }

        // this is effectively the else
        Log.d(TAG, "onOptionsItemSelected: else");
        //countryList is the list of countries --> sourceNamesList
        // countryData is the hashmap        --> sourceHashMap

        String title = (String) item.getTitle(); // This is a category title
        Log.d(TAG, "onOptionsItemSelected: item.getTitle() is " + title);
        //String sourceId = itemSource.getName();
        // sourceId will be the key? maybe sourceName?
        // Either way, need to do a get call on Hashmap
        //sourceHashMap.get(title);
        ArrayList<String> temp = new ArrayList<>(categoryHashMap.get(title)); // what does this return? do I need to cast it for addAll


        sourceNamesList.clear();
        //sourceNamesList.addAll(sourceHashMap.get(item.getTitle()));

        sourceNamesList.addAll(temp);

        ((ArrayAdapter) drawerList.getAdapter()).notifyDataSetChanged();

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

        // mainActivity.setTitle(current news source);

        // For each fragment in the PageAdapter (use adapter's getCount()
            // notify hte adapter about the change in position (i)

        // Clear the fragments list

        // For each Articles in the list passed in 0..n
            // make a new Fragment using news article i
            // notify PageAdapter
        // set ViewPager's current item to item 0


        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();

        for (int i = 0; i < articleList.size(); i++) {
            String src = articleList.get(0).getTitle();
            Log.d(TAG, "reDoFragments: XYZ" + src);
            //fragments.add(MyFragment.newInstance(src + ", Item #" + (i+1) + " of "));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);


    }


    public void setSources(ArrayList<Source> sources, ArrayList<String> categories){

        // clear the source map (HashMap of source names to Source objects
        // clear the list of source names (used to populate the drawer list)
        sourceHashMap.clear();
        sourceNamesList.clear();
        categoryHashMap.clear(); // ed




        Source source; String category; String name;
        // fill the list of sources (used to populate the drawer list) using names of sources passed in
        // fill the source map with each new source name (key) and the source object (value)
        // not in flow: populate categoryHashMap based on categories of sources
        for (int i = 0; i < sources.size(); i++ ){
            source = sources.get(i);
            category = source.getCategory();
            Log.d(TAG, "setSources: category: " + category);
            name = source.getName();
            Log.d(TAG, "setSources: name: " + name);
            sourceNamesList.add(name);
            sourceHashMap.put(name, source);

            if(! categoryHashMap.containsKey(category)){
                Log.d(TAG, "setSources: no key, adding key " + category);
                categoryHashMap.put(category, new ArrayList<String>());
            }
            categoryHashMap.get(category).add(name);
        }

        //categoryHashMap.put("All", sources);

        // if the activity's category list is null, set it to a new array using the list of categories passed in
            // (Add "all" as the first item in that list)

        /*
        if(categoryList == null){
            Log.d(TAG, "setSources: == null");
            categoryList = new ArrayList<>();
            categoryList.add("all");
            for(int i = 0; i < categories.size(); i++){
                categoryList.add(categories.get(i));
            }
        }
        */


        // Not in Flow chart, see updateData in Geography MA
        Collections.sort(categories);
        for(String s : categories){
            //Log.d(TAG, "setSources: adding to menu " + s);
            opt_menu.add(s);
        }


        // notify the drawer's array adapter that the dataset has changed
        ((ArrayAdapter) drawerList.getAdapter()).notifyDataSetChanged();
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

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }




}
