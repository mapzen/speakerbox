package com.example.speakerbox;

import com.mapzen.speakerbox.Speakerbox;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
            final Speakerbox speakerbox = new Speakerbox(getActivity());

            // Test calling play() immediately (before TTS initialization is complete).
            speakerbox.play(textView.getText());

            final Button speakButton = (Button) view.findViewById(R.id.speak);
            speakButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speakerbox.play(textView.getText());
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

            return view;
        }
    }
}
