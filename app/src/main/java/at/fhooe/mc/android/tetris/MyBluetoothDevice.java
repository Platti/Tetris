package at.fhooe.mc.android.tetris;

/**
 * Class to store the name and address of an bluetooth device and if it's already paired or not
 */
public class MyBluetoothDevice {
    private String name;
    private String address;
    private boolean paired;

    /**
     * Constructor to initialize new instance
     * @param name of the bluetooth device
     * @param address of the bluetooth device, with which it is able to connect
     * @param paired to see, if the bluetooth device is already paired with own
     */
    public MyBluetoothDevice(String name, String address, boolean paired) {
        this.name = name;
        this.address = address;
        this.paired = paired;
    }

    /**
     * @return returns the name of bluetooth device as String
     */
    public String getName() {
        return name;
    }

    /**
     * @return returns address of bluetooth device as String
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return returns if already paired or not (boolean)
     */
    public boolean isPaired() {
        return paired;
    }
}
