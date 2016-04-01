package com.mapzen.speakerbox.example;

import com.mapzen.speakerbox.Speakerbox;

import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

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

        final TextView speakStatus = (TextView) view.findViewById(R.id.speak_status);

        final Button speakWNotifyButton = (Button) view.findViewById(R.id.speak_w_notify);
        speakWNotifyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Runnable onStart = new Runnable() {
                    public void run() {
                        speakStatus.setText(getText(R.string.start_speaking));
                    }
                };
                Runnable onDone = new Runnable() {
                    public void run() {
                        speakStatus.setText(getText(R.string.done_speaking));
                        clearStatusText(speakStatus);
                    }
                };
                Runnable onError = new Runnable() {
                    public void run() {
                        speakStatus.setText(getText(R.string.error_speaking));
                        clearStatusText(speakStatus);
                    }
                };
                speakerbox.play(textView.getText().toString(), onStart, onDone, null);
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

    private void clearStatusText(final TextView speakStatus) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                speakStatus.setText("");
            }
        }, 1000);
    }
}
