package at.fhooe.mc.android.tetris;

/**
 * Created by Platti on 19.05.2015.
 */
public class MyBluetoothDevice {
    private String name;
    private String address;
    private boolean paired;

    public MyBluetoothDevice(String name, String address, boolean paired) {
        this.name = name;
        this.address = address;
        this.paired = paired;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public boolean isPaired() {
        return paired;
    }
}
