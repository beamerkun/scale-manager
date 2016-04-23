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

import beamerkun.scalemanager.dao.Measurement;
import beamerkun.scalemanager.dao.User;

import java.util.UUID;

/**
 * Mess of a fragment, work heavily in progress
 */
public class MainScreenFragment extends Fragment {
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

    private Handler m_btHandler = new Handler();

    private BluetoothAdapter m_btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice m_btScaleDevice = null;
    private BluetoothGatt m_btGatt = null;
    private BluetoothGattService m_btScaleService = null;
    private BluetoothGattCharacteristic m_btScaleUserData = null;
    private BluetoothGattCharacteristic m_btScaleResult = null;

    private enum MeasurementState {NONE, WEIGHT, FULL};

    User test_user;

    MeasurementState m_measurementState = MeasurementState.NONE;

    private BluetoothGattCallback m_btGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // TODO: prettify
                m_btScaleDevice = null;
                m_btGatt = null;
                m_btScaleService = null;
                m_btScaleUserData = null;
                m_btScaleResult = null;
                m_measurementState = MeasurementState.NONE;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                m_btScaleService = m_btGatt.getService(c_scaleServiceUUID);

                if (m_btScaleService == null)
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
            if (characteristic.getUuid() != m_btScaleResult.getUuid())
                return;

            byte[] value = m_btScaleResult.getValue();
            switch (m_measurementState) {
                case FULL:
                case NONE:
                    Measurement temp = new Measurement(value);
                    if (temp.getWeight() == 0.0f)
                        //TODO restart connection
                        return;

                    // TODO do it better!
                    test_user = StorageHelper.getInstance(getActivity().getApplicationContext()).getUser();

                    m_btScaleUserData.setValue(test_user.toByteArray());
                    m_btGatt.writeCharacteristic(m_btScaleUserData);
                    m_measurementState = MeasurementState.WEIGHT;
                    break;
                case WEIGHT:
                    Measurement measurement = new Measurement(value);
                    measurement.setUserId(test_user.getId());
                    StorageHelper.getInstance(getActivity().getApplicationContext()).saveMeasurement(measurement);
                    m_measurementState = MeasurementState.FULL;
                    break;
                default:
                    return;
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
