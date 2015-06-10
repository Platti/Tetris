package at.fhooe.mc.android.tetris;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Platti on 08.06.2015.
 */
public class RevengeDialog extends DialogFragment {

    private int myScore, opponentScore;
    public AlertDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        myScore = getArguments().getInt("myScore");
        opponentScore = getArguments().getInt("opponentScore");

        if (opponentScore == -1) {
            builder = builder.setMessage("Connection lost!\nYour Score: " + myScore);
        } else if (myScore > opponentScore) {
            builder = builder.setMessage("You won!\nYour Score: " + myScore + "\nOpponent Score: " + opponentScore);
        } else if (myScore < opponentScore) {
            builder = builder.setMessage("You lost!\nYour Score: " + myScore + "\nOpponent Score: " + opponentScore);
        } else {
            builder = builder.setMessage("Draw!\nYour Score: " + myScore + "\nOpponent Score: " + opponentScore);
        }

        builder = builder.setPositiveButton("Revenge", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(getActivity(), MultiplayerActivity.class);
                i.putExtra("server", getArguments().getBoolean("server"));
                getActivity().startActivity(i);
                getActivity().finish();
                ((MultiplayerActivity) getActivity()).mService.write(new TetrisProtocol("Opponent wants Revenge!", 2));
            }
        });
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                ((MultiplayerActivity) getActivity()).mService.write(new TetrisProtocol("No Revenge!", 1));
                ((MultiplayerActivity) getActivity()).mService.stop();
            }
        });

        dialog = builder.create();

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (opponentScore == -1) {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        }
    }
}
