package com.psych.inc.twittervisualizer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends FragmentActivity {

    MapView mapView;
    GoogleMap twitterMap;
    Context mContext;

    Button ifNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ifNetwork = (Button) findViewById(R.id.afterInternet);
        ifNetwork.setVisibility(View.GONE);

        mContext = this.getApplicationContext();

        if(isNetworkAvailable()){

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new MapFragment(mContext)).commit();

        }else{

            ifNetwork.setVisibility(View.VISIBLE);

            new AlertDialog.Builder(this)
                    .setTitle("You require an internet connection to use this app!!")
                    .setPositiveButton("Got It!!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    })
                    .show();

            ifNetwork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isNetworkAvailable()){

                        ifNetwork.setVisibility(View.GONE);

                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.fragment_container, new MapFragment(mContext)).commit();

                    }else{

                        Toast.makeText(mContext, "No internet connection", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }


        //MapsInitializer.initialize(this.getApplicationContext());

        //twitterMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
