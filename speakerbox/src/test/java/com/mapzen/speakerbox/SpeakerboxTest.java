package com.mapzen.speakerbox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import android.app.Activity;

import static org.fest.assertions.api.Assertions.assertThat;

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
    public void shouldSpeakText() throws Exception {
        speakerbox.play("Hello");
        assertThat(shadowTextToSpeech.getLastSpokenText()).isEqualTo("Hello");
    }
}
