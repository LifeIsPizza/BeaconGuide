package appservweb.isilm.beaconguide;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.Random;
import java.util.UUID;

/**
 * Created by Enrico on 12/10/2016.
 */

public class BeaconApp extends Application {

    private static final String TAG_OBJECTS = "Beacon";
    private static final String TAG_NOME = "id";
    private static final String TAG_ZONA = "zona";
    private static final String TAG_MAPID = "map_id";
    private static final String TAG_VICINI = "vicini";
    private static final String TAG_DIS = "viciniHandicap";

    private Intent notifyIntent;
    private BeaconManager beaconManager = null;
    private NotificationManager notificationManager;
    private Region region = new Region("Test",
            UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
            null, null);
    private static final String stringURL = "http://www.manufattidigitali.com/prova/mobile/getJsonDataFromServer";
    private static String urlmappa;
    private boolean haveNotify = false;
    private static String jsonString = "";
    private static JSONObject jsonObj = null;
    private static String jsonStringMaps = "";
    private static JSONObject jsonObjMaps = null;
    private static Bitmap bitmap;
    private static ArrayList<appservweb.isilm.beaconguide.Beacon> ALL_BEACONS;
    private static Graph graphNor;
    private static Graph graphDis;

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
                //OLD VERSION
                /*
                if (isOnline()) { //Va fatto il check prima altrimenti spamma notifiche
                    //L'app non muore completamente nemmeno dopo system exit, ripartendo il flag è resettato e spamma notifiche
                    getLocationsList();
                    ////connectToManager(); //Non decommentare questo, decommenta quello in resume
                }
                */
                //FINE OLD VERSION
                getLocationsList();
                Log.d("BeaconApp", "Starting");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                // OLD VERSION
                /*
                if (isOnline()) { //Va fatto il check prima altrimenti spamma notifiche
                    //L'app non muore completamente nemmeno dopo system exit, ripartendo il flag è resettato e spamma notifiche
                    connectToManager(); //Decommenta questo! Ma solo quando devi provare il ranging di beacon
                }
                */
                //FINE OLD VERSION
                //#TODO 1 decommenta il connectToManager
                Log.d("BeaconApp", "Resuming");
                connectToManager();
            }

            @Override
            public void onActivityPaused(Activity activity) {
                //Fai cose ad attività pausata
                if(beaconManager!=null) {
                    beaconManager.disconnect();
                    //beaconManager = null;
                    Log.d("BeaconApp", "manager Disconnected");
                }
                Log.d("BeaconApp", "Pausing");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                /*
                if(beaconManager!=null) {
                    beaconManager.disconnect();
                    //beaconManager = null;
                }
                */
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name2"));


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
                    //Log.d("Nearest","Beacon: " + Integer.toString(nearestBeacon.getMajor()));
                    sendNearest(nearestBeacon.getMajor() - 100);
                    //Decommenta il connectToManager in onresume
                    /*
                    if (!haveNotify){ //Se non ho notificato (prima volta/entrata) faccio il procedimento di notifica
                        //E download del JSON
                        showNotification("Entrato", "Entrato nella regione");
                        haveNotify = true;
                    }
                    */

                    //#TODO 2: gestire il nearestBeacon e creare un Beacon "precedente" per checkare se è sempre lo stesso
                    // In caso contrario, ci siamo spostati (il più vicino non è lo stesso più vicino di 2 secondi fa
                    // Aggiornare UI

                }
                else { //Se la lista è empty, sono uscito dalla regione. Resetto la notifica
                    //Log.d("NoBeacons", "Nessun beacon in Range");

                    //Randomize for testing
                    /*
                    Random rand = new Random();
                    // nextInt is normally exclusive of the top value,
                    // so add 1 to make it inclusive
                    int randomNum = rand.nextInt((4 - 1) + 1) + 1;
                    Log.d("RandomBeacon", Integer.toString(randomNum));
                    sendNearest(randomNum);
                    */

                    /*
                    if (haveNotify) {
                        showNotification("Uscito", "Uscito dalla regione");
                        haveNotify = false;
                    }
                    */
                }
            }


        });
        doConnect(); //Connessione al BeaconManager e start ranging
    }

    public void sendNearest(int toSend){
        Intent intent = new Intent("intent_nearest");
        intent.putExtra("id", 3);
        intent.putExtra("idBeac", toSend);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    public void getLocationsList(){
        try {
            //Retrieving della stringa JSON da url
            jsonString = new AsyncJsonGet().execute(stringURL).get();
            Log.d("JSONLOCATION1", "Stringa: " + jsonString);
        } catch (Exception e) {
            Log.d("JSONLOCATION2", "FAIL! " + jsonString);
            Toast.makeText(getApplicationContext(), "Failed to retrieve JSON from URL", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        try {
            Log.d("JSONLOCATION3", "Stringa: " + jsonString);
            jsonObj = new JSONObject(jsonString);
            JSONArray array = jsonObj.getJSONArray("maps");
            ArrayList<String> listOfPlaces = new ArrayList<String>();
            for (int k = 0; k < array.length(); k++) {
                listOfPlaces.add(k, array.get(k).toString());
                Log.d("JSONOBJ", "String retrieved:" + array.get(k).toString());
            }
            sendPlaces(listOfPlaces);

        } catch (Exception e) {
            Log.d("JSONLOCATION4", "FAIL! " + jsonString);
            Toast.makeText(getApplicationContext(), "JSON Malformed", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
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

    private class AsyncBitmapGet extends AsyncTask<String, Void, Bitmap>{
        protected Bitmap doInBackground(String... params) {
            try {
                return getBitmapFromURL(params[0]);
            } catch (/*IO*/Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            //do stuff

        }
    }

    private void sendPlaces(ArrayList<String> places) {
        Intent intent = new Intent("custom-event-name");
        if (places.isEmpty()){
            intent.putExtra("id", 1);
            intent.putExtra("places", places);
            intent.putExtra("message", "Empty places, no maps");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            intent.putExtra("id", 1);
            intent.putExtra("places", places);
            intent.putExtra("message", "Some maps found: " + places.size());
            Log.d("Places0", places.get(0).toString());
            Log.d("Places1", places.get(1).toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void sendBeacons (ArrayList<appservweb.isilm.beaconguide.Beacon> beacons){
        Intent intent = new Intent("custom-event-name");
        if (beacons.isEmpty()){
            intent.putExtra("id", 2);
            intent.putExtra("beacons", beacons);
            intent.putExtra("message", "Empty beacons");

            ArrayList<Graph> graphs = new ArrayList<Graph>();
            graphs.add(graphNor);
            graphs.add(graphDis);
            intent.putExtra("graphs", graphs);

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            intent.putExtra("id", 2);
            intent.putExtra("beacons", beacons);
            intent.putExtra("message", "Some maps found: " + beacons.size());

            ArrayList<Graph> graphs = new ArrayList<Graph>();
            graphs.add(graphNor);
            graphs.add(graphDis);
            intent.putExtra("graphs", graphs);

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String location = intent.getStringExtra("MainMenuClicked");
            Log.d("receiver", "Got message: " + message);
            Log.d("receiver", "Getting locations for " + location);
            getBeaconsList(location);
            setBeacons(jsonObjMaps);
            sendBeacons(ALL_BEACONS);

            //ArrayList<String> values = (ArrayList<String>) intent.getSerializableExtra("DownloadMapPress");
            //Download mappe asincrono
            //values.add("Ayy Lmao Works");

        }
    };

    private void getBeaconsList(String location){
        try {
            //Retrieving della stringa JSON da url
            jsonStringMaps = new AsyncJsonGet().execute(stringURL+"?map="+location).get();
            Log.d("GetMapsJson1", "Stringa: " + jsonStringMaps);
        } catch (Exception e) {
            Log.d("GetMapsJson2", "FAIL! " + jsonStringMaps);
            Toast.makeText(getApplicationContext(), "Failed to retrieve JSON from URL", Toast.LENGTH_LONG).show();
            //System.exit(0);
            AlertDialog alertDialog = new AlertDialog.Builder(getBaseContext()).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Failed to retrieve JSON");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //System.exit(0);
                        }
                    });
            alertDialog.show();

        }
        try {
            Log.d("GetMapsJson3", "Stringa: " + jsonStringMaps);
            jsonObjMaps = new JSONObject(jsonStringMaps);

        } catch (Exception e) {
            Log.d("GetMapsJson4", "FAIL! " + jsonStringMaps);
            Toast.makeText(getApplicationContext(), "JSON Malformed", Toast.LENGTH_LONG).show();
            //System.exit(0);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }



    private void setBeacons(JSONObject object){
        Log.d("derperoni","Derperonis");
        graphNor = new Graph();
        graphDis = new Graph();
        ALL_BEACONS = new ArrayList<appservweb.isilm.beaconguide.Beacon>() {{
            try{
                for(int k = 0; k < jsonObjMaps.getJSONArray(TAG_OBJECTS).length(); k++){
                    //Log.d("ForLog","Iteration: " + Integer.toString(k));
                    add(new appservweb.isilm.beaconguide.Beacon(
                            Integer.parseInt(jsonObjMaps.getJSONArray(TAG_OBJECTS).getJSONObject(k).get(TAG_NOME).toString()),
                            jsonObjMaps.getJSONArray(TAG_OBJECTS).getJSONObject(k).get(TAG_ZONA).toString(),
                            jsonObjMaps.getJSONArray(TAG_OBJECTS).getJSONObject(k).get(TAG_MAPID).toString(),
                            Double.parseDouble(jsonObjMaps.getJSONArray(TAG_OBJECTS).getJSONObject(k).get("top_x").toString()),
                            Double.parseDouble(jsonObjMaps.getJSONArray(TAG_OBJECTS).getJSONObject(k).get("top_y").toString()),
                            Double.parseDouble(jsonObjMaps.getJSONArray(TAG_OBJECTS).getJSONObject(k).get("bottom_x").toString()),
                            Double.parseDouble(jsonObjMaps.getJSONArray(TAG_OBJECTS).getJSONObject(k).get("bottom_y").toString()),
                            jsonObjMaps.getJSONArray(TAG_OBJECTS).getJSONObject(k).getJSONArray(TAG_VICINI),
                            jsonObjMaps.getJSONArray(TAG_OBJECTS).getJSONObject(k).getJSONArray(TAG_DIS),
                            graphNor,
                            graphDis
                    ));
                }
            }catch (Exception e) {
                Log.d("Failayy", e.toString());
            }
        }};
        try{
            Log.d("LengthMappa", Integer.toString(jsonObjMaps.getJSONArray("Mappa").length()));
            for(int p = 0; p < jsonObjMaps.getJSONArray("Mappa").length(); p++){
                String fileName = (String) jsonObjMaps.getJSONArray("Mappa").getJSONObject(p).get("id");
                urlmappa = (String) jsonObjMaps.getJSONArray("Mappa").getJSONObject(p).get("path");
                bitmap = new AsyncBitmapGet().execute(urlmappa).get(); //AsyncBitmapGet().execute(stringURL+"?map="+location).get(); //getBitmapFromURL((String) jsonObjMaps.getJSONArray("Mappa").getJSONObject(p).get("path"));

                FileOutputStream foStream;
                try {
                    foStream = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, foStream);
                    foStream.close();
                } catch (Exception e) {
                    Log.d("saveImage", "Exception 2, Something went wrong!");
                    e.printStackTrace();
                }

                /*
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                FileOutputStream fo = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                fo.write(bytes.toByteArray());
                // remember close file output
                fo.close();
                */

            }
        }
        catch (Exception e) {
            Log.d("FailGetJSONArrayMappa", e.toString());
        }
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
