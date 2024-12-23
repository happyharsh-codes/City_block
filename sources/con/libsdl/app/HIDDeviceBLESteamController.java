package org.libsdl.app;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;

class HIDDeviceBLESteamController extends BluetoothGattCallback implements HIDDevice {
    private static final int CHROMEBOOK_CONNECTION_CHECK_INTERVAL = 10000;
    private static final String TAG = "hidapi";
    private static final int TRANSPORT_AUTO = 0;
    private static final int TRANSPORT_BREDR = 1;
    private static final int TRANSPORT_LE = 2;
    private static final byte[] enterValveMode = {-64, -121, 3, 8, 7, 0};
    public static final UUID inputCharacteristic = UUID.fromString("100F6C33-1735-4313-B402-38567131E5F3");
    public static final UUID reportCharacteristic = UUID.fromString("100F6C34-1735-4313-B402-38567131E5F3");
    public static final UUID steamControllerService = UUID.fromString("100F6C32-1735-4313-B402-38567131E5F3");
    GattOperation mCurrentOperation = null;
    private BluetoothDevice mDevice;
    private int mDeviceId;
    private boolean mFrozen = false;
    /* access modifiers changed from: private */
    public BluetoothGatt mGatt;
    private Handler mHandler;
    private boolean mIsChromebook = false;
    private boolean mIsConnected = false;
    private boolean mIsReconnecting = false;
    private boolean mIsRegistered = false;
    private HIDDeviceManager mManager;
    /* access modifiers changed from: private */
    public LinkedList<GattOperation> mOperations;

    public void close() {
    }

    public UsbDevice getDevice() {
        return null;
    }

    public String getManufacturerName() {
        return "Valve Corporation";
    }

    public int getProductId() {
        return 4358;
    }

    public String getProductName() {
        return "Steam Controller";
    }

    public String getSerialNumber() {
        return "12345";
    }

    public int getVendorId() {
        return 10462;
    }

    public int getVersion() {
        return TRANSPORT_AUTO;
    }

