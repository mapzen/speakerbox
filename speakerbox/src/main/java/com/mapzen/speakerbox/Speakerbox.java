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

import android.app.Activity;
import android.app.Application;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.LinkedHashMap;

public class Speakerbox implements TextToSpeech.OnInitListener {
    final static String TAG = Speakerbox.class.getSimpleName();

    final Activity activity;
    final TextToSpeech textToSpeech;
    final Application.ActivityLifecycleCallbacks callbacks;

    private boolean initialized = false;
    private boolean muted = false;
    private String playOnInit = null;

    private final LinkedHashMap<String, String> samples = new LinkedHashMap<String, String>();

    public Speakerbox(Activity activity) {
        this.activity = activity;
        this.textToSpeech = new TextToSpeech(activity, this);

        final Application application = (Application) activity.getApplicationContext();
        this.callbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (Speakerbox.this.activity == activity) {
                    textToSpeech.shutdown();
                    application.unregisterActivityLifecycleCallbacks(this);
                }
            }
        };

        application.registerActivityLifecycleCallbacks(callbacks);
        enableVolumeControl();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            initialized = true;
            if (playOnInit != null) {
                playInternal(playOnInit);
            }
        } else {
            Log.e(TAG, "Initialization failed.");
        }
    }

    public void play(CharSequence text) {
        play(text.toString());
    }

    public void play(String text) {
        text = applyRemixes(text);
        if (initialized) {
            playInternal(text);
        } else {
            playOnInit = text;
        }
    }

    public void stop() {
	textToSpeech.stop();
    }

    private String applyRemixes(String text) {
        for (String key : samples.keySet()) {
            if (text.contains(key)) {
                text = text.replace(key, samples.get(key));
            }
        }

        return text;
    }

    private void playInternal(String text) {
        if (!muted) {
            Log.d(TAG, "Playing: \""+ text + "\"");
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void mute() {
        muted = true;
    }

    public void unmute() {
        muted = false;
    }

    public boolean isMuted() {
        return muted;
    }

    public void remix(String original, String remix) {
        samples.put(original, remix);
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

    public void enableVolumeControl() {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public void disableVolumeControl() {
        activity.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }
}
