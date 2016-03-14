package beamerkun.scalemanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Date;
import java.util.UUID;

/**
 * Mess of a fragment, work heavily in progress
 */
public class MainScreenFragment extends Fragment {

    public class Measurement {
        public Date date;
        public float weight = 0;
        public float bodyFat = 0;
        public float bodyWater = 0;
        public float boneWeight = 0;
        public float muscleMass = 0;
        public int visceralFat = 0;
        public int BMR = 0;
        public float BMI = 0;

        public Measurement() {
            date = new Date();
        }
        public Measurement(byte[] bytes) {
            date = new Date();
            weight = (bytes[4] << 8 + bytes[5]) / 10f;
            bodyFat = (bytes[6] << 8 + bytes[7]) / 10f;
            bodyWater = (bytes[8] << 8 + bytes[9]) / 10f;
            boneWeight = (bytes[10] << 8 + bytes[11]) / 10f;
            muscleMass = (bytes[12] << 8 + bytes[13]) / 10f;
            visceralFat = bytes[14];
            BMR = bytes[15] << 8 + bytes[16];
            BMI = (bytes[17] << 8 + bytes[18]) / 10f;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_screen, container, false);

        final Button bt_connect_button = (Button) v.findViewById(R.id.bt_connect_button);

        bt_connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_btScaleDevice == null)
                    btLeScan();
                else
                    m_btGatt.discoverServices();
            }
        });

        return v;
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
        m_btGatt = m_btScaleDevice.connectGatt(
                getActivity().getApplicationContext(), true, m_btGattCallback);
    }

    private static final long c_scanPeriod = 10000;
    
    private static final String c_scaleDeviceName = "VScale";

    private static final UUID c_scaleServiceUUID =
            UUID.fromString("f433bd80-75b8-11e2-97d9-0002a5d5c51b");

    private static final UUID c_scaleCharResultUUID =
            UUID.fromString("1a2ea400-75b9-11e2-be05-0002a5d5c51b");

    private static final UUID c_scaleCharUserDataUUID =
            UUID.fromString("29f11080-75b9-11e2-8bf6-0002a5d5c51b");

    private static final UUID c_gattClientCharacteristicConfigurationUUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static byte[] c_testUserData =
            {0x10, 0x00, 0x00, (byte) 0x18, (byte) 0xB6};

    private Handler m_btHandler = new Handler();

    private BluetoothAdapter m_btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice m_btScaleDevice = null;
    private BluetoothGatt m_btGatt = null;
    private BluetoothGattService m_btScaleService = null;
    private BluetoothGattCharacteristic m_btScaleUserData = null;
    private BluetoothGattCharacteristic m_btScaleResult = null;

    private Measurement m_measurement = null;

    private BluetoothGattCallback m_btGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                m_btScaleDevice = null;
                m_btGatt = null;
                m_btScaleService = null;
                m_btScaleUserData = null;
                m_btScaleResult = null;
                m_measurement = null;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                m_btScaleService = m_btGatt.getService(c_scaleServiceUUID);

                if(m_btScaleService == null)
                    return;

                m_btScaleUserData = m_btScaleService.getCharacteristic(c_scaleCharUserDataUUID);
                m_btScaleResult = m_btScaleService.getCharacteristic(c_scaleCharResultUUID);

                m_btGatt.setCharacteristicNotification(m_btScaleResult, true);
                BluetoothGattDescriptor descriptor =
                        m_btScaleResult.getDescriptor(c_gattClientCharacteristicConfigurationUUID);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                m_btGatt.writeDescriptor(descriptor);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if(characteristic.getUuid() == m_btScaleResult.getUuid()) {
                byte[] value = m_btScaleResult.getValue();
                if(m_measurement == null) {
                    Measurement temp = new Measurement(value);
                    if (temp.weight != 0.0f) {
                        m_measurement = new Measurement();

                        m_btScaleUserData.setValue(c_testUserData);
                        m_btGatt.writeCharacteristic(m_btScaleUserData);
                    }
                } else {
                    m_measurement = new Measurement(value);

                    // TODO: store this data somewhere

                    m_measurement = null;
                }
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
}
