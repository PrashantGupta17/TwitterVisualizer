package com.psych.inc.twitter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Psych Inc on 14-05-2015.
 */
public class Coordinates {

    @SerializedName("coordinates")
    private Collection<Float> coordinates;

    @SerializedName("type")
    private String type;

    public LatLng getLatLng(){

        ArrayList<Float> coord = new ArrayList<Float>();

        for(Float coor : coordinates){

            coord.add(coor);

        }

        // As coordinates are returned as geoJSON meaning longitude first,latitude second
        return new LatLng(coord.get(1),coord.get(0));
    }

}
