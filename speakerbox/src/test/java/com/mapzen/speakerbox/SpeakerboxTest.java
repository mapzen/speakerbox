package com.mapzen.speakerbox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import android.app.Activity;

import java.util.ArrayList;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.reflect.core.Reflection.field;
import static org.robolectric.Robolectric.application;

@RunWith(SpeakerboxTestRunner.class)
public class SpeakerboxTest {
    private Activity activity;
    private Speakerbox speakerbox;
    private ShadowTextToSpeech shadowTextToSpeech;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(Activity.class).create().start().resume().get();
        speakerbox = new Speakerbox(activity);
        shadowTextToSpeech = Robolectric.shadowOf_(speakerbox.textToSpeech);
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertThat(speakerbox).isNotNull();
    }

    @Test
    public void shouldInitTextToSpeech() throws Exception {
        assertThat(shadowTextToSpeech.getContext()).isEqualTo(activity);
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
        speakerbox.callbacks.onActivityDestroyed(activity);
        assertThat(shadowTextToSpeech.isShutdown()).isTrue();
    }

    @Test
    public void shouldNotShutdownTextToSpeechOnAnotherActivityDestroyed() throws Exception {
        speakerbox.callbacks.onActivityDestroyed(new Activity());
        assertThat(shadowTextToSpeech.isShutdown()).isFalse();
    }

    @Test
    public void shouldUnregisterLifecycleCallbacksOnActivityDestroyed() throws Exception {
        speakerbox.callbacks.onActivityDestroyed(activity);
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
}
