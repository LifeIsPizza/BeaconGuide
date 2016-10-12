package appservweb.isilm.beaconguide;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

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
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        Log.d("onCreate", "NewTTS");
        btnDownloadMap = (Button) findViewById(R.id.btnDownloadMap);
        menuListItems = (ListView) findViewById(R.id.lstDownloadedMaps);
        if(checkNetwork() && checkBluetooth(mBluetoothAdapter)) {
            tts = new TextToSpeech(this, this);
            ListViewSpeaker speaker = new ListViewSpeaker(menuListItems, this, this);
            speaker.initialize();
        }
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        menuListItems.setAdapter(adapter);

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
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

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


//    @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


}

