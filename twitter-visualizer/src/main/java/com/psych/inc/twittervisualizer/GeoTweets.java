package com.psych.inc.twittervisualizer;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Psych Inc on 17-05-2015.
 */
public class GeoTweets implements ClusterItem{

    public final String name;
    private final LatLng mPosition;

    public GeoTweets(LatLng position, String name) {
        this.name = name;
        mPosition = position;
    }

    @Override
    public LatLng getPosition() {

        return mPosition;

    }

    public String getName(){

        return this.name;

    }
}
