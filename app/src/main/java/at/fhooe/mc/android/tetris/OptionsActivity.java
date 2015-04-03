package at.fhooe.mc.android.tetris;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import at.fhooe.mc.android.tetris.R;

public class OptionsActivity extends Activity implements AdapterView.OnItemClickListener {

    TetrisColor color;
    MediaPlayer mP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_activity);

        color = new TetrisColor(this);

        ImageView iv = null;
        iv = (ImageView) findViewById(R.id.imageView1);
        iv.setBackgroundColor(color.o);
        iv = (ImageView) findViewById(R.id.imageView2);
        iv.setBackgroundColor(color.i);
        iv = (ImageView) findViewById(R.id.imageView3);
        iv.setBackgroundColor(color.l);
        iv = (ImageView) findViewById(R.id.imageView4);
        iv.setBackgroundColor(color.j);
        iv = (ImageView) findViewById(R.id.imageView5);
        iv.setBackgroundColor(color.s);
        iv = (ImageView) findViewById(R.id.imageView6);
        iv.setBackgroundColor(color.z);
        iv = (ImageView) findViewById(R.id.imageView7);
        iv.setBackgroundColor(color.t);

        ListView listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<ColorTheme> adapter = new MyArrayAdapter(this);
        adapter.add(new ColorTheme(getString(R.string.classicColor),
                new int[]{
                        Color.RED,
                        Color.GREEN,
                        Color.YELLOW,
                        Color.BLUE,
                        Color.CYAN,
                        Color.MAGENTA,
                        Color.GRAY}));

        adapter.add(new ColorTheme(getString(R.string.greyTonesColor),
                new int[]{
                        Color.argb(255, 255, 255, 255),
                        Color.argb(255, 230, 230, 230),
                        Color.argb(255, 170, 170, 170),
                        Color.argb(255, 130, 130, 130),
                        Color.argb(255, 90, 90, 90),
                        Color.argb(255, 50, 50, 50),
                        Color.argb(255, 0, 0, 0)}));

        adapter.add(new ColorTheme(getString(R.string.pastelColors),
                new int[]{
                        Color.argb(255, 106, 203, 222),
                        Color.argb(255, 246, 156, 155),
                        Color.argb(255, 255, 247, 129),
                        Color.argb(255, 124, 139, 198),
                        Color.argb(255, 249, 180, 138),
                        Color.argb(255, 182, 216, 132),
                        Color.argb(255, 191, 128, 183)}));

        adapter.add(new ColorTheme(getString(R.string.fcbarcelonaColors),
                new int[]{
                        Color.argb(255, 0, 75, 149),
                        Color.argb(255, 164, 35, 75),
                        Color.argb(255, 226, 185, 0),
                        Color.argb(255, 219, 47, 54),
                        Color.argb(255, 255, 233, 0),
                        Color.argb(255, 255, 255, 255),
                        Color.argb(255, 0, 0, 0)}));

        adapter.add(new ColorTheme(getString(R.string.milkaColors),
                new int[]{
                        Color.argb(255, 104, 79, 163),
                        Color.argb(255, 255, 255, 255),
                        Color.argb(255, 116, 38, 18),
                        Color.argb(255, 215, 195, 145),
                        Color.argb(255, 2, 183, 240),
                        Color.argb(255, 71, 13, 2),
                        Color.argb(255, 40, 27, 115)}));

        adapter.add(new ColorTheme(getString(R.string.carosFavColors),
                new int[]{
                        getResources().getColor(R.color.red),
                        getResources().getColor(R.color.yellow),
                        getResources().getColor(R.color.purple),
                        getResources().getColor(R.color.blue),
                        getResources().getColor(R.color.green),
                        getResources().getColor(R.color.turquoise),
                        getResources().getColor(R.color.orange)}));

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        mP = MediaPlayer.create(OptionsActivity.this, R.raw.menusoundtrack);
        mP.setLooping(true);
        mP.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mP.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mP.stop();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ColorTheme colorTheme = (ColorTheme) parent.getAdapter().getItem(position);
        color.chooseNewColors(colorTheme.getColors());

        Toast.makeText(this, getString(R.string.newTheme) + colorTheme.getName(), Toast.LENGTH_SHORT).show();

        // Update Current Theme
        ImageView iv = null;
        iv = (ImageView) findViewById(R.id.imageView1);
        iv.setBackgroundColor(color.o);
        iv = (ImageView) findViewById(R.id.imageView2);
        iv.setBackgroundColor(color.i);
        iv = (ImageView) findViewById(R.id.imageView3);
        iv.setBackgroundColor(color.l);
        iv = (ImageView) findViewById(R.id.imageView4);
        iv.setBackgroundColor(color.j);
        iv = (ImageView) findViewById(R.id.imageView5);
        iv.setBackgroundColor(color.s);
        iv = (ImageView) findViewById(R.id.imageView6);
        iv.setBackgroundColor(color.z);
        iv = (ImageView) findViewById(R.id.imageView7);
        iv.setBackgroundColor(color.t);
    }
}
