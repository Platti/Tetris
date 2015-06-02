package at.fhooe.mc.android.tetris;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by Platti on 02.06.2015.
 */
public class TetrisHandler extends Handler {
    private static final String TAG = "Tetris Log-Tag";
    private Context context;
    ArrayList<Integer> nextTetrominos;

    public TetrisHandler(Context context, ArrayList<Integer> nextTetrominos ) {
        super();
        this.context = context;
        this.nextTetrominos = nextTetrominos;
    }

    public TetrisHandler(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.MESSAGE_READ: {
                Log.i(TAG, "Reading message");

                TetrisProtocol data = new TetrisProtocol("Error 404");

                ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) msg.obj);
                ObjectInput in = null;
                try {
                    in = new ObjectInputStream(bis);
                    data = (TetrisProtocol) in.readObject();

                } catch (IOException ex) {
                } catch (ClassNotFoundException ex) {
                } finally {
                    try {
                        bis.close();
                    } catch (IOException ex) {
                        // ignore close exception
                    }
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        // ignore close exception
                    }
                }

                if (data.toast != null) {
                    Toast.makeText(context, data.toast, Toast.LENGTH_LONG).show();
                }

                if (data.tetromino != -1){
                    nextTetrominos.add(data.tetromino);
                }
            }
            break;
            case Constants.MESSAGE_TOAST: {
                Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_LONG).show();
            }
            break;
        }
    }
}
