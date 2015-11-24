package com.example.sathya.locationbackground;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Sathya on 24-11-2015.
 */
public class BackgroundService extends IntentService implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks
        ,LocationListener{

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private int Notification_id=0;

    public BackgroundService(String name) {
        super(name);
    }public BackgroundService(){
        super("BackgroundLocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        createLocaationRequest();
        createApiClient();
    }

    private void createLocaationRequest() {
        mLocationRequest=new LocationRequest()
                .create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void createApiClient() {
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

        mGoogleApiClient.connect();
    }




    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        createNotification(location);
    }

    private void createNotification(Location location) {
        Notification_id++;
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this)
                .setContentTitle("Location Changed"+Notification_id)
                .setContentText("Lat : "+location.getLatitude()+"\nLog : "+location.getLongitude())
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setSound(uri);

        NotificationManager mNotificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Notification_id,mBuilder.build());
    }
}

