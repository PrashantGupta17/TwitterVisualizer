package com.psych.inc.twittervisualizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;

/**
 * Created by Psych Inc on 17-05-2015.
 */
public class ClusterInfoWindow implements GoogleMap.InfoWindowAdapter{

    private final View myContentsView;

    ClusterInfoWindow(Context context){

        myContentsView = LayoutInflater.from(context).inflate(R.layout.custom_window, null);


    }

    @Override
    public View getInfoContents(Marker marker) {

        String infoString = "";

        for(GeoTweets geoTweet : MapFragment.clickedCluster.getItems()){

            infoString += geoTweet.getName() + "\n";
        }

        TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
        tvTitle.setText("Tweets from : " + "\n" + infoString);
        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        return null;
    }

}
