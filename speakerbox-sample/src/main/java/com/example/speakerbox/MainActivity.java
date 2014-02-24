package com.example.speakerbox;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SpeakerboxFragment())
                    .commit();
        }
    }

    public static class SpeakerboxFragment extends Fragment
            implements TextToSpeech.OnInitListener {

        private TextToSpeech textToSpeech;
        private TextView textView;
        private Button button;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main, container, false);
            textView = (EditText) view.findViewById(R.id.text);
            button = (Button) view.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speak();
                }
            });
            textToSpeech = new TextToSpeech(getActivity(), this);
            textToSpeech.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            return view;
        }

        @Override
        public void onInit(int status) {
        }

        private void speak() {
            String textToSpeak = textView.getText().toString();
            if (!TextUtils.isEmpty(textToSpeak)) {
                textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }
}
