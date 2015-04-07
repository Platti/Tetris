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

//        adapter.add(new ColorTheme("Nespresso",
//                new int[]{
//                        Color.argb(255, 0, 0, 0),
//                        Color.argb(255, 78, 22, 5),
//                        Color.argb(255, 72, 21, 100),
//                        Color.argb(255, 25, 71, 25),
//                        Color.argb(255, 169, 136, 59),
//                        Color.argb(255, 151, 3, 16),
//                        Color.argb(255, 16, 115, 111)}));
//
//        adapter.add(new ColorTheme("Erdtöne",
//                new int[]{
//                        Color.argb(255, 223, 231, 182),
//                        Color.argb(255, 252, 180, 35),
//                        Color.argb(255, 212, 52, 19),
//                        Color.argb(255, 144, 168, 24),
//                        Color.argb(255, 168, 120, 24),
//                        Color.argb(255, 70, 82, 11),
//                        Color.argb(255, 82, 23, 11)}));

        adapter.add(new ColorTheme(getString(R.string.zipferColors),
                new int[]{
                        Color.argb(255, 38, 45, 115),
                        Color.argb(255, 216, 163, 21),
                        Color.argb(255, 243, 234, 97),
                        Color.argb(255, 252, 252, 252),
                        Color.argb(255, 187, 107, 46),
                        Color.argb(255, 155, 159, 170),
                        Color.argb(255, 38, 15, 1)}));

//        adapter.add(new ColorTheme("McDonalds",
//                new int[]{
//                        Color.argb(255, 248, 247, 0),
//                        Color.argb(255, 176, 24, 0),
//                        Color.argb(255, 255, 255, 255),
//                        Color.argb(255, 1, 70, 16),
//                        Color.argb(255, 34, 34, 104),
//                        Color.argb(255, 121, 4, 118),
//                        Color.argb(255, 73, 41, 28)}));
//
//        adapter.add(new ColorTheme("Reggae",
//                new int[]{
//                        Color.argb(255, 177, 1, 1),
//                        Color.argb(255, 253, 225, 19),
//                        Color.argb(255, 51, 208, 51),
//                        Color.argb(255, 1, 1, 1),
//                        Color.argb(255, 255, 96, 2),
//                        Color.argb(255, 104, 21, 17),
//                        Color.argb(255, 6, 95, 23)}));

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

//        mP = MediaPlayer.create(OptionsActivity.this, R.raw.menusoundtrack);
//        mP.setLooping(true);
//        mP.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mP.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mP.stop();
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
