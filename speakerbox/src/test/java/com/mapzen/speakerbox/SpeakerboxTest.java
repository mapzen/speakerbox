package com.mapzen.speakerbox;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.application;

@RunWith(SpeakerboxTestRunner.class)
public class SpeakerboxTest {
    @Test
    public void shouldNotBeNull() throws Exception {
        Speakerbox speakerbox = new Speakerbox(application);
        assertThat(speakerbox).isNotNull();
    }

    @Test
    public void shouldInitTextToSpeech() throws Exception {
        Speakerbox speakerbox = new Speakerbox(application);
        ShadowTextToSpeech shadowTextToSpeech = Robolectric.shadowOf_(speakerbox.textToSpeech);
        assertThat(shadowTextToSpeech.getContext()).isEqualTo(application);
        assertThat(shadowTextToSpeech.getOnInitListener()).isEqualTo(speakerbox);
    }
}
