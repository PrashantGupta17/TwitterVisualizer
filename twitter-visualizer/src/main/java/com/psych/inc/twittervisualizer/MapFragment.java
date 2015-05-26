package com.psych.inc.twittervisualizer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;

import com.google.maps.android.clustering.ClusterManager;
import com.psych.inc.twitter.Authenticated;
import com.psych.inc.twitter.Coordinates;
import com.psych.inc.twitter.Tweet;
import com.psych.inc.twitter.Twitter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * Created by Psych Inc on 16-05-2015.
 */
@SuppressLint("ValidFragment")
public class MapFragment extends Fragment implements ClusterManager.OnClusterClickListener<GeoTweets>, ClusterManager.OnClusterItemInfoWindowClickListener<GeoTweets>, ClusterManager.OnClusterInfoWindowClickListener<GeoTweets>, ClusterManager.OnClusterItemClickListener<GeoTweets>{

    MapView mapView;
    GoogleMap twitterMap;
    Marker home;

    Context tContext;
    long time = 0;
    float distance = 0;

    ClusterManager<GeoTweets> clusterManager;
    public static Cluster<GeoTweets> clickedCluster;
    LocationManager lm;
    int lastLocationFlag = 0;

    ArrayList<String> allId = new ArrayList<>();

    MapFragment(Context context){

        tContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);

