package beamerkun.scalemanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import beamerkun.scalemanager.dao.Measurement;

// Mess moved to another class
// TODO cleanup somehow

public class ScaleHelper {
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
    public static ScaleHelper sInstance = null;
    private Context m_context = null;
    private Handler m_btHandler = new Handler();
    private BluetoothAdapter m_btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice m_btScaleDevice;
    private BluetoothGatt m_btGatt;
    private BluetoothGattService m_btScaleService;
    private BluetoothGattCharacteristic m_btScaleUserData;
    private BluetoothGattCharacteristic m_btScaleResult;
    private MeasurementState m_measurementState;
    private byte[] m_userData = new byte[5];
    private List<ScaleMeasurementListener> scaleMeasurementListenerList = new ArrayList<>();
    private BluetoothGattCallback m_btGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                reset();
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

                    if (temp.getWeight() == 0.0f || m_userData[3] == 0x00 || m_userData[4] == 0x00) {
                        reset();
                        return;
                    }

                    m_btScaleUserData.setValue(m_userData);
                    m_btGatt.writeCharacteristic(m_btScaleUserData);
                    m_measurementState = MeasurementState.WEIGHT;
                    onMeasurementStateChanged(m_measurementState);
                    break;
                case WEIGHT:
                    onMeasurementReceived(new Measurement(value));
                    m_measurementState = MeasurementState.FULL;
                    onMeasurementStateChanged(m_measurementState);
                    break;
                default:
                    break;
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

    private ScaleHelper(Context context) {
        m_context = context;
        reset();
    }

    static ScaleHelper getInstance(Context context) {
        if (sInstance == null)
            sInstance = new ScaleHelper(context);
        return sInstance;
    }

    public void startListening(ScaleMeasurementListener listener) {
        scaleMeasurementListenerList.add(listener);
    }

    public void stopListening(ScaleMeasurementListener listener) {
        scaleMeasurementListenerList.remove(listener);
    }

    public void reset() {
        m_btScaleDevice = null;
        m_btGatt = null;
        m_btScaleService = null;
        m_btScaleUserData = null;
        m_btScaleResult = null;
        m_measurementState = MeasurementState.NONE;
        onMeasurementStateChanged(m_measurementState);
    }

    public void setUserData(byte[] bytes) {
        for (int i = 0; i < m_userData.length; ++i)
            m_userData[i] = bytes[i];
    }

    public void startScan() {
        if (m_btScaleDevice == null)
            btLeScan();
        else
            m_btGatt.discoverServices();
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
                m_context, true, m_btGattCallback);
    }

    private void onMeasurementReceived(Measurement measurement) {
        for (ScaleMeasurementListener listener : scaleMeasurementListenerList) {
            listener.onMeasurementReceived(measurement);
        }
    }

    private void onMeasurementStateChanged(MeasurementState new_state) {
        for (ScaleMeasurementListener listener : scaleMeasurementListenerList) {
            listener.onMeasurementStateChanged(new_state);
        }
    }

    public enum MeasurementState {NONE, WEIGHT, FULL}

    public interface ScaleMeasurementListener {
        public void onMeasurementStateChanged(MeasurementState new_state);

        public void onMeasurementReceived(Measurement measurement);
    }
}
