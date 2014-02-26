package com.mapzen.speakerbox;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.bytecode.ShadowMap;

public class SpeakerboxTestRunner extends RobolectricTestRunner {
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