        Button tweetMe;
        tweetMe = (Button) v.findViewById(R.id.refresh);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);


        // Gets to GoogleMap from the MapView and does initialization stuff
        twitterMap = mapView.getMap();
        twitterMap.getUiSettings().setMyLocationButtonEnabled(false);
        twitterMap.getUiSettings().setZoomControlsEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        tweetMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refreshTweets();
            }
        });

        clusterManager = new ClusterManager<>(tContext, twitterMap);


        twitterMap.setOnCameraChangeListener(clusterManager);
        twitterMap.setOnMarkerClickListener(clusterManager);
        twitterMap.setOnInfoWindowClickListener(clusterManager);

        twitterMap.setInfoWindowAdapter(clusterManager.getMarkerManager());

        clusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(new ClusterInfoWindow(tContext));
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new ClusterItemInfoWindow(tContext));

        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<GeoTweets>() {

            @Override
            public boolean onClusterClick(Cluster<GeoTweets> cluster) {
                clickedCluster = cluster; // remember for use later in the Adapter

                return false;
            }
        });

        clusterManager.setOnClusterInfoWindowClickListener((ClusterManager.OnClusterInfoWindowClickListener<GeoTweets>) this);
        clusterManager.setOnClusterItemInfoWindowClickListener((ClusterManager.OnClusterItemInfoWindowClickListener<GeoTweets>) this);

        lm = (LocationManager)tContext.getSystemService(Context.LOCATION_SERVICE);

        LocationListener ll = new LocationListener() {

            @Override
            public void onLocationChanged(Location loc) {
                // TODO Auto-generated method stub

                home.setPosition(new LatLng(loc.getLatitude(), loc.getLongitude()));

                lastLocationFlag = 1;

            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }
        };

        Location myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        home = twitterMap.addMarker(new MarkerOptions().title("My Position").position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        twitterMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 10));

        final String geoParams = String.valueOf(myLocation.getLatitude()) + "," + String.valueOf(myLocation.getLongitude()) + "," + "10km";

        new ExecuteTwitterRequest().execute(geoParams);

        // request location change updates if distance changes by 10m
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,time,distance,ll);

        new AlertDialog.Builder(v.getContext())
                .setTitle("About TV!!")
                .setMessage("This app is aimed at displaying tweets in your 10km radius on a google map. " +
                        "Press the tweet button on top right corner to display tweets around " +
                        "current location. Don't turn off the internet. Have Fun!!")
                .setPositiveButton("Got It!!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                })
                .show()
                .setCanceledOnTouchOutside(false);

        return v;
    }

    public void refreshTweets(){

        if(isNetworkAvailable()){

            if(lastLocationFlag != 0){
                allId.clear();
            }

            twitterMap.clear();
            clusterManager.clearItems();

            Toast.makeText(tContext, "Please Wait..." , Toast.LENGTH_LONG).show();

            Location myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            home = twitterMap.addMarker(new MarkerOptions().title("My Position").position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            final String geoParams = String.valueOf(myLocation.getLatitude()) + "," + String.valueOf(myLocation.getLongitude()) + "," + "10km";

            new ExecuteTwitterRequest().execute(geoParams);

            lastLocationFlag = 0;

        }else{

            Toast.makeText(tContext, "No internet connection!!", Toast.LENGTH_LONG).show();

        }


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) tContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onClusterClick(Cluster<GeoTweets> geoTweetsCluster) {

        Toast.makeText(tContext, "hah", Toast.LENGTH_LONG).show();
        clickedCluster = geoTweetsCluster;
        return true;

    }

    @Override
    public void onClusterInfoWindowClick(Cluster<GeoTweets> geoTweetsCluster) {

        twitterMap.animateCamera(CameraUpdateFactory.newLatLngZoom(geoTweetsCluster.getPosition(), twitterMap.getCameraPosition().zoom + 4));

    }

    @Override
    public boolean onClusterItemClick(GeoTweets geoTweets) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(GeoTweets geoTweets) {

        twitterMap.animateCamera(CameraUpdateFactory.newLatLngZoom(geoTweets.getPosition(), twitterMap.getCameraPosition().zoom + 4));

    }

    class ExecuteTwitterRequest extends AsyncTask<String, Void, String> {

        final static String CONSUMER_KEY = "SVEXHbMDogm39AdFPq255D6gC";
        final static String CONSUMER_SECRET = "sRGFO0XBWksOivYUNazfyQszRhppejPhLFN7jI69uVe5eyzDks";
        final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
        final static String TwitterStreamURL = "https://api.twitter.com/1.1/search/tweets.json?result_type=mixed&count=200&geocode=";

        @Override
        protected String doInBackground(String... geoQuery) {
            String result = null;
            Log.e("tv", "hah!");
            // Append since_id to query if first time, otherwise run as it is
            if (geoQuery.length > 0 && allId.size() == 0) {

                result = getTwitterStream(geoQuery[0]);

            }else if(geoQuery.length > 0 && allId.size() > 0){

                String newGeoQuery = geoQuery[0] + "&since_id=" + allId.get(allId.size()-1);
                result = getTwitterStream(newGeoQuery);

            }

            return result;
        }

        // onPostExecute convert the JSON results into a Twitter object which is an Array list of tweets
        @Override
        protected void onPostExecute(String result) {
            // Hold json result
            ArrayList<Tweet> twits = jsonToTwitter(result);
            // Hold new MarkerOptions from those results

            ArrayList<GeoTweets> newGeoTweets = new ArrayList<>();

            if(twits.size() != 0){

                for(Tweet tweet : twits){

                    Coordinates coord = tweet.getCoordinates();

                    GeoTweets geoTweet = new GeoTweets(coord.getLatLng(), "User : " + tweet.getUser().getName());

                    newGeoTweets.add(geoTweet);

                    allId.add(tweet.getId());

                }
            }else{

                Toast.makeText(tContext, "No tweets!! Try other options.",Toast.LENGTH_LONG).show();
            }

            clusterManager.setRenderer(new TweetClusterRenderer(tContext, twitterMap, clusterManager));


            for(GeoTweets geoTweet : newGeoTweets){

                clusterManager.addItem(geoTweet);

            }


        }

        // converts a string of JSON data into a Twitter object
        private ArrayList<Tweet> jsonToTwitter(String result) {

            ArrayList<Tweet> twits = null;

            if (result != null && result.length() > 0) {
                try {
                    // Assign json to Twitter object. This is to get different tweets inside a statuses array
                    Gson gson = new Gson();
                    twits = gson.fromJson(result, Twitter.class).getStatuses();

                } catch (IllegalStateException ex) {
                    // just eat the exception
                }
            }
            // Return a arraylist of individual tweets
            return twits;
        }

        // convert a JSON authentication object into an Authenticated object
        // To get token and its type
        private Authenticated jsonToAuthenticated(String rawAuthorization) {
            Authenticated auth = null;
            if (rawAuthorization != null && rawAuthorization.length() > 0) {
                try {
                    Gson gson = new Gson();
                    auth = gson.fromJson(rawAuthorization, Authenticated.class);
                } catch (IllegalStateException ex) {
                    // just eat the exception
                }
            }
            return auth;
        }

        // Get the resulting json result of twitter api calls
        private String getResponseBody(HttpRequestBase request) {
            StringBuilder sb = new StringBuilder();
            try {

                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
                HttpResponse response = httpClient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                String reason = response.getStatusLine().getReasonPhrase();

                if (statusCode == 200) {

                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();

                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sb.append(line);
                    }
                } else {
                    sb.append(reason);
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (ClientProtocolException ex1) {
            } catch (IOException ex2) {
            }
            return sb.toString();
        }

        // Run the query successfully to provide back resulting json
        private String getTwitterStream(String geoQuery) {
            String results = null;

            try {
                // URL encode the consumer key and secret
                String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
                String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");

                // Concatenate the encoded consumer key, a colon character, and the
                // encoded consumer secret
                String combined = urlApiKey + ":" + urlApiSecret;

                // Base64 encode the string
                String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

                // Obtain a bearer token
                HttpPost httpPost = new HttpPost(TwitterTokenURL);
                httpPost.setHeader("Authorization", "Basic " + base64Encoded);
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
                String rawAuthorization = getResponseBody(httpPost);
                Authenticated auth = jsonToAuthenticated(rawAuthorization);

                // Applications should verify that the value associated with the
                // token_type key of the returned object is bearer
                if (auth != null && auth.token_type.equals("bearer")) {

                    // Authenticate API requests with bearer token
                    HttpGet httpGet = new HttpGet(TwitterStreamURL + geoQuery);

                    // construct a normal HTTPS request and include an Authorization
                    // header with the value of Bearer <>
                    httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
                    httpGet.setHeader("Content-Type", "application/json");
                    // update the results with the body of the response
                    results = getResponseBody(httpGet);
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (IllegalStateException ex1) {
            }

            return results;
        }
    }
}


