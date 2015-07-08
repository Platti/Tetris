package at.fhooe.mc.android.tetris;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

/**
 * Created by Platti on 08.07.2015.
 */
public class HighscoreListAdapter extends ArrayAdapter<ParseObject> {

    public HighscoreListAdapter(Context _context) {
        super(_context, -1);
    }

    @Override
    public View getView(int _pos, View _view, ViewGroup _parent) {

        if (_view == null) {
            Context c = getContext();
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _view = inflater.inflate(R.layout.highscore_list_layout, null);
        }

        ParseObject entry = getItem(_pos);

        TextView tv = null;
        tv = (TextView) _view.findViewById(R.id.country);
        tv.setText(entry.getString("country"));
        tv = (TextView) _view.findViewById(R.id.nickname);
        tv.setText(entry.getString("name"));
        tv = (TextView) _view.findViewById(R.id.score);
        tv.setText(String.valueOf(entry.getInt("score")));

        return _view;
    }
}
