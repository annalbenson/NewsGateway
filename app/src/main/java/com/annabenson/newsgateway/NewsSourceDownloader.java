package com.annabenson.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Anna on 4/11/2018.
 */

public class NewsSourceDownloader extends AsyncTask<String,Void,String> {

    private static final String TAG = "NewsSourceDownloader";
    private static final String KEY = "85e81c15a86e405ebf43991e0e30520c"; // API key
    private static final String URL_ALL_SOURCES = "https://newsapi.org/v1/sources?language=en&country=us&apiKey=";
    private static final String URL_GET_CATEGORY = "https://newsapi.org/v1/sources?language=en&country=us&category=";
    private static final String URL_CATEGORY_END = "&apiKey=";
    private MainActivity mainActivity;
    private String newsCategory;

    private ArrayList<String> categories;

    public NewsSourceDownloader(MainActivity ma, String nc){
        mainActivity = ma;

        if(nc.equals("all") || nc.equals("")){
            newsCategory = "";
        }
    }

    @Override
    protected void onPreExecute(){
        Log.d(TAG, "onPreExecute: ");
    }

    @Override
    protected void onPostExecute(String s){
        Log.d(TAG, "onPostExecute: ");

        ArrayList<Source> sourceList = parseJSON(s);

        // create list of unique category names (did this in parse JSON to avoid another loop here)
        mainActivity.setSources(sourceList, categories);

        return;
    }

    @Override
    protected String doInBackground(String... params){

        // connect to newsapi.org
        //    make a news source query (including category, "" is ok

        String dataURL = URL_GET_CATEGORY + newsCategory + URL_CATEGORY_END + KEY;
        Log.d(TAG, "doInBackground: URL is " + dataURL);

        Uri dataUri = Uri.parse(dataURL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            //Log.d(TAG, "doInBackground: A");
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            //Log.d(TAG, "doInBackground: " + sb.toString());

        }
        //catch (FileNotFoundException e){
        //    return null;
        //}
        catch (Exception e) {
            //Exception -> Log an error message
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        //Log.d(TAG, "doInBackground: " + sb.toString());
        Log.d(TAG, "doInBackground: returning");
        return sb.toString();

    }

    private ArrayList<Source> parseJSON(String s){
        Log.d(TAG, "parseJSON: ");

        ArrayList<Source> sourceList = new ArrayList<>();
        categories = new ArrayList<>();

        String id; String name; String url; String category;

        Log.d(TAG, "parseJSON: String is " + s);
        try{
            // get json object

            JSONArray sources = new JSONArray(s);

            for(int i = 0; i < sources.length(); i++){
                JSONObject obj = sources.getJSONObject(i);
                id = obj.getString("id");
                name = obj.getString("name");
                url = obj.getString("url");
                category = obj.getString("category");

                if(! categories.contains(category) ){
                    categories.add(category);
                    Log.d(TAG, "parseJSON: Added category " + category);
                }

                sourceList.add(new Source(id,name,url,category));
            }

            return sourceList;

        }catch (Exception e){
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }

        return null;

    } // end of parseJSON

}