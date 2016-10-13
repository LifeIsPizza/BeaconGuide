package appservweb.isilm.beaconguide;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Enrico on 12/10/2016.
 */

public class ListViewSpeaker {

    private ListView speakview;
    private TextToSpeech tts;

    public ListViewSpeaker(ListView view, Context context, OnInitListener listener){
        speakview = view;
        tts = new TextToSpeech(context, listener);
    }

    public void initialize(){
        speakview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = speakview.getItemAtPosition(position);
                speakItem(listItem.toString());
            }
        });

    }

    private void speakItem(String item) {
        Log.d(null, "Speaking:");
        tts.speak(item, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    //Not Used, for reference
    private void speakOut(EditText txtText) {

        String text = txtText.getText().toString();

        //tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }


}
