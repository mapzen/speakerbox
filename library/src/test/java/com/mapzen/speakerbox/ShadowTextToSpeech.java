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
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@SuppressWarnings("unused")
@Implements(TextToSpeech.class)
public class ShadowTextToSpeech {
    private Context context;
    private TextToSpeech.OnInitListener listener;
    private String lastSpokenText;
    private boolean shutdown = false;
    private boolean stopped = false;
    private boolean isSpeaking = false;
    private int queueMode = -1;
    private UtteranceProgressListener utteranceProgressListener;
    private boolean finishOnSpeak = false;
    private boolean errorOnSpeak = false;
    private Locale language = null;

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
        this.queueMode = queueMode;
        isSpeaking = true;
        final String utteranceId = params.get(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
        notifyProgressListener(utteranceId);
        return TextToSpeech.SUCCESS;
    }

    @Implementation
    public int speak(final CharSequence text,
            final int queueMode,
            final Bundle params,
            final String utteranceId) {
        lastSpokenText = text.toString();
        this.queueMode = queueMode;
        isSpeaking = true;
        notifyProgressListener(utteranceId);
        return TextToSpeech.SUCCESS;
    }

    @Implementation
    public int setOnUtteranceProgressListener(UtteranceProgressListener listener) {
        utteranceProgressListener = listener;
        return TextToSpeech.SUCCESS;
    }

    public String getLastSpokenText() {
        return lastSpokenText;
    }

    public void clearLastSpokenText() {
        lastSpokenText = null;
    }

    public int getQueueMode() {
        return queueMode;
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
        isSpeaking = false;
    }

    public boolean isStopped() {
        return stopped;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public void setFinishOnSpeak(boolean finishOnSpeak) {
        this.finishOnSpeak = finishOnSpeak;
    }

    public void setErrorOnSpeak(boolean errorOnSpeak) {
        this.errorOnSpeak = errorOnSpeak;
    }

    @Implementation
    public Set<Locale> getAvailableLanguages() {
        HashSet languages = new HashSet<>();
        languages.add(Locale.CANADA_FRENCH);
        languages.add(Locale.FRENCH);
        return languages;
    }

    @Implementation
    public void setLanguage(Locale locale) {
        language = locale;
    }

    public Locale getLanguage() {
        return language;
    }

    private void notifyProgressListener(String utteranceId) {
        utteranceProgressListener.onStart(utteranceId);
        if (finishOnSpeak) {
            utteranceProgressListener.onDone(utteranceId);
        }
        if (errorOnSpeak) {
            utteranceProgressListener.onError(utteranceId);
        }
    }
}
