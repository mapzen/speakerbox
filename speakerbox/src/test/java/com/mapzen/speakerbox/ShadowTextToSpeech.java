/*
 * Copyright 2014 Mapzen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mapzen.speakerbox;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;

@SuppressWarnings("unused")
@Implements(TextToSpeech.class)
public class ShadowTextToSpeech {
    private Context context;
    private TextToSpeech.OnInitListener listener;
    private String lastSpokenText;
    private boolean shutdown = false;
    private boolean stopped = false;

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

    @Implementation
    public int speak(final String text, final int queueMode, final HashMap<String, String> params) {
        lastSpokenText = text;
        return TextToSpeech.SUCCESS;
    }

    public String getLastSpokenText() {
        return lastSpokenText;
    }

    public void clearLastSpokenText() {
        lastSpokenText = null;
    }

    @Implementation
    public void shutdown() {
        shutdown = true;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    @Implementation
    public void stop() {
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }
}
