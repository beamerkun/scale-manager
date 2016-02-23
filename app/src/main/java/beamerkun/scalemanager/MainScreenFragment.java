package beamerkun.scalemanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Mess of a fragment, work heavily in progress
 */
public class MainScreenFragment extends Fragment {
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
                if(m_btScaleDevice == null)
                    btLeScan();
                else
                    m_btGatt.discoverServices();
            }
        });
        bt_scan_button.setEnabled(m_btAdapter.isEnabled());

        IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterStateChanged = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(m_btStateReceiver, filterStateChanged);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(m_btStateReceiver);
    }

    private void btLeScan() {
        m_btHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                m_btAdapter.stopLeScan(m_btLeScanCallback);
            }
        }, c_scanPeriod);
        m_btAdapter.startLeScan(m_btLeScanCallback);
    }

    private void onScaleFound(BluetoothDevice scale) {
        m_btScaleDevice = scale;

        m_btGatt = m_btScaleDevice.connectGatt(getActivity().getApplicationContext(), true, m_btGattCallback);
    }

    private static final long c_scanPeriod = 10000;
    private static final String c_scaleDeviceName = "VScale";

    private Handler m_btHandler = new Handler();

    private BluetoothAdapter m_btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice m_btScaleDevice = null;
    private BluetoothGatt m_btGatt = null;

    private BluetoothGattCallback m_btGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if(newState == BluetoothProfile.STATE_CONNECTED) {
                System.out.println("Gatt connected");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                System.out.println("Gatt disconnected");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                for(BluetoothGattService service : gatt.getServices()) {
                    if(service.getType() == BluetoothGattService.SERVICE_TYPE_SECONDARY)
                        continue;
                    // filter out standard services
                    if(service.getUuid().toString().endsWith("00805f9b34fb"))
                        continue;
                    System.out.printf("service: %s%n", service.getUuid());
                    for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        System.out.printf("  char perm: %d%n", characteristic.getPermissions());
                        System.out.printf("  char prop: %d%n", characteristic.getProperties());
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println(characteristic.toString());
            }
        }
    };

    private BluetoothAdapter.LeScanCallback m_btLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(c_scaleDeviceName.equals(device.getName())) {
                // Remove pending scan stop and run it manually
                m_btHandler.removeCallbacksAndMessages(null);
                m_btAdapter.stopLeScan(this);

                onScaleFound(device);
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
