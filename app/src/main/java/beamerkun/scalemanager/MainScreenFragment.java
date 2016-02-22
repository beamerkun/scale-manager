package beamerkun.scalemanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Mess of a fragment, work heavily in progress
 */
public class MainScreenFragment extends Fragment {

    public MainScreenFragment() {
        m_btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_screen, container, false);

        final Button bt_on_button = (Button) v.findViewById(R.id.bt_on_button);
        final Button bt_scan_button = (Button) v.findViewById(R.id.bt_scan_button);

        bt_on_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
            }
        });
        bt_on_button.setEnabled(!m_btAdapter.isEnabled());

        bt_scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_arrayAdapter.clear();
                m_btAdapter.startDiscovery();
            }
        });
        bt_scan_button.setEnabled(m_btAdapter.isEnabled());

        IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterStateChanged = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(m_btListReceiver, filterFound);
        getActivity().registerReceiver(m_btStateReceiver, filterStateChanged);

        m_arrayAdapter = new ArrayAdapter<String>(v.getContext(),
                android.R.layout.simple_list_item_1,
                new ArrayList<String>());

        ListView lv = (ListView) v.findViewById(R.id.listView);
        lv.setAdapter(m_arrayAdapter);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(m_btListReceiver);
        getActivity().unregisterReceiver(m_btStateReceiver);
    }

    static final private String c_btScaleDeviceName = "VScale";

    private BluetoothAdapter m_btAdapter;
    private ArrayAdapter<String> m_arrayAdapter;
    private BluetoothDevice m_btScaleDevice = null;

    private final BroadcastReceiver m_btListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                m_arrayAdapter.add(device.getName() + " , " + device.getAddress());
                if (device.getName().equals(c_btScaleDeviceName)) {
                    m_btScaleDevice = device;
                }
            }
        }
    };

    private final BroadcastReceiver m_btStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        getView().findViewById(R.id.bt_on_button).setEnabled(false);
                        getView().findViewById(R.id.bt_scan_button).setEnabled(true);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        getView().findViewById(R.id.bt_on_button).setEnabled(true);
                        getView().findViewById(R.id.bt_scan_button).setEnabled(false);
                        break;
                }
            }
        }
    };
}
