package at.fhooe.mc.android.tetris;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.countrypicker.CountryPicker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Platti on 08.07.2015.
 */
public class nicknameDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.choose_a_nickname));

        final SharedPreferences sp = getActivity().getSharedPreferences(MainActivity.PREF_NAME, getActivity().MODE_PRIVATE);

        final LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputNickname = new EditText(getActivity());
        inputNickname.setInputType(InputType.TYPE_CLASS_TEXT);
        inputNickname.setHint("Nickname");
        inputNickname.setFilters(new InputFilter[] {new InputFilter.LengthFilter(15)});

        linearLayout.addView(inputNickname);

        builder.setView(linearLayout);


        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!inputNickname.getText().toString().equals("unknown") && !inputNickname.getText().toString().equals("")) {

                    final Context context = getActivity();
                    final FragmentManager fragMgr = getFragmentManager();
                    final String name = inputNickname.getText().toString();

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("TetrisUser");
                    query.whereEqualTo("name", inputNickname.getText().toString());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> users, ParseException e) {
                            if (e == null && users.size() == 0) {
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putString("name", inputNickname.getText().toString());
                                edit.commit();

                                ParseObject userObject = new ParseObject("TetrisUser");
                                userObject.put("name", name);
                                userObject.saveInBackground();

                                CountryPicker picker = CountryPicker.newInstance(context.getString(R.string.select_your_country));
                                picker.setListener((StartMenu) context);
                                picker.show(fragMgr, "color_picker");
                            } else if (e == null) {
                                Toast.makeText(context, context.getString(R.string.nickname_taken), Toast.LENGTH_LONG).show();
                                DialogFragment nameDialog = new nicknameDialog();
                                nameDialog.show(fragMgr, "name_dialog");
                            } else {
                                Log.d("Check Nickname Taken", "Error: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    DialogFragment nameDialog = new nicknameDialog();
                    nameDialog.show(getFragmentManager(), "name_dialog");
                }



            }
        });

        return builder.create();
    }
}
