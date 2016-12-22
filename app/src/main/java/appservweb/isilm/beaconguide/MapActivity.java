package appservweb.isilm.beaconguide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapActivity extends AppCompatActivity implements SensorEventListener {

    private TextToSpeech tts;
    private ArrayList<Beacon> beacons;
    private Graph graph;
    private Search search;

    ImageView mapView;
    ImageView drawView;
    TextView textDirections;
    private int viewWidth;
    private int viewHeight;
    private String oldDirection = "";
    private final String arrivo = "Arrivato a Destinazione";

    private float currentDegree = 0;
    private int destinationDegree;
    Beacon myBeacon = null;
    private Beacon destinationBeacon;
    private int idNextBeacon;
    private ArrayList<Graph> graphs;
    // device sensor manager
    private SensorManager mSensorManager;

    private int phoneOrientation = 0;
    Display display;
    private boolean blindFlag;
    private boolean handicapFlag;

    private int lastIdReceived = -1;
    private int idReceivedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = (ImageView) findViewById(R.id.imageView2);
        drawView = (ImageView) findViewById(R.id.imageView);
        textDirections = (TextView) findViewById(R.id.textDirections);
        textDirections.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //do stuff
            }
        });

        Intent intent = getIntent();
        beacons = (ArrayList<Beacon>) intent.getSerializableExtra("beacons");

        destinationBeacon = selectDestBeacon(intent.getStringExtra("selected"));
        graphs = (ArrayList<Graph>) intent.getSerializableExtra("graphs");
        //1 è disabili

        //blindFlag = intent.getBooleanExtra("blindFlag", true);
        //handicapFlag = intent.getBooleanExtra("handicapFlag", true);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        blindFlag = settings.getBoolean("blindFlag", true);
        handicapFlag = settings.getBoolean("handicapFlag", false);
        if (handicapFlag){
            graph = graphs.get(1);
            Log.d("HandicapFlag", "Flag is true");
        }
        else {
            graph = graphs.get(0);
            Log.d("HandicapFlag", "Flag is false");
        }

        graph.toStringa();
        //Log.d("graph",graph.toString());
        search = new Search(graph);

        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //int myBeaconId = 0;
                //myBeacon = findInList(myBeaconId);
                //idNextBeacon = search.getNext(myBeacon.getIdb(),destinationBeacon.getIdb());
                //destinationDegree = getNextBeaconDegree(idNextBeacon);
                //loadNewMap();

                //drawMethod();
            }
        });


        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }


    @Override
    public void onRestart() {
        super.onRestart();
        //speaker.destroy();
        Log.d("MapActivity", "Mi sto Restartando");
        Intent changeActivity = new Intent(this, MainMenu.class);
        startActivity(changeActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (blindFlag)
            resumeTTS();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("intent_nearest"));
        // for the system's orientation sensor registered listeners

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSensorManager.unregisterListener(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("CDA", "onBackPressed Called");
        Intent changeActivity = new Intent(this, BeaconsMenu.class);
        changeActivity.putExtra("beacons", beacons);
        changeActivity.putExtra("graphs", graphs);
        startActivity(changeActivity);
    }

    //Classe di Broadcast Receiver con comportamento custom per gestire i messaggi ricevuti
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getIntExtra("id", 0) == 3) { //ID = 3, è il nuovo beacon
                Log.d("MessaggioRic", Integer.toString(intent.getIntExtra("idBeac", 0)));
                /*
                Beacon nearest;
                nearest = (Beacon) intent.getSerializableExtra("nearest");
                onMyBeaconChanged(nearest.getIdb());
                */
                if (myBeacon == null){
                    onMyBeaconChanged(intent.getIntExtra("idBeac", 0));
                    lastIdReceived = intent.getIntExtra("idBeac", 0);
                    idReceivedCount = 0;
                }
                else {
                    int newBeacon = intent.getIntExtra("idBeac", 0);
                    if (newBeacon != myBeacon.getIdb()){
                        if(newBeacon == lastIdReceived) {
                            idReceivedCount++;
                            if (idReceivedCount >= 5 && isANearby(newBeacon)) {
                                myBeacon = findInList(lastIdReceived);
                                onMyBeaconChanged(newBeacon);
                            }
                            else if(idReceivedCount >= 10) {
                                myBeacon = findInList(lastIdReceived);
                                onMyBeaconChanged(newBeacon);
                            }
                        }
                        else {
                            lastIdReceived = newBeacon;
                            idReceivedCount = 1;
                        }
                    }
                }
                //onMyBeaconChanged(intent.getIntExtra("idBeac", 0));
            }
        }
    };

    void drawMethod(){
        viewHeight = mapView.getHeight();
        viewWidth = mapView.getWidth();

        Bitmap.Config conf = Bitmap.Config.ARGB_4444;
        Bitmap bmp = Bitmap.createBitmap(viewWidth,viewHeight,conf);
        Canvas canvas = new Canvas(bmp);

        double startX = myBeacon.getTop_x()*viewWidth;
        double startY = myBeacon.getTop_y()* viewHeight;
        double endX = myBeacon.getBottom_x()*viewWidth;
        double endY = myBeacon.getBottom_y()*viewHeight;

        Paint myPaint = new Paint();

        myPaint.setColor(Color.rgb(0,0,0));
        myPaint.setStrokeWidth(7);
        canvas.drawRect((int)startX, (int)startY, (int)endX, (int)endY, myPaint);
        myPaint.setColor(Color.rgb(255, 255, 255));
        myPaint.setStrokeWidth(0);
        canvas.drawRect((int)startX + 7, (int)startY +7, (int)endX -7, (int)endY -7, myPaint);
        drawView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        drawView.setAlpha((float) 0.7);
        drawView.setImageBitmap(bmp);

    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentDegree = Math.round(sensorEvent.values[0]);

        phoneOrientation = display.getRotation()*90;
        phoneOrientation = (360 - phoneOrientation)%360;

        //Log.d("degree: " + currentDegree, "MainMenu");
        currentDegree = (int)destinationDegree -currentDegree;
        //Log.d("degree: " + currentDegree, "MainMenu");

        if(currentDegree<0)
            currentDegree = 360+currentDegree;

        currentDegree = (currentDegree + phoneOrientation)%360;


        String directions = "";
        if(currentDegree>=330 ||currentDegree<30)
            directions = "vai DRITTO";
        else if(currentDegree>=30 &&currentDegree<150)
            directions = "gira a DESTRA";
        else if(currentDegree>=150 &&currentDegree<210)
            directions = "torna INDIETRO";
        else if(currentDegree>=210 &&currentDegree<330)
            directions = "gira a SINISTRA";
        //Log.d("degree: " + currentDegree, "destin: " + destinationDegree);
        if(!directions.equals(oldDirection) && !textDirections.getText().toString().equals(arrivo))
        {
            oldDirection = directions;
            textDirections.setText(directions);
            if (blindFlag)
                tts.speak(oldDirection, TextToSpeech.QUEUE_FLUSH, null, null);
            //if (speaker != null)
            //    speaker.speakItem(oldDirection);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onMyBeaconChanged(int newID){
        if (myBeacon == null) {
            myBeacon = findInList(newID);
            loadNewMap();
        }
        else if(myBeacon.getMap_id()!= findInList(newID).getMap_id()){
            myBeacon = findInList(newID);
            loadNewMap();
        }
        else {
            myBeacon = findInList(newID);
        }

        Log.d("destin", Integer.toString(destinationBeacon.getIdb()));
        Log.d("mybeacon", Integer.toString(myBeacon.getIdb()));

        Log.d("GETNEXT di", Integer.toString(myBeacon.getIdb()) +", "+ Integer.toString(destinationBeacon.getIdb()));

        if (myBeacon.getIdb() == destinationBeacon.getIdb()){
            textDirections.setText(arrivo);
            if (blindFlag)
                tts.speak(arrivo, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else{
            idNextBeacon = search.getNext(myBeacon.getIdb(),destinationBeacon.getIdb());
            Log.d("nextbeacon", Integer.toString(idNextBeacon));
            destinationDegree = getNextBeaconDegree(idNextBeacon);
        }
        drawMethod();
    }

    private void loadNewMap() {
        Bitmap.Config conf = Bitmap.Config.ARGB_4444;
        Bitmap bmp = Bitmap.createBitmap(mapView.getWidth(),mapView.getHeight(),conf);
        try {
            //bmp = BitmapFactory.decodeStream(this.openFileInput("1"));
            FileInputStream fiStream;
            fiStream = getApplicationContext().openFileInput(myBeacon.getMap_id());
            bmp = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
            //bmp = BitmapFactory.decodeStream(getApplicationContext().openFileInput(myBeacon.getMap_id()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.setImageBitmap(bmp);
    }

    private Beacon findInList(int id){
        for(int i = 0; i < beacons.size(); i++)
            if(beacons.get(i).getIdb()== id)
                return  beacons.get(i);

        return null;
    }

    private int getNextBeaconDegree(int idNextBeacon){
        ArrayList<Nearby> nearby = myBeacon.getVicini();
        if(nearby!= null){
            for(int i = 0; i < nearby.size();i++){
                if(nearby.get(i).getIde() == idNextBeacon)
                    return nearby.get(i).getGradi();
            }
        }
        return 0;
    }

    private void resumeTTS(){
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Log.d("onInit", "setLanguage");
                    int result = tts.setLanguage(Locale.ITALIAN);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    } else {
                        Log.d("Testing", "Inizializzazione corretta");
                    }
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });
    }

    private Beacon selectDestBeacon (String search){

            for (Beacon beac : beacons) {
                if (beac.getZona().equals(search)) {
                    return beac;
                }
            }
            return null;

    }

    private boolean isANearby(int newID) {

        boolean isANearby = false;
        if (handicapFlag){
            for (Nearby n : myBeacon.getVic_dis()) {
                if(n.getIde()==newID) {
                    Log.d(n.getIde()+ "",newID+ "");
                    isANearby = true;
                }
            }
        }
        else{
            for (Nearby n : myBeacon.getVicini()) {
                if(n.getIde()==newID) {
                    Log.d(n.getIde()+ "",newID+ "");
                    isANearby = true;
                }
            }
        }

        return  isANearby;
    }

}
