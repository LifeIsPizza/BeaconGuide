package appservweb.isilm.beaconguide;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Enrico on 12/10/2016.
 */

public class BeaconApp extends Application {

    private Intent notifyIntent;
    private BeaconManager beaconManager;
    private NotificationManager notificationManager;
    private Region region = new Region("Test",
            UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
            null, null);
    private static final String stringURL = "http://www.manufattidigitali.com/prova/mobile/getJsonDataFromServer";
    private boolean haveNotify = false;
    private static String jsonString = "";
    private static JSONObject jsonObj = null;



    @Override
    public void onCreate() {
        notifyIntent = new Intent(this, MainMenu.class);

        ActivityLifecycleCallbacks callbacks = new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                connectToManager();
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                beaconManager.disconnect();
                Log.d("BeaconApp", "Stopping");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.d("BeaconApp", "SaveState");

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d("BeaconApp", "ActivityDestroyed");
                beaconManager.disconnect();
            }
        };

        this.registerActivityLifecycleCallbacks(callbacks);
        super.onCreate();
        //EstimoteSDK.enableDebugLogging(true);

        connectToManager();
    }

    public void connectToManager(){


        beaconManager = new BeaconManager(getBaseContext());
        if (isOnline()) {
            beaconManager.setRangingListener(new BeaconManager.RangingListener() {
                @Override
                public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                    if (!list.isEmpty()) {
                        if (!haveNotify){
                            Beacon nearestBeacon = list.get(0); //Il primo della lista è il più vicino
                            showNotification("Entrato", "Entrato nella regione");
                            haveNotify = true;
                            try {
                                jsonString = new AsyncJsonGet().execute(stringURL).get();
                            } catch (Exception e) {
                                Log.d("JSONTEST", "FAIL! " + jsonString);

                                Toast.makeText(getApplicationContext(), "Failed to retrieve JSON from URL", Toast.LENGTH_LONG).show();
                                System.exit(0);
                                AlertDialog alertDialog = new AlertDialog.Builder(getBaseContext()).create();
                                alertDialog.setTitle("Warning");
                                alertDialog.setMessage("Failed to retrieve JSON");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                System.exit(0);
                                            }
                                        });
                                alertDialog.show();

                            }
                            try {
                                Log.d("JSONTEST", "Stringa: " + jsonString);
                                jsonObj = new JSONObject(jsonString);
                                //addItems();

                            } catch (Exception e) {
                                Log.d("JSONTEST", "FAIL! " + jsonString);

                                Toast.makeText(getApplicationContext(), "JSON Malformed", Toast.LENGTH_LONG).show();
                                System.exit(0);
                                AlertDialog alertDialog = new AlertDialog.Builder(getBaseContext()).create();
                                alertDialog.setTitle("Warning");
                                alertDialog.setMessage("JSON Malformed");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                System.exit(0);
                                            }
                                        });
                                alertDialog.show();

                            }

                            //Gestione del beacon più vicino, eventuale message passing se cambia.
                            //Lista dei beacon ranged è in "list"
                        }
                    }
                    else {
                        if (haveNotify) {
                            showNotification("Uscito", "Uscito dalla regione");
                            haveNotify = false;
                        }
                    }
                }


            });
            doConnect();
        }
        else{
            Toast.makeText(getApplicationContext(), "Network not online, please enable", Toast.LENGTH_LONG).show();
            System.exit(0);
        }


    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public void showNotification(String title, String message) {
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    public void doConnect(){
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }
    private class AsyncJsonGet extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadUrl(params[0]);
            } catch (/*IO*/Exception e) {
                e.printStackTrace();
                return "Unable to retrieve URL";
            }
        }
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(10000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("debug", "The response is: " + response);
                is = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String newLine = System.getProperty("line.separator");
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + newLine);
                }
                String result = sb.toString();
                return result;

            }

            finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        @Override
        protected void onPostExecute(String result) {
            jsonString = result;
        }


    }
}
