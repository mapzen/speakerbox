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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowLog;

import android.app.Activity;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fest.reflect.core.Reflection.field;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = com.mapzen.speakerbox.ShadowTextToSpeech.class)
public class SpeakerboxTest {
    private Activity activity;
    private Speakerbox speakerbox;
    private com.mapzen.speakerbox.ShadowTextToSpeech shadowTextToSpeech;

    @Before
    public void setUp() throws Exception {
        init();
        shadowTextToSpeech.getOnInitListener().onInit(TextToSpeech.SUCCESS);
    }

    private void init() {
        activity = Robolectric.buildActivity(Activity.class).create().start().resume().get();
        speakerbox = new Speakerbox(activity.getApplication());
        speakerbox.setActivity(activity);
        shadowTextToSpeech = (ShadowTextToSpeech) ShadowExtractor.extract(
                speakerbox.getTextToSpeech());
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertThat(speakerbox).isNotNull();
    }

    @Test
    public void shouldInitTextToSpeech() throws Exception {
        assertThat(shadowTextToSpeech.getContext()).isEqualTo(activity.getApplication());
        assertThat(shadowTextToSpeech.getOnInitListener()).isEqualTo(speakerbox);
    }

    @Test
    public void shouldPlayString() throws Exception {
        String expected = "Hello";
        speakerbox.play(expected);
        assertThat(shadowTextToSpeech.getLastSpokenText()).isEqualTo(expected);
    }

    @Test
    public void shouldPlayCharacterSequence() throws Exception {
        CharSequence expected = "Hello";
        speakerbox.play(expected);
        assertThat(shadowTextToSpeech.getLastSpokenText()).isEqualTo(expected.toString());
    }

    @Test
    public void shouldShutdownTextToSpeechOnActivityDestroyed() throws Exception {
        speakerbox.getCallbacks().onActivityDestroyed(activity);
        assertThat(shadowTextToSpeech.isShutdown()).isTrue();
    }

    @Test
    public void shouldNotShutdownTextToSpeechOnAnotherActivityDestroyed() throws Exception {
        speakerbox.getCallbacks().onActivityDestroyed(new Activity());
        assertThat(shadowTextToSpeech.isShutdown()).isFalse();
    }

    @Test
    public void shouldUnregisterLifecycleCallbacksOnActivityDestroyed() throws Exception {
        speakerbox.getCallbacks().onActivityDestroyed(activity);
        ArrayList callbackList = field("mActivityLifecycleCallbacks")
                .ofType(ArrayList.class)
                .in(application)
                .get();
        assertThat(callbackList).isEmpty();
    }

    @Test
    public void shouldNotPlayWhenMuted() throws Exception {
        speakerbox.mute();
        speakerbox.play("Hello");
        assertThat(shadowTextToSpeech.getLastSpokenText()).isNull();
    }

    @Test
    public void shouldPlayAfterUnmute() throws Exception {
        speakerbox.mute();
        speakerbox.unmute();
        speakerbox.play("Hello");
        assertThat(shadowTextToSpeech.getLastSpokenText()).isEqualTo("Hello");
    }

    @Test
    public void shouldReturnMuteState() throws Exception {
        speakerbox.mute();
        assertThat(speakerbox.isMuted()).isTrue();

        speakerbox.unmute();
        assertThat(speakerbox.isMuted()).isFalse();
    }

    @Test
    public void shouldStopOnMute() {
        speakerbox.unmute();
        speakerbox.play("Hello");
        speakerbox.mute();
        assertThat(speakerbox.getTextToSpeech().isSpeaking()).isFalse();
    }

    @Test
    public void shouldLogInitializationError() throws Exception {
        shadowTextToSpeech.getOnInitListener().onInit(TextToSpeech.ERROR);
        assertThat(ShadowLog.getLogs().get(0).tag).isEqualTo(Speakerbox.TAG);
        assertThat(ShadowLog.getLogs().get(0).msg).isEqualTo("Initialization failed.");
    }

