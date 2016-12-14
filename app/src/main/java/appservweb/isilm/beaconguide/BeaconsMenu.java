package appservweb.isilm.beaconguide;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

import java.util.ArrayList;

public class BeaconsMenu extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ListView menuListItems;
    private ListViewSpeaker speaker;
    private ArrayList<String> values;
    private ArrayAdapter<String> adapter;
    private ArrayList<Beacon> beacons;
    private ArrayList<Graph> graphs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setActionBar(toolbar);
        menuListItems = (ListView) findViewById(R.id.lstBeacons);

        //Lista di test
        values = new ArrayList<String>();
        Intent intent = getIntent();
        beacons = (ArrayList<Beacon>) intent.getSerializableExtra("beacons");
        for (int k = 0; k < beacons.size(); k++ ){
            values.add(beacons.get(k).getZona());
        }

        graphs = (ArrayList<Graph>) intent.getSerializableExtra("graphs");



        /*
        speaker = new ListViewSpeaker(this, this);
        menuListItems.setLongClickable(true);
        menuListItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = menuListItems.getItemAtPosition(position);
                speaker.speakItem(listItem.toString());
            }
        });
        menuListItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("main long clicked","pos: " + position);
                Object listItem = menuListItems.getItemAtPosition(position);
                speaker.speakItem(listItem.toString());
                changeAct(beacons);
                return true;
            }

        });
        */


        //Adattatore per lista
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        menuListItems.setAdapter(adapter);

        //Broadcast manager di messaggi per message passing da UI a Background thread di ranging
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name3"));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);

        }
    };

    private void changeAct(ArrayList<Beacon> beacons, ArrayList<Graph> graphs, String selected){
        Intent changeActivity = new Intent(this, MapActivity.class);
        changeActivity.putExtra("beacons", beacons);
        changeActivity.putExtra("graphs", graphs);
        if(graphs.isEmpty())
            Log.d("CheckinGraphs", "Empty");
        else
            Log.d("CheckinGraphs", "NotEmpty");
        changeActivity.putExtra("selected", selected);
        startActivity(changeActivity);
    }

    @Override
    public void onBackPressed() {
        Intent changeActivity = new Intent(this, MainMenu.class);
        startActivity(changeActivity);
    }

    @Override
    public void onInit(int status) {
        Log.d("onInit", "BeaconsMenu");
        speaker.onInit(status);
    }

    @Override
    public void onStop() {
        super.onStop();
        //speaker.destroy();
        Log.d("BeaconsMenu", "Mi sto Stoppando");
    }

    @Override
    public void onPause() {
        super.onPause();
        //speaker.destroy();
        Log.d("BeaconsMenu", "Mi sto Pausando");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //speaker.destroy();
        Log.d("BeaconsMenu", "Mi sto Restartando");
        onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        speaker = new ListViewSpeaker(this, this);
        menuListItems.setLongClickable(true);
        menuListItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = menuListItems.getItemAtPosition(position);
                speaker.speakItem(listItem.toString());
            }
        });
        menuListItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("main long clicked","pos: " + position);
                Object listItem = menuListItems.getItemAtPosition(position);
                speaker.speakItem(listItem.toString());
                changeAct(beacons, graphs, listItem.toString());
                return true;
            }

        });
    }
}
