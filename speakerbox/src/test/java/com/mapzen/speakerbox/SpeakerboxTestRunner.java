package com.mapzen.speakerbox;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.bytecode.ShadowMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpeakerboxTestRunner extends RobolectricTestRunner {
    private static final List<String> CUSTOM_SHADOW_TARGETS =
            Collections.unmodifiableList(Arrays.asList(
                    "com.mapzen.speakerbox.ShadowTextToSpeech"
            ));

    public SpeakerboxTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected ShadowMap createShadowMap() {
        return super.createShadowMap()
                .newBuilder()
                .addShadowClass(ShadowTextToSpeech.class)
                .build();
    }
}
