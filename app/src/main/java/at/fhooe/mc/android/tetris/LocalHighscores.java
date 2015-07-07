package at.fhooe.mc.android.tetris;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LocalHighscores extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_local_highscores, container, false);

        SharedPreferences sp = this.getActivity().getSharedPreferences(MainActivity.PREF_NAME, this.getActivity().MODE_PRIVATE);
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.local_highscore_layout);

        int score;
        boolean highlighted = false;
        for (int i = 0; i < 10; i++) {
            score = sp.getInt("score" + i, -1);
            if (score >= 0) {
                TextView tv = new TextView(this.getActivity());
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(30.0f);
                if (i == 0) {
                    tv.setTextColor(getResources().getColor(R.color.gold));
                } else if (i == 1) {
                    tv.setTextColor(getResources().getColor(R.color.silver));
                } else if (i == 2) {
                    tv.setTextColor(getResources().getColor(R.color.bronze));
                } else {
                    tv.setTextColor(getResources().getColor(R.color.white));
                }
                if(!highlighted && score == sp.getInt("latest score", -1)){
                    tv.setTextColor(getResources().getColor(R.color.cyan));
                    highlighted = true;
                }
                tv.setText(String.valueOf(score));
                layout.addView(tv);
            }
        }

        return rootView;
    }
}