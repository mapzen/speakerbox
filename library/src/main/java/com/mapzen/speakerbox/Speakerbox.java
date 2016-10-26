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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

public class Speakerbox implements TextToSpeech.OnInitListener {
    final static String TAG = Speakerbox.class.getSimpleName();

    /**
     * Pitch when we have focus
     */
    private static final float FOCUS_PITCH = 1.0f;
    /**
     * Pitch when we should duck audio for another app
     */
    private static final float DUCK_PITCH = 0.5f;
    /**
     * ID for when no text is spoken
     */
    public static final String UTTERANCE_ID_NONE = "-1";

    private final TextToSpeech textToSpeech;

    private final Application application;

    /**
     * Callbacks are registered for upon initialization. Set an activity on the Speakerbox
     * object to have this class take care of shutting down the TextToSpeech object or register
     * for {@link android.app.Application.ActivityLifecycleCallbacks} in your application and
     */
    private final Application.ActivityLifecycleCallbacks callbacks;

    /**
     * If set, this class will shut itself down when the activity is destroyed. Only set an
     * activity if you want the Speakerbox's state to be tied to the activity lifecycle
     */
    private Activity activity = null;

    private boolean initialized = false;
    private boolean muted = false;
    private String playOnInit = null;
    private int queueMode = TextToSpeech.QUEUE_FLUSH;

    private final LinkedHashMap<String, String> samples = new LinkedHashMap<String, String>();
    private final ArrayList<String> unwantedPhrases = new ArrayList<String>();

    private HashMap<String, Runnable> onStartRunnables = new HashMap<String, Runnable>();
    private HashMap<String, Runnable> onDoneRunnables = new HashMap<String, Runnable>();
    private HashMap<String, Runnable> onErrorRunnables = new HashMap<String, Runnable>();

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    textToSpeech.setPitch(FOCUS_PITCH);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    textToSpeech.setPitch(DUCK_PITCH);
                    break;
            }
        }
    };

    UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
            detectAndRun(utteranceId, onStartRunnables);
        }

        @Override
        public void onDone(String utteranceId) {
            if (detectAndRun(utteranceId, onDoneRunnables)) {
                // because either onDone or onError will be called for an utteranceId, cleanup other
                if (onErrorRunnables.containsKey(utteranceId)) {
                    onErrorRunnables.remove(utteranceId);
                }
            }
        }

        @Override
        public void onError(String utteranceId) {
            if (detectAndRun(utteranceId, onErrorRunnables)) {
                // because either onDone or onError will be called for an utteranceId, cleanup other
                if (onDoneRunnables.containsKey(utteranceId)) {
                    onDoneRunnables.remove(utteranceId);
                }
            }
        }
    };

    public Speakerbox(final Application application) {
        this.application = application;
        this.textToSpeech = new TextToSpeech(application, this);
        this.textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
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
                    shutdown();
                }
            }
        };
        application.registerActivityLifecycleCallbacks(callbacks);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            initialized = true;
            if (playOnInit != null) {
                playInternal(playOnInit, UTTERANCE_ID_NONE);
            }
        } else {
            Log.e(TAG, "Initialization failed.");
        }
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        enableVolumeControl(this.activity);
    }

    public Application.ActivityLifecycleCallbacks getCallbacks() {
        return callbacks;
    }

    public void play(CharSequence text) {
        play(text.toString(), null, null, null);
    }

    /**
     * Play text and perform action when text starts playing
     *
     * Runnable is run on the main thread
     * @param text
     * @param onStart
     */
    public void playAndOnStart(String text, Runnable onStart) {
        play(text, onStart, null, null);
    }

    /**
     * Play text and perform action when text finishes playing
     *
     * Runnable is run on the main thread
     * @param text
     * @param onDone
     */
    public void playAndOnDone(String text, Runnable onDone) {
        play(text, null, onDone, null);
    }

    /**
     * Play text and perform action when error occurs in playback
     *
     * Runnable is run on the main thread
     * @param text
     * @param onError
     */
    public void playAndOnError(String text, Runnable onError) {
        play(text, null, null, onError);
    }

    /**
     * Play text and perform actions when text starts playing, text finishes playing or
     * text incurs error playing. Note that {@param onDone} and {@param onError} are mutually
     * exclusive and only one will be called. All runnables are run on the main thread
     *
     * @param text
     * @param onStart
     */
    public void play(String text, Runnable onStart, Runnable onDone, Runnable onError) {
        if(doesNotContainUnwantedPhrase(text)) {
            text = applyRemixes(text);
            if (initialized) {
                String utteranceId = String.valueOf(SystemClock.currentThreadTimeMillis());
                if (onStart != null) {
                    onStartRunnables.put(utteranceId, onStart);
                }
                if (onDone != null) {
                    onDoneRunnables.put(utteranceId, onDone);
                }
                if (onError != null) {
                    onErrorRunnables.put(utteranceId, onError);
                }
                playInternal(text, utteranceId);
            } else {
                playOnInit = text;
            }
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

    private void playInternal(String text, String utteranceId) {
        if (muted) {
            return;
        }
        Log.d(TAG, "Playing: \""+ text + "\"");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, queueMode, null, utteranceId);
        } else {
            final HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
            textToSpeech.speak(text, queueMode, params);
        }
    }

    public void dontPlayIfContains(String text) {
        unwantedPhrases.add(text);
    }

    private boolean doesNotContainUnwantedPhrase(String text){
        for(String invalid : unwantedPhrases) {
            if(text.contains(invalid)) {
                return false;
            }
        }
        return true;
    }

    public void mute() {
        muted = true;
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
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

    public void requestAudioFocus() {
        final AudioManager am = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
    }

    public void abandonAudioFocus() {
        AudioManager am = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(audioFocusChangeListener);
    }

    public void enableVolumeControl(Activity activity) {
        if (activity != null) {
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    public void disableVolumeControl(Activity activity) {
        if (activity != null) {
            activity.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }
    }

    public void setQueueMode(int queueMode) {
        this.queueMode = queueMode;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Set<Locale> getAvailableLanguages() {
        return textToSpeech.getAvailableLanguages();
    }

    public void setLanguage(Locale locale) {
        textToSpeech.setLanguage(locale);
    }

    /**
     * Shutdown the {@link TextToSpeech} object and unregister activity lifecycle callbacks
     */
    public void shutdown() {
        textToSpeech.shutdown();
        application.unregisterActivityLifecycleCallbacks(callbacks);
    }

    /**
     * Find the runnable for a given utterance id, run it on the main thread and then remove
     * it from the map
     * @param utteranceId the id key to use
     * @param hashMap utteranceIds to runnable map to use
     * @return whether value was found
     */
    private boolean detectAndRun(String utteranceId, HashMap<String, Runnable> hashMap) {
        if (hashMap.containsKey(utteranceId)) {
            Runnable runnable = hashMap.get(utteranceId);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(runnable);
            hashMap.remove(utteranceId);
            return true;
        } else {
            return false;
        }
    }
}
