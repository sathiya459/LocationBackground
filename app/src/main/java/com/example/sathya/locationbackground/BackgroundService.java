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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
    HttpURLConnection mHttpURLConnection;
    URL url;


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
        postLocaation(location);
    }

    private void postLocaation(Location location) {
        try {
            url=new URL("http://www.locationupdate.netau.net/updatetodb.php?lat="+location.getLatitude()+"&long="+location.getLongitude());
            //url=new URL("http://192.168.43.172/locationupdate/updatetodb.php?lat=0.011&long=70.70");
           // url=new URL("http://192.168.43.172/locationupdate/updatetodb.php?lat="+location.getLatitude()+"&long="+location.getLongitude());
            mHttpURLConnection= (HttpURLConnection) url.openConnection();
            mHttpURLConnection.setConnectTimeout(5000);
            mHttpURLConnection.setDoOutput(true);
            //mHttpURLConnection.connect();
            try {
                InputStream in = new BufferedInputStream(mHttpURLConnection.getInputStream());
                createNotification("updated");
                //readStream(in);
            }catch (Exception e){
                createNotification(e);
            }
            finally {
                mHttpURLConnection.disconnect();
            }



//            BufferedReader reader = null;
//            reader = new BufferedReader(new InputStreamReader(mHttpURLConnection.getInputStream()));
//            String result="",response="";
//            while((result=reader.readLine())!=null){
//                response=response+result;
//            }





        } catch (MalformedURLException e) {
            createNotification(e);
            e.printStackTrace();
        } catch (IOException e) {
            createNotification(e);
            e.printStackTrace();
        }

    }
    private void createNotification(Exception e) {
        Notification_id++;
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this)
                .setContentTitle("ERROR : "+Notification_id)
                .setContentText("er:"+e)
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setSound(uri);

        NotificationManager mNotificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Notification_id,mBuilder.build());
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
    private void createNotification(String s) {
        Notification_id++;
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this)
                .setContentTitle("Location Changed"+Notification_id)
                .setContentText(s)
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setSound(uri);

        NotificationManager mNotificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Notification_id,mBuilder.build());
    }
}

