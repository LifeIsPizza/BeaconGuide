package appservweb.isilm.beaconguide;

import android.app.LauncherActivity;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        Log.d(null, "NewTTS");
        tts = new TextToSpeech(this, this);
        btnDownloadMap = (Button) findViewById(R.id.btnDownloadMap);
        menuListItems = (ListView) findViewById(R.id.lstDownloadedMaps);
        ListViewSpeaker speaker = new ListViewSpeaker(menuListItems, this, this);
        speaker.initialize();
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

