package com.annabenson.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Anna on 4/11/2018.
 */

public class NewsArticleDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "NewsArticleDownloader";
    private static final String KEY = "85e81c15a86e405ebf43991e0e30520c"; // API key
    private static final String URL_STEM = "https://newsapi.org/v1/articles?source=";
    private static final String URL_END = "&apiKey=";


    private NewsService newsService;
    private String newsSource;


    public NewsArticleDownloader(NewsService newsService, String newsSource){
        this.newsService = newsService;
        this.newsSource = newsSource;

        // END
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute: preparing to download articles");
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: ");
        try{
            newsService.setArticles(parseJSON(s));
        } catch (Exception e){
            Log.d(TAG, "onPostExecute: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... strings) {

        // connect to newsapi.org
        // make a news article query iwth specified news source

        String dataURL = URL_STEM + newsSource + URL_END + KEY;
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


    private ArrayList<Article> parseJSON(String s){
        Log.d(TAG, "parseJSON: parsing articles ");

        ArrayList<Article> articleList = new ArrayList<>();

        String author; String title; String description; String urlToImage; String publishedAt;

        Log.d(TAG, "parseJSON: String is " + s);
        try{
            JSONObject entire = new JSONObject(s);
            JSONArray articles = entire.getJSONArray("articles");

            for(int i = 0; i < articles.length(); i++){
                JSONObject obj = articles.getJSONObject(i);
                author = obj.getString("author");
                title = obj.getString("title");
                description = obj.getString("description");
                urlToImage = obj.getString("urlToImage");
                publishedAt = obj.getString("publishedAt");

                articleList.add( new Article(author,title,description,urlToImage,publishedAt));
            }

            return articleList;

        }catch (Exception e){
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }

        return null;

    } // end parseJSON
}
