package appservweb.isilm.beaconguide;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;
import java.util.Locale;

/**
 * Created by Enrico on 12/10/2016.
 */

public class ListViewSpeaker {

    private TextToSpeech tts;

    public ListViewSpeaker(Context context, OnInitListener listener){
        tts = new TextToSpeech(context, listener);
    }

    public void onInit(int status) {
        Log.d("onInit", "LVSpeaker");
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

    public void speakItem(String item) {
        Log.d("Derp", "Speaking: " + item);
        tts.speak(item, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