    public void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
    }

    public void onMtuChanged(BluetoothGatt bluetoothGatt, int i, int i2) {
    }

    public void onReadRemoteRssi(BluetoothGatt bluetoothGatt, int i, int i2) {
    }

    public void onReliableWriteCompleted(BluetoothGatt bluetoothGatt, int i) {
    }

    public boolean open() {
        return true;
    }

    static class GattOperation {
        BluetoothGatt mGatt;
        Operation mOp;
        boolean mResult = true;
        UUID mUuid;
        byte[] mValue;

        private enum Operation {
            CHR_READ,
            CHR_WRITE,
            ENABLE_NOTIFICATION
        }

        private GattOperation(BluetoothGatt bluetoothGatt, Operation operation, UUID uuid) {
            this.mGatt = bluetoothGatt;
            this.mOp = operation;
            this.mUuid = uuid;
        }

        private GattOperation(BluetoothGatt bluetoothGatt, Operation operation, UUID uuid, byte[] bArr) {
            this.mGatt = bluetoothGatt;
            this.mOp = operation;
            this.mUuid = uuid;
            this.mValue = bArr;
        }

        public void run() {
            BluetoothGattCharacteristic characteristic;
            BluetoothGattDescriptor descriptor;
            byte[] bArr;
            int i = AnonymousClass4.$SwitchMap$org$libsdl$app$HIDDeviceBLESteamController$GattOperation$Operation[this.mOp.ordinal()];
            if (i == HIDDeviceBLESteamController.TRANSPORT_BREDR) {
                if (!this.mGatt.readCharacteristic(getCharacteristic(this.mUuid))) {
                    Log.e(HIDDeviceBLESteamController.TAG, "Unable to read characteristic " + this.mUuid.toString());
                    this.mResult = false;
                    return;
                }
                this.mResult = true;
            } else if (i == HIDDeviceBLESteamController.TRANSPORT_LE) {
                BluetoothGattCharacteristic characteristic2 = getCharacteristic(this.mUuid);
                characteristic2.setValue(this.mValue);
                if (!this.mGatt.writeCharacteristic(characteristic2)) {
                    Log.e(HIDDeviceBLESteamController.TAG, "Unable to write characteristic " + this.mUuid.toString());
                    this.mResult = false;
                    return;
                }
                this.mResult = true;
            } else if (i == 3 && (characteristic = getCharacteristic(this.mUuid)) != null && (descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))) != null) {
                int properties = characteristic.getProperties();
                if ((properties & 16) == 16) {
                    bArr = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                } else if ((properties & 32) == 32) {
                    bArr = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
                } else {
                    Log.e(HIDDeviceBLESteamController.TAG, "Unable to start notifications on input characteristic");
                    this.mResult = false;
                    return;
                }
                this.mGatt.setCharacteristicNotification(characteristic, true);
                descriptor.setValue(bArr);
                if (!this.mGatt.writeDescriptor(descriptor)) {
                    Log.e(HIDDeviceBLESteamController.TAG, "Unable to write descriptor " + this.mUuid.toString());
                    this.mResult = false;
                    return;
                }
                this.mResult = true;
            }
        }

        public boolean finish() {
            return this.mResult;
        }

        private BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
            BluetoothGattService service = this.mGatt.getService(HIDDeviceBLESteamController.steamControllerService);
            if (service == null) {
                return null;
            }
            return service.getCharacteristic(uuid);
        }

        public static GattOperation readCharacteristic(BluetoothGatt bluetoothGatt, UUID uuid) {
            return new GattOperation(bluetoothGatt, Operation.CHR_READ, uuid);
        }

        public static GattOperation writeCharacteristic(BluetoothGatt bluetoothGatt, UUID uuid, byte[] bArr) {
            return new GattOperation(bluetoothGatt, Operation.CHR_WRITE, uuid, bArr);
        }

        public static GattOperation enableNotification(BluetoothGatt bluetoothGatt, UUID uuid) {
            return new GattOperation(bluetoothGatt, Operation.ENABLE_NOTIFICATION, uuid);
        }
    }

    /* renamed from: org.libsdl.app.HIDDeviceBLESteamController$4  reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$org$libsdl$app$HIDDeviceBLESteamController$GattOperation$Operation;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                org.libsdl.app.HIDDeviceBLESteamController$GattOperation$Operation[] r0 = org.libsdl.app.HIDDeviceBLESteamController.GattOperation.Operation.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$org$libsdl$app$HIDDeviceBLESteamController$GattOperation$Operation = r0
                org.libsdl.app.HIDDeviceBLESteamController$GattOperation$Operation r1 = org.libsdl.app.HIDDeviceBLESteamController.GattOperation.Operation.CHR_READ     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$org$libsdl$app$HIDDeviceBLESteamController$GattOperation$Operation     // Catch:{ NoSuchFieldError -> 0x001d }
                org.libsdl.app.HIDDeviceBLESteamController$GattOperation$Operation r1 = org.libsdl.app.HIDDeviceBLESteamController.GattOperation.Operation.CHR_WRITE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$org$libsdl$app$HIDDeviceBLESteamController$GattOperation$Operation     // Catch:{ NoSuchFieldError -> 0x0028 }
                org.libsdl.app.HIDDeviceBLESteamController$GattOperation$Operation r1 = org.libsdl.app.HIDDeviceBLESteamController.GattOperation.Operation.ENABLE_NOTIFICATION     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.libsdl.app.HIDDeviceBLESteamController.AnonymousClass4.<clinit>():void");
        }
    }

    public HIDDeviceBLESteamController(HIDDeviceManager hIDDeviceManager, BluetoothDevice bluetoothDevice) {
        this.mManager = hIDDeviceManager;
        this.mDevice = bluetoothDevice;
        this.mDeviceId = hIDDeviceManager.getDeviceIDForIdentifier(getIdentifier());
        this.mIsRegistered = false;
        this.mIsChromebook = this.mManager.getContext().getPackageManager().hasSystemFeature("org.chromium.arc.device_management");
        this.mOperations = new LinkedList<>();
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mGatt = connectGatt();
    }

    public String getIdentifier() {
        Object[] objArr = new Object[TRANSPORT_BREDR];
        objArr[TRANSPORT_AUTO] = this.mDevice.getAddress();
        return String.format("SteamController.%s", objArr);
    }

    public BluetoothGatt getGatt() {
        return this.mGatt;
    }

    private BluetoothGatt connectGatt(boolean z) {
        if (Build.VERSION.SDK_INT < 23) {
            return this.mDevice.connectGatt(this.mManager.getContext(), z, this);
        }
        try {
            return this.mDevice.connectGatt(this.mManager.getContext(), z, this, TRANSPORT_LE);
        } catch (Exception unused) {
            return this.mDevice.connectGatt(this.mManager.getContext(), z, this);
        }
    }

    private BluetoothGatt connectGatt() {
        return connectGatt(false);
    }

    /* access modifiers changed from: protected */
    public int getConnectionState() {
        BluetoothManager bluetoothManager;
        Context context = this.mManager.getContext();
        if (context == null || (bluetoothManager = (BluetoothManager) context.getSystemService("bluetooth")) == null) {
            return TRANSPORT_AUTO;
        }
        return bluetoothManager.getConnectionState(this.mDevice, 7);
    }

    public void reconnect() {
        if (getConnectionState() != TRANSPORT_LE) {
            this.mGatt.disconnect();
            this.mGatt = connectGatt();
        }
    }

    /* access modifiers changed from: protected */
    public void checkConnectionForChromebookIssue() {
        if (this.mIsChromebook) {
            int connectionState = getConnectionState();
            if (connectionState == 0) {
                Log.v(TAG, "Chromebook: We have either been disconnected, or the Chromebook BtGatt.ContextMap bug has bitten us.  Attempting a disconnect/reconnect, but we may not be able to recover.");
                this.mIsReconnecting = true;
                this.mGatt.disconnect();
                this.mGatt = connectGatt(false);
            } else if (connectionState == TRANSPORT_BREDR) {
                Log.v(TAG, "Chromebook: We're still trying to connect.  Waiting a bit longer.");
            } else if (connectionState == TRANSPORT_LE) {
                if (!this.mIsConnected) {
                    Log.v(TAG, "Chromebook: We are in a very bad state; the controller shows as connected in the underlying Bluetooth layer, but we never received a callback.  Forcing a reconnect.");
                    this.mIsReconnecting = true;
                    this.mGatt.disconnect();
                    this.mGatt = connectGatt(false);
                } else if (isRegistered()) {
                    Log.v(TAG, "Chromebook: We are connected, and registered.  Everything's good!");
                    return;
                } else if (this.mGatt.getServices().size() > 0) {
                    Log.v(TAG, "Chromebook: We are connected to a controller, but never got our registration.  Trying to recover.");
                    probeService(this);
                } else {
                    Log.v(TAG, "Chromebook: We are connected to a controller, but never discovered services.  Trying to recover.");
                    this.mIsReconnecting = true;
                    this.mGatt.disconnect();
                    this.mGatt = connectGatt(false);
                }
            }
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    this.checkConnectionForChromebookIssue();
                }
            }, 10000);
        }
    }

    private boolean isRegistered() {
        return this.mIsRegistered;
    }

    private void setRegistered() {
        this.mIsRegistered = true;
    }

    private boolean probeService(HIDDeviceBLESteamController hIDDeviceBLESteamController) {
        if (isRegistered()) {
            return true;
        }
        if (!this.mIsConnected) {
            return false;
        }
        Log.v(TAG, "probeService controller=" + hIDDeviceBLESteamController);
        for (BluetoothGattService next : this.mGatt.getServices()) {
            if (next.getUuid().equals(steamControllerService)) {
                Log.v(TAG, "Found Valve steam controller service " + next.getUuid());
                for (BluetoothGattCharacteristic next2 : next.getCharacteristics()) {
                    if (next2.getUuid().equals(inputCharacteristic)) {
                        Log.v(TAG, "Found input characteristic");
                        if (next2.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")) != null) {
                            enableNotification(next2.getUuid());
                        }
                    }
                }
                return true;
            }
        }
        if (this.mGatt.getServices().size() == 0 && this.mIsChromebook && !this.mIsReconnecting) {
            Log.e(TAG, "Chromebook: Discovered services were empty; this almost certainly means the BtGatt.ContextMap bug has bitten us.");
            this.mIsConnected = false;
            this.mIsReconnecting = true;
            this.mGatt.disconnect();
            this.mGatt = connectGatt(false);
        }
        return false;
    }

    private void finishCurrentGattOperation() {
        GattOperation gattOperation;
        synchronized (this.mOperations) {
            gattOperation = this.mCurrentOperation;
            if (gattOperation != null) {
                this.mCurrentOperation = null;
            } else {
                gattOperation = null;
            }
        }
        if (gattOperation != null && !gattOperation.finish()) {
            this.mOperations.addFirst(gattOperation);
        }
        executeNextGattOperation();
    }

    private void executeNextGattOperation() {
        synchronized (this.mOperations) {
            if (this.mCurrentOperation == null) {
                if (!this.mOperations.isEmpty()) {
                    this.mCurrentOperation = this.mOperations.removeFirst();
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            synchronized (HIDDeviceBLESteamController.this.mOperations) {
                                if (HIDDeviceBLESteamController.this.mCurrentOperation == null) {
                                    Log.e(HIDDeviceBLESteamController.TAG, "Current operation null in executor?");
                                } else {
                                    HIDDeviceBLESteamController.this.mCurrentOperation.run();
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    private void queueGattOperation(GattOperation gattOperation) {
        synchronized (this.mOperations) {
            this.mOperations.add(gattOperation);
        }
        executeNextGattOperation();
    }

    private void enableNotification(UUID uuid) {
        queueGattOperation(GattOperation.enableNotification(this.mGatt, uuid));
    }

    public void writeCharacteristic(UUID uuid, byte[] bArr) {
        queueGattOperation(GattOperation.writeCharacteristic(this.mGatt, uuid, bArr));
    }

    public void readCharacteristic(UUID uuid) {
        queueGattOperation(GattOperation.readCharacteristic(this.mGatt, uuid));
    }

    public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
        this.mIsReconnecting = false;
        if (i2 == TRANSPORT_LE) {
            this.mIsConnected = true;
            if (!isRegistered()) {
                this.mHandler.post(new Runnable() {
                    public void run() {
                        HIDDeviceBLESteamController.this.mGatt.discoverServices();
                    }
                });
            }
        } else if (i2 == 0) {
            this.mIsConnected = false;
        }
    }

    public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
        if (i != 0) {
            return;
        }
        if (bluetoothGatt.getServices().size() == 0) {
            Log.v(TAG, "onServicesDiscovered returned zero services; something has gone horribly wrong down in Android's Bluetooth stack.");
            this.mIsReconnecting = true;
            this.mIsConnected = false;
            bluetoothGatt.disconnect();
            this.mGatt = connectGatt(false);
            return;
        }
        probeService(this);
    }

    public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
        if (bluetoothGattCharacteristic.getUuid().equals(reportCharacteristic) && !this.mFrozen) {
            this.mManager.HIDDeviceFeatureReport(getId(), bluetoothGattCharacteristic.getValue());
        }
        finishCurrentGattOperation();
    }

    public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
        if (bluetoothGattCharacteristic.getUuid().equals(reportCharacteristic) && !isRegistered()) {
            Log.v(TAG, "Registering Steam Controller with ID: " + getId());
            this.mManager.HIDDeviceConnected(getId(), getIdentifier(), getVendorId(), getProductId(), getSerialNumber(), getVersion(), getManufacturerName(), getProductName(), TRANSPORT_AUTO, TRANSPORT_AUTO, TRANSPORT_AUTO, TRANSPORT_AUTO);
            setRegistered();
        }
        finishCurrentGattOperation();
    }

    public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (bluetoothGattCharacteristic.getUuid().equals(inputCharacteristic) && !this.mFrozen) {
            this.mManager.HIDDeviceInputReport(getId(), bluetoothGattCharacteristic.getValue());
        }
    }

    public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
        BluetoothGattCharacteristic characteristic;
        BluetoothGattCharacteristic characteristic2 = bluetoothGattDescriptor.getCharacteristic();
        if (characteristic2.getUuid().equals(inputCharacteristic) && (characteristic = characteristic2.getService().getCharacteristic(reportCharacteristic)) != null) {
            Log.v(TAG, "Writing report characteristic to enter valve mode");
            characteristic.setValue(enterValveMode);
            bluetoothGatt.writeCharacteristic(characteristic);
        }
        finishCurrentGattOperation();
    }

    public int getId() {
        return this.mDeviceId;
    }

    public int sendFeatureReport(byte[] bArr) {
        if (!isRegistered()) {
            Log.e(TAG, "Attempted sendFeatureReport before Steam Controller is registered!");
            if (!this.mIsConnected) {
                return -1;
            }
            probeService(this);
            return -1;
        }
        writeCharacteristic(reportCharacteristic, Arrays.copyOfRange(bArr, TRANSPORT_BREDR, bArr.length - TRANSPORT_BREDR));
        return bArr.length;
    }

    public int sendOutputReport(byte[] bArr) {
        if (!isRegistered()) {
            Log.e(TAG, "Attempted sendOutputReport before Steam Controller is registered!");
            if (!this.mIsConnected) {
                return -1;
            }
            probeService(this);
            return -1;
        }
        writeCharacteristic(reportCharacteristic, bArr);
        return bArr.length;
    }

    public boolean getFeatureReport(byte[] bArr) {
        if (!isRegistered()) {
            Log.e(TAG, "Attempted getFeatureReport before Steam Controller is registered!");
            if (!this.mIsConnected) {
                return false;
            }
            probeService(this);
            return false;
        }
        readCharacteristic(reportCharacteristic);
        return true;
    }

    public void setFrozen(boolean z) {
        this.mFrozen = z;
    }

    public void shutdown() {
        close();
        BluetoothGatt bluetoothGatt = this.mGatt;
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            this.mGatt = null;
        }
        this.mManager = null;
        this.mIsRegistered = false;
        this.mIsConnected = false;
        this.mOperations.clear();
    }
}
