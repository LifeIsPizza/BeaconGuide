package appservweb.isilm.beaconguide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MapActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,SensorEventListener {

    private ListViewSpeaker speaker;
    private ArrayList<Beacon> beacons;

    ImageView mapView;
    ImageView drawView;
    TextView textDirections;
    private int viewWidth;
    private int viewHeight;
    private String oldDirection = "";

    private float currentDegree = 0f;
    private int destinationDegree;
    Beacon myBeacon;
    private int idNextBeacon;
    private int idDestinationBeacon;
    // device sensor manager
    private SensorManager mSensorManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = (ImageView) findViewById(R.id.imageView2);
        drawView = (ImageView) findViewById(R.id.imageView);
        textDirections = (TextView) findViewById(R.id.textDirections);
        Intent intent = getIntent();
        beacons = (ArrayList<Beacon>) intent.getSerializableExtra("beacons");
        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Random r = new Random();
                int myBeaconId = Math.abs(r.nextInt()%3) +1;
                idNextBeacon = (myBeaconId +1)%3;
                myBeacon = findInList(myBeaconId);
                destinationDegree = getNextBeaconDegree(idNextBeacon);
                Log.d("DESTINATION DEGREE "+ destinationDegree, "main menu");
                drawMethod();
            }
        });


    }


    public void onInit(int status) {
        Log.d("onInit", "MainMenu");
        speaker.onInit(status);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onDestroy() {
        speaker.destroy();
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners

    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent changeActivity = new Intent(this, BeaconsMenu.class);
        changeActivity.putExtra("beacons", beacons);
        startActivity(changeActivity);
    }

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
        Log.d("degree: " + currentDegree, "MainMenu");
        currentDegree = (int)destinationDegree -currentDegree;
        Log.d("degree: " + currentDegree, "MainMenu");

        if(currentDegree<0)
            currentDegree = 360+currentDegree;
        String directions = "";
        if(currentDegree>=340 ||currentDegree<20)
            directions = "vai DRITTO";
        else if(currentDegree>=20 &&currentDegree<160)
            directions = "gira a DESTRA";
        else if(currentDegree>=160 &&currentDegree<200)
            directions = "torna INDIETRO";
        else if(currentDegree>=200 &&currentDegree<340)
            directions = "gira a SINISTRA";
        if(directions!=oldDirection)
        {
            oldDirection = directions;
            textDirections.setText(directions);
            speaker.speakItem(oldDirection);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onMyBeaconChanged(int newID){
        if(myBeacon.getMap_id()!= findInList(newID).getMap_id()){
            loadNewMap();
        }
        myBeacon = findInList(newID);

        //idNextBeacon = algorithm();
        idNextBeacon = (myBeacon.getIdb() +1)%3;
        destinationDegree = getNextBeaconDegree(idNextBeacon);
        drawMethod();
    }

    private void loadNewMap() {
        //mapView.setImageBitmap();
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

}
