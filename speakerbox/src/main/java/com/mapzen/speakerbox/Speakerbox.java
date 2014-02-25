package com.mapzen.speakerbox;

import android.app.Activity;
import android.speech.tts.TextToSpeech;

public class Speakerbox implements TextToSpeech.OnInitListener {
    final Activity activity;
    final TextToSpeech textToSpeech;

    public Speakerbox(Activity activity) {
        this.activity = activity;
        this.textToSpeech = new TextToSpeech(activity, this);
    }

    @Override
    public void onInit(int i) {
    }

    public void play(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
