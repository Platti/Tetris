package at.fhooe.mc.android.tetris;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class GlobalHighscores extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_global_highscores, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences(MainActivity.PREF_NAME, getActivity().MODE_PRIVATE);
        TextView tv = (TextView) rootView.findViewById(R.id.your_highscore);
        int yourHighscore = sp.getInt("score0", -1);
        if (yourHighscore >= 0) {
            tv.setText(String.valueOf(yourHighscore));
        }


        ListView listView = (ListView) rootView.findViewById(R.id.listView_global_highscores);
        final ArrayAdapter<ParseObject> adapter = new HighscoreListAdapter(getActivity());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("TetrisHighscore");
        query.orderByDescending("score");
        query.setLimit(100);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + scoreList.size() + " scores");

                    for (int i = 0; i < scoreList.size() && i < 100; i++) {
                        adapter.add(scoreList.get(i));
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

        listView.setAdapter(adapter);

        return rootView;
    }
}
