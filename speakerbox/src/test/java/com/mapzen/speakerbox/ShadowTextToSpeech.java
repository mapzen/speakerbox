package com.mapzen.speakerbox;

import org.robolectric.annotation.Implements;

import android.content.Context;
import android.speech.tts.TextToSpeech;

@Implements(TextToSpeech.class)
public class ShadowTextToSpeech {
    private Context context;
    private TextToSpeech.OnInitListener listener;

    @SuppressWarnings("unused")
    public void __constructor__(Context context, TextToSpeech.OnInitListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public Context getContext() {
        return context;
    }

    public TextToSpeech.OnInitListener getOnInitListener() {
        return listener;
    }
}
