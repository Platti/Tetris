package at.fhooe.mc.android.tetris;

import java.util.UUID;

/**
 * Created by Platti on 02.06.2015.
 */
public class Constants {
    public static final int REQUEST_ENABLE_BT = 1;
    public static final String NAME = "Tetris Bluetooth";
    public static final UUID MY_UUID = UUID.fromString("dc70349f-4084-4bf4-8573-0eb470d93270");

    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_TOAST = 5;
    public static final String MESSAGE_KEY_TOAST = "Message for Toast";

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
}
