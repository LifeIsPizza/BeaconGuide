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
    private BeaconManager beaconManager = null;
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

        //Callback di lifecycle, utilizzati per accendere/spegnere ranging e manager se l'app va in pausa/stop
        ActivityLifecycleCallbacks callbacks = new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //Fai cose ad attività creata
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (isOnline()) { //Va fatto il check prima altrimenti spamma notifiche
                    //L'app non muore completamente nemmeno dopo system exit, ripartendo il flag è resettato e spamma notifiche
                    connectToManager();
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                //Fai cose ad attività ripresa
            }

            @Override
            public void onActivityPaused(Activity activity) {
                //Fai cose ad attività pausata
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if(beaconManager!=null) {
                    beaconManager.disconnect();
                    beaconManager = null;
                }
                Log.d("BeaconApp", "Stopping");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.d("BeaconApp", "SaveState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d("BeaconApp", "ActivityDestroyed");
//                if(beaconManager!=null)
//                    beaconManager.disconnect();
            }
        };

        //Registrazione delle callback sull'attività
        this.registerActivityLifecycleCallbacks(callbacks);
        super.onCreate();
        //EstimoteSDK.enableDebugLogging(true);
        /*if (isOnline()) { //Va fatto il check prima altrimenti spamma notifiche
            //L'app non muore completamente nemmeno dopo system exit, ripartendo il flag è resettato e spamma notifiche
            connectToManager();
        }
        else{
            Toast.makeText(getApplicationContext(), "Network not online, please enable", Toast.LENGTH_LONG).show();
            //System.exit(0);
        }*/
    }

    //Connessione al Manager
    public void connectToManager(){
        //Classe BeaconManager per gestire il ranging
        beaconManager = new BeaconManager(getBaseContext());
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    //Se ho notificato, ad ogni discovery periodica aggiorno solo il nearestBeacon
                    Beacon nearestBeacon = list.get(0); //Il primo della lista è il più vicino
                    if (!haveNotify){ //Se non ho notificato (prima volta/entrata) faccio il procedimento di notifica
                        //E download del JSON
                        showNotification("Entrato", "Entrato nella regione");
                        haveNotify = true;
                        if(!isOnline()) {
                            Toast.makeText(getApplicationContext(), "Network not online, please enable", Toast.LENGTH_LONG).show();
                            System.exit(0);
                        }
                        try {
                            //Retrieving della stringa JSON da url
                            jsonString = new AsyncJsonGet().execute(stringURL).get();
                            Log.d("JSONRETRIEVE", "Stringa: " + jsonString);
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
                            //Da gestire gli oggetti che passa il JSON

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
                else { //Se la lista è empty, sono uscito dalla regione. Resetto la notifica
                    if (haveNotify) {
                        showNotification("Uscito", "Uscito dalla regione");
                        haveNotify = false;
                    }
                }
            }


        });
        doConnect(); //Connessione al BeaconManager e start ranging
    }
    //Check del network
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    //Notifica all'entrata/uscita di regione
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
    //Connessione al BeaconManager e inizio del ranging
    public void doConnect(){
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    //Classe che prende il JSON in maniera asincrona per non bloccare la baracca
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
