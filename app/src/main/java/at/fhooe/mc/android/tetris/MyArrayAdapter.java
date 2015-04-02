package at.fhooe.mc.android.tetris;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Platti on 02.04.2015.
 */
public class MyArrayAdapter extends ArrayAdapter<ColorTheme> {

    public MyArrayAdapter(Context _context) {
        super(_context, -1);
    }

    @Override
    public View getView(int _pos, View _view, ViewGroup _parent) {

        if (_view==null){
            Context c = getContext();
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _view = inflater.inflate(R.layout.color_theme_layout, null);
        }

        ColorTheme colorTheme = getItem(_pos);
        int[] colors = colorTheme.getColors();

        TextView tv = (TextView) _view.findViewById(R.id.textView_name);
        tv.setText(colorTheme.getName());

        ImageView iv = null;
        iv = (ImageView) _view.findViewById(R.id.imageView1);
        iv.setBackgroundColor(colors[0]);
        iv = (ImageView) _view.findViewById(R.id.imageView2);
        iv.setBackgroundColor(colors[1]);
        iv = (ImageView) _view.findViewById(R.id.imageView3);
        iv.setBackgroundColor(colors[2]);
        iv = (ImageView) _view.findViewById(R.id.imageView4);
        iv.setBackgroundColor(colors[3]);
        iv = (ImageView) _view.findViewById(R.id.imageView5);
        iv.setBackgroundColor(colors[4]);
        iv = (ImageView) _view.findViewById(R.id.imageView6);
        iv.setBackgroundColor(colors[5]);
        iv = (ImageView) _view.findViewById(R.id.imageView7);
        iv.setBackgroundColor(colors[6]);

        return _view;
    }
}

