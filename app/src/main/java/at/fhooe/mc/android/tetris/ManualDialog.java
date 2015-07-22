package at.fhooe.mc.android.tetris;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by Platti on 18.07.2015.
 */
public class ManualDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final TextView headlineGeneral = new TextView(getActivity());
        headlineGeneral.setText(getString(R.string.general));
        headlineGeneral.setPadding(0,20,0,0);
        headlineGeneral.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        linearLayout.addView(headlineGeneral);

        final Button btnGeneral = new Button(getActivity());
        btnGeneral.setText(getString(R.string.general_text));
        btnGeneral.setBackgroundColor(Color.TRANSPARENT);
        btnGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://tetris.wikia.com/wiki/Tetris_Wiki"));
                startActivity(intent);
            }
        });
        linearLayout.addView(btnGeneral);

        final TextView headlineHandling = new TextView(getActivity());
        headlineHandling.setText(getString(R.string.handling));
        headlineHandling.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        linearLayout.addView(headlineHandling);

        final ImageView imageHandling = new ImageView(getActivity());
        imageHandling.setImageDrawable(getResources().getDrawable(R.drawable.handling2));
        linearLayout.addView(imageHandling);

        final TextView headlineAbout = new TextView(getActivity());
        headlineAbout.setText(getString(R.string.about));
        headlineAbout.setPadding(0,0,0,5);
        headlineAbout.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        linearLayout.addView(headlineAbout);

        final TextView textAbout = new TextView(getActivity());
        textAbout.setText(getString(R.string.about_text));
        textAbout.setPadding(0,0,0,20);
        linearLayout.addView(textAbout);

        final ScrollView scrollView = new ScrollView(getActivity());
        scrollView.addView(linearLayout);

        builder.setView(scrollView);

        builder.setPositiveButton(getString(R.string.back), null);

        return builder.create();
    }


}
