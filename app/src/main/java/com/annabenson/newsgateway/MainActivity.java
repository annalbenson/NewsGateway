package com.annabenson.newsgateway;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.graphics.Color.BLACK;


public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private static final String ACTION_NEWS_STORY = "ANS";
    private static final String ACTION_MSG_TO_SERVICE = "AMTS";

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

    private String currentCategory = "";
    private String currentSource = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("News Gateway");



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

        drawerList.setBackgroundResource(R.color.secondaryColor);


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

                        // create an intent ACTION_MSG_TO_SERVICE
                        Intent intentClick = new Intent();
                        intentClick.setAction(ACTION_MSG_TO_SERVICE);
                        Log.d(TAG, "onItemClick: creating intent MSG to SERVICE");

                        // add the selected source object (use the source map and the source name to get the object) as an extra to the intent
                        Source source = sourceHashMap.get(sourceNamesList.get(position));

                        Log.d(TAG, "onItemClick: adding selected source id " + source.getId());
                        // more overhead to pass source so just pass ID instead

                        //Bundle bundle = new Bundle();
                        //bundle.putSerializable("source", source);
                        //intentClick.putExtras(bundle);
                        intentClick.putExtra("sourceID", source.getId());

                        // broadcast the intent
                        sendBroadcast(intentClick);
                        Log.d(TAG, "onItemClick: broadcast sent");

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

    /*
    @Override
    protected void onSaveInstanceState(Bundle outState){

        Log.d(TAG, "onSaveInstanceState: saving state");
        // need to save category of current fragment
        // also need which fragment (need fragment numbering)

        int currentFragment = pager.getCurrentItem();
        Log.d(TAG, "onSaveInstanceState: current fragment is " + currentFragment);
        //Fragment f = pageAdapter.getItem(currentFragment);
        //String articleTitle = f.getArguments().getString("title");
        //articleTitle

        // assuming you're not changing the category and then rotating
        // assuming you're just viewing
        String currentSourceName = (String) getSupportActionBar().getTitle(); // get name


        Log.d(TAG, "onSaveInstanceState: current source name in bar is " + currentSourceName);
        Source source = sourceHashMap.get(currentSourceName); // get source
        Log.d(TAG, "onSaveInstanceState: source is non null: " + source);
        String currentSourceID = source.getId();
        Log.d(TAG, "onSaveInstanceState: currentSourceID is " + currentSourceID);

        outState.putString("currentCategory", currentCategory);
        outState.putInt("currentFragment", currentFragment);
        outState.putString("currentSourceName", currentSourceName);
        outState.putString("currentSourceID", currentSourceID );

        super.onSaveInstanceState(outState);
    }
    */

    /*
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        // Need to load category into drawer
        // Need to select news source and load fragment
        // Need to make viewer on specific fragment

        String category = savedInstanceState.getString("currentCategory");
        Log.d(TAG, "onRestoreInstanceState: loaded category is " + category);
        String sourceName = savedInstanceState.getString("currentSourceName");
        Log.d(TAG, "onRestoreInstanceState: loaded name is " + sourceName);
        String sourceID = savedInstanceState.getString("currentSourceID");
        Log.d(TAG, "onRestoreInstanceState: loaded id is " + sourceID);
        getSupportActionBar().setTitle(sourceName);
        Log.d(TAG, "onRestoreInstanceState: bar set to " + sourceName);




        // use sourceID to get article fragments

        /*
        IntentFilter intentFilter = new IntentFilter(ACTION_NEWS_STORY);
        newsReceiver = new NewsReceiver();
        registerReceiver(newsReceiver, intentFilter);

        Log.d(TAG, "onRestoreInstanceState: using sourceID to get article fragments");
        //pager.setBackground(null);
        Intent intent = new Intent();
        intent.setAction(ACTION_MSG_TO_SERVICE);
        intent.putExtra("sourceID", sourceID);
        sendBroadcast(intent);
        */

        /*
        // similar code to onOptionsItemSelected
        ArrayList<String> temp = new ArrayList<>(categoryHashMap.get(category));
        sourceNamesList.clear();
        sourceNamesList.addAll(temp);
        ((ArrayAdapter) drawerList.getAdapter()).notifyDataSetChanged();

        // need to set fragment to the fragment they were on
        //int pos = savedInstanceState.getInt("currentFragment");
        //pager.setCurrentItem(pos);
    }
    */
    


    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(mainActivity, NewsService.class);
        stopService(intent);
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
        Log.d(TAG, "onOptionsItemSelected: else, category selected from options menu");
        //countryList is the list of countries --> sourceNamesList
        // countryData is the hashmap        --> sourceHashMap

        String category = (String) item.getTitle(); // This is a category title
        Log.d(TAG, "onOptionsItemSelected: item.getTitle() is " + category);
        currentCategory = category; // for save instance state



        //String sourceId = itemSource.getName();
        // sourceId will be the key? maybe sourceName?
        // Either way, need to do a get call on Hashmap
        //sourceHashMap.get(title);
        ArrayList<String> temp = new ArrayList<>(categoryHashMap.get(category)); // what does this return? do I need to cast it for addAll


        sourceNamesList.clear();
        //sourceNamesList.addAll(sourceHashMap.get(item.getTitle()));

        sourceNamesList.addAll(temp);

        ((ArrayAdapter) drawerList.getAdapter()).notifyDataSetChanged();


        // Create News Source Downloader Async Task, passing "this" and news category; execute
        //new NewsSourceDownloader(this,category).execute();
        // Doesn't work if I do this here, do not know why


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

        Log.d(TAG, "reDoFragments: started");
        // set Activity Title to the string that holds the name of the current news source


        //getSupportActionBar().setTitle(current news source);

        // For each fragment in the PageAdapter (use adapter's getCount()
            // notify the adapter about the change in position (i)
        //Log.d(TAG, "reDoFragments: count " + pageAdapter.getCount());
        for(int i = 0; i < pageAdapter.getCount(); i++ ){
            pageAdapter.notifyChangeInPosition(i);
        }

        // Clear the fragments list
            fragments.clear();

        // For each Articles in the list passed in 0..n
            // make a new Fragment using news article i

        int totalArticles = articleList.size();
        for(int i = 0; i < totalArticles; i++){
            Article article = articleList.get(i);

            String src = articleList.get(i).getTitle();
            //Log.d(TAG, "reDoFragments: Article title " + src);

            fragments.add(MyFragment.newInstance( mainActivity, article.getTitle(), article.getPublishedAt(), article.getAuthor(), article.getUrlToImage(), article.getDescription(), i, totalArticles, article.getUrlToArticle() ));
        }


        // notify PageAdapter
        pageAdapter.notifyDataSetChanged();
        // set ViewPager's current item to item 0
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
            //Log.d(TAG, "setSources: category: " + category);
            name = source.getName();
            //Log.d(TAG, "setSources: name: " + name);
            sourceNamesList.add(name);
            sourceHashMap.put(name, source);

            if(! categoryHashMap.containsKey(category)){
                //Log.d(TAG, "setSources: no key, adding key " + category);
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

            switch (intent.getAction()){
                case ACTION_NEWS_STORY:
                    try{
                        // get article list (storylist) from intent's extras
                        Bundle bundle = intent.getExtras();
                        ArrayList<Article> articles = (ArrayList<Article>) bundle.getSerializable("storylist");
                        Log.d(TAG, "onReceive: Articles received by onReceive");
                        for(int i = 0; i <articles.size(); i++){
                            Log.d(TAG, "onReceive: title: " + articles.get(i).getTitle());
                        }

                        // call reDoFragments, passing list of articles
                        reDoFragments(articles);

                    }
                    catch (Exception e){
                        Log.d(TAG, "onReceive: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

            } // end switch

        } // end onReceive


    } // end receiver class

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

        /*
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
