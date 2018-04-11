package com.annabenson.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
    private static final String URL_STEM1 = "";
    private static final String URL_STEM2 = "";
    private MainActivity mainActivity;
    private String newsCategory;

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

        return;
    }

    @Override
    protected String doInBackground(String... params){

        // connect to newsapi.org
        //    make a news source query (including category, "" is ok


        String dataURL = URL_STEM1 + newsCategory + URL_STEM2;


        Uri dataUri = Uri.parse(dataURL);
        String urlToUse = dataUri.toString();


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

    @Override
    private ArrayList<Article> parseJSON(String s){
        Log.d(TAG, "parseJSON: ");

        ArrayList<Article> articleList = new ArrayList<>();
        try{

        }catch (Exception e){

        }

    }



}
