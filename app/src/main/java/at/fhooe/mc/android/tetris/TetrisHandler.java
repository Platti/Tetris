package at.fhooe.mc.android.tetris;

import android.app.AlertDialog;
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
    private int running = 0;
    private Context context;
    ArrayList<Integer> nextTetrominos;

    public TetrisHandler(Context context, ArrayList<Integer> nextTetrominos) {
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

                if (context instanceof MultiplayerActivity) {
                    if (data.tetromino != -1) {
                        nextTetrominos.add(data.tetromino);
                        ((MultiplayerActivity) (context)).mService.write(new TetrisProtocol(running, TetrisProtocol.ACK));
                        Log.i(TAG, "received tetromino ID: " + data.tetromino);
                        if (running == 4) {
                            ((MultiplayerActivity) (context)).startGameClient();
                            running++;
                        } else {
                            running++;
                        }
                    }

                    if (data.tetrominoRequest) {
                        ((MultiplayerActivity) (context)).fillTetrominoArray(true);
                    }

                    if (data.acknowledgement != -1) {
                        ((MultiplayerActivity) (context)).fillTetrominoArray(false);
                        if (data.acknowledgement == 4) {
                            ((MultiplayerActivity) (context)).nextTetromino();
                            ((MultiplayerActivity) (context)).newTetromino();
                        }
                    }

                    if (data.gameOver) {
                        ((MultiplayerActivity) (context)).opponentGameOver = data.gameOver;
                        ((MultiplayerActivity) (context)).opponentScore = data.score;

                        if (((MultiplayerActivity) (context)).myGameOver) {
                            ((MultiplayerActivity) (context)).showRevengeDialog();
                        }
                    }

                    if (data.revenge != 0) {
                        if (data.revenge == 1) {
                            if (((MultiplayerActivity) (context)).dialog != null && ((RevengeDialog) ((MultiplayerActivity) (context)).dialog).dialog != null) {
                                ((RevengeDialog) ((MultiplayerActivity) (context)).dialog).dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            } else {
                                ((MultiplayerActivity) (context)).finish();
                            }
                        }
                    }

                    if (data.ready == 1) {
                        ((MultiplayerActivity) (context)).opponentReady = true;
                    } else if (data.ready == 2 && !((MultiplayerActivity) (context)).myGameOver) {
                        ((MultiplayerActivity) (context)).mService.write(new TetrisProtocol(1, TetrisProtocol.READY));
                    }
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
