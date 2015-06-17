package at.fhooe.mc.android.tetris;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Dialog which appears after a Multiplayer-Game (with bluetooth connection)
 * Decide between revenge to start a new Multiplayer-Game
 * or go back to menu and stop game and connection
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
            builder = builder.setMessage(getString(R.string.connection_lost) + "\n" + getString(R.string.your_score) + " " + myScore);
        } else if (myScore > opponentScore) {
            builder = builder.setMessage(getString(R.string.you_won) + "\n" + getString(R.string.your_score) + " " + myScore + "\n" + getString(R.string.opponent_score) + " " + opponentScore);
        } else if (myScore < opponentScore) {
            builder = builder.setMessage(getString(R.string.you_lost) + "\n" + getString(R.string.your_score) + " " + myScore + "\n" + getString(R.string.opponent_score) + " " + opponentScore);
        } else {
            builder = builder.setMessage(getString(R.string.draw) + "\n" + getString(R.string.your_score) + " " + myScore + "\n" + getString(R.string.opponent_score) + " " + opponentScore);
        }

        builder = builder.setPositiveButton(getString(R.string.revenge), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(getActivity(), MultiplayerActivity.class);
                i.putExtra("server", getArguments().getBoolean("server"));
                getActivity().startActivity(i);
                getActivity().finish();
                ((MultiplayerActivity) getActivity()).mService.write(new TetrisProtocol(getString(R.string.opponent_wants_revenge), 2));
            }
        });
        builder.setNegativeButton(getString(R.string.back), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                ((MultiplayerActivity) getActivity()).mService.write(new TetrisProtocol(getString(R.string.no_revenge), 1));
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
