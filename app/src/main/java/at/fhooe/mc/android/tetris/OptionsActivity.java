package at.fhooe.mc.android.tetris;

import android.app.Activity;
import android.graphics.Color;
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
        adapter.add(new ColorTheme("Classic",
                new int[]{
                        Color.RED,
                        Color.GREEN,
                        Color.YELLOW,
                        Color.BLUE,
                        Color.CYAN,
                        Color.MAGENTA,
                        Color.GRAY}));

        adapter.add(new ColorTheme("Black 'n' White",
                new int[]{
                        Color.argb(255, 255, 255, 255),
                        Color.argb(255, 230, 230, 230),
                        Color.argb(255, 170, 170, 170),
                        Color.argb(255, 130, 130, 130),
                        Color.argb(255, 90, 90, 90),
                        Color.argb(255, 50, 50, 50),
                        Color.argb(255, 0, 0, 0)}));

        adapter.add(new ColorTheme("FC Barcelona",
                new int[]{
                        Color.argb(255, 0, 75, 149),
                        Color.argb(255, 164, 35, 75),
                        Color.argb(255, 226, 185, 0),
                        Color.argb(255, 219, 47, 54),
                        Color.argb(255, 255, 233, 0),
                        Color.argb(255, 255, 255, 255),
                        Color.argb(255, 0, 0, 0)}));

        adapter.add(new ColorTheme("Caro's favorites",
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ColorTheme colorTheme = (ColorTheme) parent.getAdapter().getItem(position);
        color.chooseNewColors(colorTheme.getColors());

        Toast.makeText(this, "New theme: " + colorTheme.getName(), Toast.LENGTH_SHORT).show();

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
