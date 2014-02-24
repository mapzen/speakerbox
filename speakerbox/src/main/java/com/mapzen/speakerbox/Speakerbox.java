package com.mapzen.speakerbox;

import android.content.Context;
import android.speech.tts.TextToSpeech;

public class Speakerbox implements TextToSpeech.OnInitListener {
    final Context context;
    final TextToSpeech textToSpeech;

    public Speakerbox(Context context) {
        this.context = context;
        this.textToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int i) {
    }

    public void play(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