    @Test
    public void shouldPlayLastSavedTextOnInit() throws Exception {
        init();
        speakerbox.play("Hello");
        shadowTextToSpeech.clearLastSpokenText();
        shadowTextToSpeech.getOnInitListener().onInit(TextToSpeech.SUCCESS);
        assertThat(shadowTextToSpeech.getLastSpokenText()).isEqualTo("Hello");
    }

    @Test
    public void shouldSubstituteRemixedText() throws Exception {
        speakerbox.remix("Hello", "Hi");
        speakerbox.remix("Goodbye", "Bye");

        speakerbox.play("Hello");
        assertThat(shadowTextToSpeech.getLastSpokenText()).isEqualTo("Hi");

        speakerbox.play("Goodbye");
        assertThat(shadowTextToSpeech.getLastSpokenText()).isEqualTo("Bye");
    }

    @Test
    public void shouldPreserveRemixOrder() throws Exception {
        speakerbox.remix("Hello", "Hi");
        speakerbox.remix("Hi", "Yo");
        speakerbox.play("Hello");
        assertThat(shadowTextToSpeech.getLastSpokenText()).isEqualTo("Yo");
    }

    @Test
    public void shouldNotRemixSavedTextAgain() throws Exception {
        init();
        speakerbox.remix("Hi", "Hi Hi");
        speakerbox.play("Hi");
        shadowTextToSpeech.clearLastSpokenText();
        shadowTextToSpeech.getOnInitListener().onInit(TextToSpeech.SUCCESS);
        assertThat(shadowTextToSpeech.getLastSpokenText()).isEqualTo("Hi Hi");
    }

    @Test
    public void shouldReturnUnderlyingTextToSpeechInstance() throws Exception {
        assertThat(speakerbox.getTextToSpeech()).isEqualTo(speakerbox.getTextToSpeech());
    }

    @Test
    public void shouldEnableVolumeControlByDefault() throws Exception {
        init();
        assertThat(activity.getVolumeControlStream()).isEqualTo(AudioManager.STREAM_MUSIC);
    }

