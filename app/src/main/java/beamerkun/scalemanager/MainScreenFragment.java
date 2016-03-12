package beamerkun.scalemanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothHealth;
import android.bluetooth.BluetoothHealthAppConfiguration;
import android.bluetooth.BluetoothHealthCallback;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Formatter;
import java.util.List;
import java.util.UUID;

/**
 * Mess of a fragment, work heavily in progress
 */
public class MainScreenFragment extends Fragment {

    private void readAllCharacteristics(BluetoothGattService service) {
        if (service != null) {
            for (BluetoothGattCharacteristic c : service.getCharacteristics()) {
                m_btGatt.readCharacteristic(c);
            }
        }
    }

    private void writeAllCharacteristics(BluetoothGattService service) {
        if (service != null) {
            for (BluetoothGattCharacteristic c : service.getCharacteristics()) {
                byte[] val = {(byte)0xff, (byte)0xff, (byte) 0xff, (byte) 0xff};
                c.setValue(val);
                m_btGatt.writeCharacteristic(c);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_screen, container, false);

        final Button bt_on_button = (Button) v.findViewById(R.id.bt_on_button);
        final Button bt_scan_button = (Button) v.findViewById(R.id.bt_scan_button);
        final Button bt_handle = (Button) v.findViewById(R.id.bt_handle);

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
                if (m_btScaleDevice == null)
                    btLeScan();
                else
                    m_btGatt.discoverServices();
            }
        });
        bt_scan_button.setEnabled(m_btAdapter.isEnabled());

        bt_handle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_btGatt == null)
                    return;
                BluetoothGattService service2 = m_btGatt.getService(c_scaleDeviceServiceUUID);
                UUID c_CharUUID_1 = UUID.fromString("1a2ea400-75b9-11e2-be05-0002a5d5c51b");
                UUID c_CharUUID_2 = UUID.fromString("23b4fec0-75b9-11e2-972a-0002a5d5c51b");
                UUID c_CharUUID_3 = UUID.fromString("29f11080-75b9-11e2-8bf6-0002a5d5c51b");
                if (service2 != null) {
                    System.out.println("service 2");
                    BluetoothGattCharacteristic char1 = service2.getCharacteristic(c_CharUUID_1);
                    BluetoothGattCharacteristic char2 = service2.getCharacteristic(c_CharUUID_2);
                    BluetoothGattCharacteristic char3 = service2.getCharacteristic(c_CharUUID_3);

                    m_btGatt.setCharacteristicNotification(char1, true);
                    m_btGatt.setCharacteristicNotification(char2, true);
                    m_btGatt.setCharacteristicNotification(char3, true);

                    for(BluetoothGattDescriptor desc : char1.getDescriptors())
                        System.out.println("desc: " + desc.getUuid());

                    for(BluetoothGattDescriptor desc : char2.getDescriptors())
                        System.out.println("desc: " + desc.getUuid());

                    for (BluetoothGattDescriptor desc : char3.getDescriptors())
                        System.out.println("desc: " + desc.getUuid());

                    char1.getDescriptors().get(0).setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    m_btGatt.writeCharacteristic(char1);

                    char2.getDescriptors().get(0).setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    m_btGatt.writeCharacteristic(char2);

                    byte[] arr = {0x10, 0x00, 0x00, (byte) 0xB6, (byte) 0x18};
                    char3.setValue(arr);
                    m_btGatt.writeCharacteristic(char3);
                }
            }
        });

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

    private static final UUID c_scaleDeviceServiceUUID =
            UUID.fromString("f433bd80-75b8-11e2-97d9-0002a5d5c51b");

    private Handler m_btHandler = new Handler();

    private BluetoothAdapter m_btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice m_btScaleDevice = null;
    private BluetoothGatt m_btGatt = null;

    private BluetoothGattCallback m_btGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                System.out.println("Gatt connected");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                System.out.println("Gatt disconnected");
            } else {
                System.out.println("new state " + newState);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //BluetoothGattService service2 = gatt.getService(c_scaleDeviceServiceUUID_2);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("write char:" + characteristic.getUuid().toString());
            } else {
                System.out.println("write failed " + status);
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("read char:" + characteristic.getUuid().toString());
            } else {
                System.out.println("read failed " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            System.out.println("changed char:" + characteristic.getUuid().toString());
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("read char:" + descriptor.getUuid().toString());
            } else {
                System.out.println("read failed " + status);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("read char:" + descriptor.getUuid().toString());
            } else {
                System.out.println("read failed " + status);
            }
        }
    };

    private BluetoothAdapter.LeScanCallback m_btLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (c_scaleDeviceName.equals(device.getName())) {
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
