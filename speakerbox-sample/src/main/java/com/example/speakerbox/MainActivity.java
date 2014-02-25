package com.example.speakerbox;

import com.mapzen.speakerbox.Speakerbox;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
            final Speakerbox speakerbox = new Speakerbox(getActivity());
            final TextView textView = (EditText) view.findViewById(R.id.text);
            final Button button = (Button) view.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speakerbox.play(textView.getText().toString());
                }
            });
            return view;
        }
    }
}