    @Test
    public void shouldDisableVolumeControl() throws Exception {
        init();
        speakerbox.disableVolumeControl(activity);
        assertThat(activity.getVolumeControlStream())
                .isEqualTo(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    @Test
    public void shouldReEnableVolumeControl() throws Exception {
        init();
        speakerbox.disableVolumeControl(activity);
        speakerbox.enableVolumeControl(activity);
        assertThat(activity.getVolumeControlStream()).isEqualTo(AudioManager.STREAM_MUSIC);
    }

    @Test
    public void shouldStopPlaying() throws Exception {
        init();
        shadowTextToSpeech.getOnInitListener().onInit(TextToSpeech.SUCCESS);
        speakerbox.play("Hello");
        speakerbox.stop();
        assertThat(shadowTextToSpeech.isStopped()).isTrue();
    }

    @Test
    public void checkIfTextIgnored() throws Exception {
        init();
        shadowTextToSpeech.getOnInitListener().onInit(TextToSpeech.SUCCESS);
        speakerbox.dontPlayIfContains("Continue on  for");
        speakerbox.play("Yo");
        speakerbox.play("Continue on  for 12 feet");
        assertThat(shadowTextToSpeech.getLastSpokenText()).isEqualTo("Yo");
    }

    @Test
    public void shouldUseDefaultQueueMode() throws Exception {
        init();
        shadowTextToSpeech.getOnInitListener().onInit(TextToSpeech.SUCCESS);
        speakerbox.play("Yo");
        assertThat(shadowTextToSpeech.getQueueMode()).isEqualTo(TextToSpeech.QUEUE_FLUSH);
    }

    @Test
    public void shouldUpdateQueueMode() throws Exception {
        init();
        shadowTextToSpeech.getOnInitListener().onInit(TextToSpeech.SUCCESS);
        speakerbox.setQueueMode(TextToSpeech.QUEUE_ADD);
        speakerbox.play("Yo");
        assertThat(shadowTextToSpeech.getQueueMode()).isEqualTo(TextToSpeech.QUEUE_ADD);
    }

    @Test @Config(sdk = 21)
    public void playAndOnStart_shouldNotifyOnStart_Api21() throws Exception {
        final OnStartListener onStartListener = new OnStartListener();
        speakerbox.setQueueMode(TextToSpeech.QUEUE_ADD);
        speakerbox.playAndOnStart("Yo", new Runnable() {
            @Override public void run() {
                onStartListener.onStart = true;
            }
        });
        assertThat(onStartListener.onStart).isTrue();
    }

    @Test @Config(sdk = 21)
    public void playAndOnDone_shouldNotifyOnDone_Api21() throws Exception {
        final OnDoneListener onDoneListener = new OnDoneListener();
        shadowTextToSpeech.setFinishOnSpeak(true);
        speakerbox.setQueueMode(TextToSpeech.QUEUE_ADD);
        speakerbox.playAndOnDone("Yo", new Runnable() {
            @Override public void run() {
                onDoneListener.onDone = true;
            }
        });
        assertThat(onDoneListener.onDone).isTrue();
    }

    @Test @Config(sdk = 21)
    public void playAndOnError_shouldNotifyOnError_Api21() throws Exception {
        final OnErrorListener onErrorListener = new OnErrorListener();
        shadowTextToSpeech.setErrorOnSpeak(true);
        speakerbox.setQueueMode(TextToSpeech.QUEUE_ADD);
        speakerbox.playAndOnError("Yo", new Runnable() {
            @Override public void run() {
                onErrorListener.onError = true;
            }
        });
        assertThat(onErrorListener.onError).isTrue();
    }

    @Test @Config(sdk = 19)
    public void playAndOnStart_shouldNotifyOnStart_Api19() throws Exception {
        final OnStartListener onStartListener = new OnStartListener();
        speakerbox.setQueueMode(TextToSpeech.QUEUE_ADD);
        speakerbox.playAndOnStart("Yo", new Runnable() {
            @Override public void run() {
                onStartListener.onStart = true;
            }
        });
        assertThat(onStartListener.onStart).isTrue();
    }

    @Test @Config(sdk = 19)
    public void playAndOnDone_shouldNotifyOnDone_Api19() throws Exception {
        final OnDoneListener onDoneListener = new OnDoneListener();
        shadowTextToSpeech.setFinishOnSpeak(true);
        speakerbox.setQueueMode(TextToSpeech.QUEUE_ADD);
        speakerbox.playAndOnDone("Yo", new Runnable() {
            @Override public void run() {
                onDoneListener.onDone = true;
            }
        });
        assertThat(onDoneListener.onDone).isTrue();
    }

    @Test @Config(sdk = 19)
    public void playAndOnError_shouldNotifyOnError_Api19() throws Exception {
        final OnErrorListener onErrorListener = new OnErrorListener();
        shadowTextToSpeech.setErrorOnSpeak(true);
        speakerbox.setQueueMode(TextToSpeech.QUEUE_ADD);
        speakerbox.playAndOnError("Yo", new Runnable() {
            @Override public void run() {
                onErrorListener.onError = true;
            }
        });
        assertThat(onErrorListener.onError).isTrue();
    }

    @Test @Config(sdk = 21)
    public void getAvailableLanguages_shouldReturnTtsLanguages() throws Exception {
        Set<Locale> languages = speakerbox.getAvailableLanguages();
        assertThat(languages).isEqualTo(shadowTextToSpeech.getAvailableLanguages());
    }

    @Test
    public void setLanguage_shouldSetTtsLangugae() throws Exception {
        speakerbox.setLanguage(Locale.JAPANESE);
        assertThat(shadowTextToSpeech.getLanguage()).isEqualTo(Locale.JAPANESE);
    }

    class OnStartListener {
        boolean onStart = false;
    }

    class OnDoneListener {
        boolean onDone = false;
    }

    class OnErrorListener {
        boolean onError = false;
    }
}
