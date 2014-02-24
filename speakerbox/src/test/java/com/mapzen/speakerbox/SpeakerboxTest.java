package com.mapzen.speakerbox;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class SpeakerboxTest {
    @Test
    public void shouldNotBeNull() throws Exception {
        Speakerbox speakerbox = new Speakerbox();
        assertThat(speakerbox).isNotNull();
    }
}
