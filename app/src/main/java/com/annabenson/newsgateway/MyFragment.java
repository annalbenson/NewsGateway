package com.annabenson.newsgateway;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Anna on 4/17/2018.
 */

public class MyFragment extends Fragment{

    //public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String TAG = "MyFragment";

    public TextView articleTitle;
    public TextView articleDate;
    public TextView articleAuthor;
    public ImageView articleImage;
    public TextView articleDescription;



    public static final MyFragment newInstance(String title, String date, String author, String image, String description)
    {
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

        String title = getArguments().getString("title");
        String date = getArguments().getString("date");
        String author = getArguments().getString("author");
        String image = getArguments().getString("image")

        View v = inflater.inflate(R.layout.myfragment_layout, container, false);
        //TextView messageTextView = (TextView)v.findViewById(R.id.textView);
        //messageTextView.setText(message);

        Log.d(TAG, "onCreateView: making fragment");

        String


        // connect views to variables
        articleTitle = v.findViewById(R.id.titleID);
        articleDate = v.findViewById(R.id.dateID);
        articleAuthor = v.findViewById(R.id.authorID);
        articleImage = v.findViewById(R.id.imageID);
        articleDescription = v.findViewById(R.id.descriptionID);

        // set views
        articleTitle.setText();


        return v;
    }

}
