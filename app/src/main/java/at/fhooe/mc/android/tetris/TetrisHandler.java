package at.fhooe.mc.android.tetris;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Platti on 02.06.2015.
 */
public class TetrisHandler extends Handler {
    private static final String TAG = "Tetris Log-Tag";
    private Context context;

    public TetrisHandler(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.MESSAGE_READ: {
                Log.i(TAG, "Reading message");
                String data = new String((byte[]) msg.obj);
                Toast.makeText(context, data, Toast.LENGTH_LONG).show();
            }
            break;
            case Constants.MESSAGE_TOAST: {
                Bundle data = msg.getData();
                Toast.makeText(context, data.getString(Constants.MESSAGE_KEY_TOAST), Toast.LENGTH_LONG).show();
            }
            break;
        }
    }
}
