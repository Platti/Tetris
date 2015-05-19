package at.fhooe.mc.android.tetris;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Platti on 19.05.2015.
 */
public class DeviceArrayAdapter extends ArrayAdapter<MyBluetoothDevice> {

    public DeviceArrayAdapter(Context _context) {
        super(_context, -1);
    }

    @Override
    public View getView(int _pos, View _view, ViewGroup _parent) {

        if (_view == null) {
            Context c = getContext();
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _view = inflater.inflate(R.layout.bluetooth_device_layout, null);
        }

        MyBluetoothDevice device = getItem(_pos);

        TextView tv = (TextView) _view.findViewById(R.id.device_name);
        tv.setText(device.getName());

        ImageView iv = (ImageView) _view.findViewById(R.id.device_paired);
        if (device.isPaired()) {
            iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.paired));
        } else {
            iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.not_paired));
        }

        return _view;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.getCount(); i++) {
            if (!this.getItem(i).isPaired()) {
                this.remove(this.getItem(i));
            }
        }
    }

    @Override
    public void add(MyBluetoothDevice object) {
        for (int i = 0; i < this.getCount(); i++) {
            if (this.getItem(i).getAddress().equals(object.getAddress())) {
                return;
            }
        }
        super.add(object);
    }
}
