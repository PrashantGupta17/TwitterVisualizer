package com.psych.inc.twittervisualizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Psych Inc on 17-05-2015.
 */
public class TweetClusterRenderer extends DefaultClusterRenderer<GeoTweets>{

    private Context copyContext;
    GoogleMap copyMap;
    ClusterManager<GeoTweets> copyClusterManager;

    // Below statements must be initialized after constructor call to get context
    //private final IconGenerator mIconGenerator = new IconGenerator(context);
    //private final IconGenerator mClusterIconGenerator = new IconGenerator(context);
    // no need of ImageView and IconGenerator as we only want to show image for marker as it is and not in image view
    //private final IconGenerator mIconGenerator;
    //private final IconGenerator mClusterIconGenerator;

    //private final ImageView mImageView;
    //private final ImageView mClusterImageView;
    //private final int mDimension;



    // Take context and customizedMap as arguements for constructor
    public TweetClusterRenderer(Context c, GoogleMap btdtMap, ClusterManager<GeoTweets> manage) {

        // In super pass context and map taken from dashboardActivity
        //super(getApplicationContext(), getMap(), mClusterManager);
        super(c, btdtMap, manage);

        copyContext = c;
        copyMap = btdtMap;
        copyClusterManager = manage;

        // Initialize IconGenerators;
        //mIconGenerator = new IconGenerator(copyContext);
        //mClusterIconGenerator = new IconGenerator(copyContext);

        // Just setdrawable for mClusterImageView and set it as content view for mClusterIconGenerator; just like mIconGenerator

        // ClusterIcon should be a single Image
        //mClusterImageView = new ImageView(copyContext);
        //mClusterIconGenerator.setContentView(mClusterImageView);

        //mImageView = new ImageView(copyContext);
        // No need to set padding or dimensions

        //mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
        //mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        //int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
        //mImageView.setPadding(padding, padding, padding, padding);

        //mIconGenerator.setContentView(mImageView);

    }


    // below customized to btdt from Places class
    @Override
    protected void onBeforeClusterItemRendered(GeoTweets geoTweet, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.
        // no need of ImageView and IconGenerator as we only want to show image for marker as it is and not in image view
        //mImageView.setImageResource(place.profilePhoto);
        //Bitmap icon = mIconGenerator.makeIcon();

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(geoTweet.name);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<GeoTweets> cluster, MarkerOptions markerOptions) {
        // Draw multiple people.
        // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
        /*List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
        int width = mDimension;
        int height = mDimension;

        for (Person p : cluster.getItems()) {
            // Draw 4 at most.
            if (profilePhotos.size() == 4) break;
            Drawable drawable = getResources().getDrawable(p.profilePhoto);
            drawable.setBounds(0, 0, width, height);
            profilePhotos.add(drawable);
        }
        MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
        multiDrawable.setBounds(0, 0, width, height);

        mClusterImageView.setImageDrawable(multiDrawable);
        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        */

        //customize mClusterIconGenerator to show only one image clustetricon from drawable
        // no need of ImageView and IconGenerator as we only want to show image for marker as it is and not in image view

        Bitmap clusterIcon = BitmapFactory.decodeResource(copyContext.getResources(),
                R.drawable.clustericon);
        clusterIcon = clusterIcon.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(clusterIcon);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK); // Text Color
        paint.setStrokeWidth(15); // Text Size
        paint.setTextSize(23);
        paint.setTextAlign(Paint.Align.CENTER);


        int x = (clusterIcon.getWidth())/2;
        int y = (clusterIcon.getHeight())/2 + 7;
        // some more settings...

        // draw text on canvas for no. of places inside it
        canvas.drawText(String.valueOf(cluster.getSize()), x, y, paint);

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(clusterIcon));

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<GeoTweets> cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }
}
