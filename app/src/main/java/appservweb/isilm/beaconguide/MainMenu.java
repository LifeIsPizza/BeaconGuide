package appservweb.isilm.beaconguide;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class MainMenu extends AppCompatActivity implements TextToSpeech.OnInitListener {

    //Variables
    private TextToSpeech tts;
    private Button btnDownloadMap;
    private ListView menuListItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //Adattatore Bluetooth
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        Log.d("onCreate", "NewTTS");
        btnDownloadMap = (Button) findViewById(R.id.btnDownloadMap);
        menuListItems = (ListView) findViewById(R.id.lstDownloadedMaps);

        //Check se bluetooth e network sono abilitati.
        //Attenzione: se l'IF non ha successo, l'app si chiude. Se ha successo inizializziamo il TTS e procediamo
        if(checkNetwork() && checkBluetooth(mBluetoothAdapter)) {
            tts = new TextToSpeech(this, this);
            ListViewSpeaker speaker = new ListViewSpeaker(menuListItems, this, this);
            speaker.initialize();
        }

        //Lista di test
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };

        //Adattatore per lista
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        menuListItems.setAdapter(adapter);

        //Check di Permessi su Android SDK per beacons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {             final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                });
                builder.show();
            }
        }

        //Broadcast manager di messaggi per message passing da UI a Background thread di ranging
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
    }

    //Classe di Broadcast Receiver con comportamento custom per gestire i messaggi ricevuti
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            //Il thread di ranging manda messaggi a seconda degli spostamenti. Riceviamo i messaggi ed agiamo
            //Qui andrà gestito il messaggio, vengono fatte cose a seconda del beacon più vicino (incluso nel messaggio)
        }
    };

    //Setup del Text2Speech
    @Override
    public void onInit(int status) {
        Log.d("onInit", "If status tts success");
        if (status == TextToSpeech.SUCCESS) {
            Log.d("onInit", "setLanguage");
            int result = tts.setLanguage(Locale.ITALIAN);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                //btnSpeak.setEnabled(true);
                //speakOut();
                Log.d("Testing", "Inizializzazione corretta");
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    //Riempimento del menu opzioni
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    //Check se c'è connessione ad internet, ritorna true/false
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //Check se il bluetooth è attivo
    public boolean checkBluetooth(BluetoothAdapter mBluetoothAdapter){
        if (!mBluetoothAdapter.isEnabled()){
            AlertDialog alertDialog = new AlertDialog.Builder(MainMenu.this).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Bluetooth non abilitato. Abilitare e riaprire l'app.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
            return false;

        }
        else{
            return true;
        }
    }

    //Check se la connessione ad internet è presente
    public boolean checkNetwork(){
        if (!isOnline()){
            AlertDialog alertDialog = new AlertDialog.Builder(MainMenu.this).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Connessione alla rete non abilitata. Abilitare e riaprire l'app per scaricare dati mappe.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
            return false;

        }
        else{
            return true;
        }
    }
}

