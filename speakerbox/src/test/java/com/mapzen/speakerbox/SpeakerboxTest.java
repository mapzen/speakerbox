package com.mapzen.speakerbox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.application;

@RunWith(SpeakerboxTestRunner.class)
public class SpeakerboxTest {
    private Speakerbox speakerbox;

    @Before
    public void setUp() throws Exception {
        speakerbox = new Speakerbox(application);
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertThat(speakerbox).isNotNull();
    }

    @Test
    public void shouldInitTextToSpeech() throws Exception {
        ShadowTextToSpeech shadowTextToSpeech = Robolectric.shadowOf_(speakerbox.textToSpeech);
        assertThat(shadowTextToSpeech.getContext()).isEqualTo(application);
        assertThat(shadowTextToSpeech.getOnInitListener()).isEqualTo(speakerbox);
    }

    @Test
    public void shouldSpeakText() throws Exception {
        speakerbox.play("text");
        ShadowTextToSpeech shadowTextToSpeech = Robolectric.shadowOf_(speakerbox.textToSpeech);
        assertThat(shadowTextToSpeech.getLastSpokenText()).isEqualTo("text");
    }
}
