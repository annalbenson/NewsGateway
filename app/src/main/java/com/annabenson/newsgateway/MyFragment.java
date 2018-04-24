package com.annabenson.newsgateway;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;



/**
 * Created by Anna on 4/17/2018.
 */

public class MyFragment extends Fragment{

    //public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String TAG = "MyFragment";
    public static MainActivity mainActivity;

    public TextView articleTitle;
    public TextView articleDate;
    public TextView articleAuthor;
    public ImageView articleImage;
    public TextView articleDescription;



    public static final MyFragment newInstance(  MainActivity ma ,String title, String date, String author, String image, String description)
    {
        mainActivity = ma;

        Log.d(TAG, "newInstance: with title " + title);
        MyFragment f = new MyFragment();
        Bundle bdl = new Bundle(5);
        bdl.putString("title", title);
        bdl.putString("date", date );
        bdl.putString("author", author);
        bdl.putString("image", image);
        bdl.putString("description", description);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
        String title = getArguments().getString("title");
        String date = getArguments().getString("date");
        String author = getArguments().getString("author");
        String image = getArguments().getString("image");
        String description = getArguments().getString("description");
        */


        String title = ( getArguments().getString("title") != null ? getArguments().getString("title") : "" );
        String date = ( getArguments().getString("date") != null || ! getArguments().getString("date").equals("null") ? getArguments().getString("date") : "" );
        String author = ( getArguments().getString("author") != null  || ! getArguments().getString("author").equals("null")  ? getArguments().getString("author") : "" );
        String image = getArguments().getString("image");
        String description = ( getArguments().getString("description") != null ? getArguments().getString("description") : "" );

        View v = inflater.inflate(R.layout.myfragment_layout, container, false);
        //TextView messageTextView = (TextView)v.findViewById(R.id.textView);
        //messageTextView.setText(message);

        Log.d(TAG, "onCreateView: making fragment");




        // connect views to variables
        articleTitle = v.findViewById(R.id.titleID);
        articleDate = v.findViewById(R.id.dateID);
        articleAuthor = v.findViewById(R.id.authorID);
        articleImage = v.findViewById(R.id.imageID);
        articleDescription = v.findViewById(R.id.descriptionID);

        // set text views
        articleTitle.setText(title);
        articleDate.setText(date);
        articleAuthor.setText(author);
        articleDescription.setText(description);

        //image loading
        articleImage.setImageResource(R.drawable.placeholder);

        if( connected() ) {


            if (image == null) {
                Log.d(TAG, "onCreateView: null image url");
                articleImage.setVisibility(View.GONE); // ???? What do here
            } else {
            /* Download Image */
                final String photoUrl = image;
                Picasso picasso = new Picasso.Builder(mainActivity).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        // Here we try https if the http image attempt failed
                        final String changedUrl = photoUrl.replace("http:", "https:");
                        picasso.load(changedUrl)
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(articleImage);

                    }
                }).build();

                picasso.load(photoUrl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(articleImage);


            }
        }
        else{
            /* Not Connected */
            Log.d(TAG, "onCreateView: not connected");
        }


        return v;
    }

    private boolean connected(){
        ConnectivityManager cm = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
