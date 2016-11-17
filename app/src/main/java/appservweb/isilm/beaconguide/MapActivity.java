package appservweb.isilm.beaconguide;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapActivity extends AppCompatActivity {

    List<BeaconArea> listBeaconArea;
    ImageView mapView;
    ImageView drawView;
    private int viewWidth;
    private int viewHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = (ImageView) findViewById(R.id.imageView2);
        drawView = (ImageView) findViewById(R.id.imageView);
        listBeaconArea = new ArrayList<BeaconArea>();
        listBeaconArea.add(new BeaconArea(1,1,0,0,1,205/705));
        listBeaconArea.add(new BeaconArea(2,1,0,206/751,533/1058,552/751));
        listBeaconArea.add(new BeaconArea(3,1,0,553/751,533/1058,1));
        listBeaconArea.add(new BeaconArea(4,2,0,0,1,205/751));

        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                drawMethod();
            }
        });


    }

    void drawMethod(){
        viewHeight = mapView.getHeight();
        viewWidth = mapView.getWidth();

        Random r = new Random();
        int myBeaconId = Math.abs(r.nextInt()%3) +1;
        System.out.println("beacon: " +myBeaconId);
        BeaconArea beacon = listBeaconArea.get(myBeaconId -1);

        Bitmap.Config conf = Bitmap.Config.ARGB_4444;
        Bitmap bmp = Bitmap.createBitmap(viewWidth,viewHeight,conf);
        Canvas canvas = new Canvas(bmp);


        System.out.println(viewHeight + " " +viewWidth);
        double startX = beacon.getStartX()*viewWidth;
        double startY = beacon.getStartY()* viewHeight;
        double endX = beacon.getEndX()*viewWidth;
        double endY = beacon.getEndY()*viewHeight;

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
}