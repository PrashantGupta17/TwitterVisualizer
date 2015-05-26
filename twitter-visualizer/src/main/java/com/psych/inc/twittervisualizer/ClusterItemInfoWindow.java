package com.psych.inc.twittervisualizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Psych Inc on 17-05-2015.
 */
public class ClusterItemInfoWindow implements GoogleMap.InfoWindowAdapter{

    private final View myContentsView;

    ClusterItemInfoWindow(Context context){

        myContentsView = LayoutInflater.from(context).inflate(R.layout.custom_window, null);

    }

    @Override
    public View getInfoContents(Marker marker) {

        TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
        tvTitle.setText("Tweet from : " + marker.getTitle());
        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        return null;
    }

}
