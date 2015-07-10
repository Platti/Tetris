package at.fhooe.mc.android.tetris;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.lang.reflect.Field;
import java.util.Locale;

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

        ImageView iv = (ImageView) _view.findViewById(R.id.country);
        String drawableName = "flag_" + entry.getString("country").toLowerCase(Locale.ENGLISH);
        iv.setImageResource(getResId(drawableName));
        TextView tv = null;
        tv = (TextView) _view.findViewById(R.id.nickname);
        tv.setText(entry.getString("name"));
        tv = (TextView) _view.findViewById(R.id.score);
        tv.setText(String.valueOf(entry.getInt("score")));

        return _view;
    }

    /**
     * The drawable image name has the format "flag_$countryCode". We need to
     * load the drawable dynamically from country code. Code from
     * http://stackoverflow.com/
     * questions/3042961/how-can-i-get-the-resource-id-of
     * -an-image-if-i-know-its-name
     *
     * @param drawableName
     * @return
     */
    private int getResId(String drawableName) {

        try {
            Class<R.drawable> res = R.drawable.class;
            Field field = res.getField(drawableName);
            int drawableId = field.getInt(null);
            return drawableId;
        } catch (Exception e) {
            Log.e("COUNTRYPICKER", "Failure to get drawable id.", e);
        }
        return -1;
    }
}
