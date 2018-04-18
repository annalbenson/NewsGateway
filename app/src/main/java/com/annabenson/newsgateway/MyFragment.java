package com.annabenson.newsgateway;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Anna on 4/17/2018.
 */

public class MyFragment extends Fragment{

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String TAG = "MyFragment";

    public static final MyFragment newInstance(String message)
    {
        Log.d(TAG, "newInstance: with message " + message);
        MyFragment f = new MyFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        String message = getArguments().getString(EXTRA_MESSAGE);
        View v = inflater.inflate(R.layout.myfragment_layout, container, false);
        TextView messageTextView = (TextView)v.findViewById(R.id.textView);
        messageTextView.setText(message);

        return v;
    }

}
