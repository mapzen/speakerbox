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
package com.example.speakerbox;

import com.mapzen.speakerbox.Speakerbox;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SpeakerboxFragment())
                    .commit();
        }
    }

    public static class SpeakerboxFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_main, container, false);
            final TextView textView = (EditText) view.findViewById(R.id.text);
            final Speakerbox speakerbox = new Speakerbox(getActivity().getApplication());
            speakerbox.setActivity(getActivity());

            // Test calling play() immediately (before TTS initialization is complete).
            speakerbox.play(textView.getText());

            final Button speakButton = (Button) view.findViewById(R.id.speak);
            speakButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speakerbox.play(textView.getText());
                }
            });

            final Button stopButton = (Button) view.findViewById(R.id.stop);
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speakerbox.stop();
                }
            });

            final ToggleButton muteButton = (ToggleButton) view.findViewById(R.id.mute);
            muteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        speakerbox.mute();
                    } else {
                        speakerbox.unmute();
                    }
                }
            });

            final RadioButton add = (RadioButton) view.findViewById(R.id.queue_mode_add);
            add.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        speakerbox.setQueueMode(TextToSpeech.QUEUE_ADD);
                    }
                }
            });

            final RadioButton flush = (RadioButton) view.findViewById(R.id.queue_mode_flush);
            flush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        speakerbox.setQueueMode(TextToSpeech.QUEUE_FLUSH);
                    }
                }
            });

            final Button requestButton = (Button) view.findViewById(R.id.request_focus_btn);
            requestButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    speakerbox.requestAudioFocus();
                }
            });

            final Button abandonButton = (Button) view.findViewById(R.id.abandon_focus_btn);
            abandonButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    speakerbox.abandonAudioFocus();
                }
            });

            return view;
        }
    }
}
