package org.libsdl.app;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HIDDeviceManager {
    private static final String ACTION_USB_PERMISSION = "org.libsdl.app.USB_PERMISSION";
    private static final String TAG = "hidapi";
    private static HIDDeviceManager sManager;
    private static int sManagerRefCount;
    private final BroadcastReceiver mBluetoothBroadcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                Log.d(HIDDeviceManager.TAG, "Bluetooth device connected: " + bluetoothDevice);
                if (HIDDeviceManager.this.isSteamController(bluetoothDevice)) {
                    HIDDeviceManager.this.connectBluetoothDevice(bluetoothDevice);
                }
            }
            if (action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                BluetoothDevice bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                Log.d(HIDDeviceManager.TAG, "Bluetooth device disconnected: " + bluetoothDevice2);
                HIDDeviceManager.this.disconnectBluetoothDevice(bluetoothDevice2);
            }
        }
    };
    private HashMap<BluetoothDevice, HIDDeviceBLESteamController> mBluetoothDevices = new HashMap<>();
    private BluetoothManager mBluetoothManager;
    private Context mContext;
    private HashMap<Integer, HIDDevice> mDevicesById = new HashMap<>();
    private Handler mHandler;
    private boolean mIsChromebook = false;
    private List<BluetoothDevice> mLastBluetoothDevices;
    private int mNextDeviceId = 0;
    private SharedPreferences mSharedPreferences = null;
    private final BroadcastReceiver mUsbBroadcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                HIDDeviceManager.this.handleUsbDeviceAttached((UsbDevice) intent.getParcelableExtra("device"));
            } else if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                HIDDeviceManager.this.handleUsbDeviceDetached((UsbDevice) intent.getParcelableExtra("device"));
            } else if (action.equals(HIDDeviceManager.ACTION_USB_PERMISSION)) {
                HIDDeviceManager.this.handleUsbDevicePermission((UsbDevice) intent.getParcelableExtra("device"), intent.getBooleanExtra("permission", false));
            }
        }
    };
    private UsbManager mUsbManager;

    private native void HIDDeviceRegisterCallback();

    private native void HIDDeviceReleaseCallback();

    /* access modifiers changed from: package-private */
    public native void HIDDeviceConnected(int i, String str, int i2, int i3, String str2, int i4, String str3, String str4, int i5, int i6, int i7, int i8);

    /* access modifiers changed from: package-private */
    public native void HIDDeviceDisconnected(int i);

    /* access modifiers changed from: package-private */
    public native void HIDDeviceFeatureReport(int i, byte[] bArr);

    /* access modifiers changed from: package-private */
    public native void HIDDeviceInputReport(int i, byte[] bArr);

    /* access modifiers changed from: package-private */
    public native void HIDDeviceOpenPending(int i);

    /* access modifiers changed from: package-private */
    public native void HIDDeviceOpenResult(int i, boolean z);

    public static HIDDeviceManager acquire(Context context) {
        if (sManagerRefCount == 0) {
            sManager = new HIDDeviceManager(context);
        }
        sManagerRefCount++;
        return sManager;
    }

    public static void release(HIDDeviceManager hIDDeviceManager) {
        HIDDeviceManager hIDDeviceManager2 = sManager;
        if (hIDDeviceManager == hIDDeviceManager2) {
            int i = sManagerRefCount - 1;
            sManagerRefCount = i;
            if (i == 0) {
                hIDDeviceManager2.close();
                sManager = null;
            }
        }
    }

    private HIDDeviceManager(Context context) {
        this.mContext = context;
        HIDDeviceRegisterCallback();
        this.mSharedPreferences = this.mContext.getSharedPreferences(TAG, 0);
        this.mIsChromebook = this.mContext.getPackageManager().hasSystemFeature("org.chromium.arc.device_management");
        this.mNextDeviceId = this.mSharedPreferences.getInt("next_device_id", 0);
    }

    public Context getContext() {
        return this.mContext;
    }

    public int getDeviceIDForIdentifier(String str) {
        SharedPreferences.Editor edit = this.mSharedPreferences.edit();
        int i = this.mSharedPreferences.getInt(str, 0);
        if (i == 0) {
            i = this.mNextDeviceId;
            int i2 = i + 1;
            this.mNextDeviceId = i2;
            edit.putInt("next_device_id", i2);
        }
        edit.putInt(str, i);
        edit.commit();
        return i;
    }

    private void initializeUSB() {
        UsbManager usbManager = (UsbManager) this.mContext.getSystemService("usb");
        this.mUsbManager = usbManager;
        if (usbManager != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
            intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
            intentFilter.addAction(ACTION_USB_PERMISSION);
            this.mContext.registerReceiver(this.mUsbBroadcast, intentFilter);
            for (UsbDevice handleUsbDeviceAttached : this.mUsbManager.getDeviceList().values()) {
                handleUsbDeviceAttached(handleUsbDeviceAttached);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public UsbManager getUSBManager() {
        return this.mUsbManager;
    }

    private void shutdownUSB() {
        try {
            this.mContext.unregisterReceiver(this.mUsbBroadcast);
        } catch (Exception unused) {
        }
    }

    private boolean isHIDDeviceInterface(UsbDevice usbDevice, UsbInterface usbInterface) {
        if (usbInterface.getInterfaceClass() != 3 && !isXbox360Controller(usbDevice, usbInterface) && !isXboxOneController(usbDevice, usbInterface)) {
            return false;
        }
        return true;
    }

    private boolean isXbox360Controller(UsbDevice usbDevice, UsbInterface usbInterface) {
        int[] iArr = {121, 1103, 1118, 1133, 1390, 1699, 1848, 2047, 3695, 3853, 4152, 4553, 4779, 5168, 5227, 5426, 5604, 5678, 5769, 6473, 7085, 8406, 9414, 11298};
        if (usbInterface.getInterfaceClass() == 255 && usbInterface.getInterfaceSubclass() == 93 && (usbInterface.getInterfaceProtocol() == 1 || usbInterface.getInterfaceProtocol() == 129)) {
            int vendorId = usbDevice.getVendorId();
            for (int i = 0; i < 24; i++) {
                if (vendorId == iArr[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isXboxOneController(UsbDevice usbDevice, UsbInterface usbInterface) {
        int[] iArr = {1118, 1848, 3695, 3853, 5426, 8406, 9414, 11720, 11812};
        if (usbInterface.getId() == 0 && usbInterface.getInterfaceClass() == 255 && usbInterface.getInterfaceSubclass() == 71 && usbInterface.getInterfaceProtocol() == 208) {
            int vendorId = usbDevice.getVendorId();
            for (int i = 0; i < 9; i++) {
                if (vendorId == iArr[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void handleUsbDeviceAttached(UsbDevice usbDevice) {
        connectHIDDeviceUSB(usbDevice);
    }

    /* access modifiers changed from: private */
    public void handleUsbDeviceDetached(UsbDevice usbDevice) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (HIDDevice next : this.mDevicesById.values()) {
            if (usbDevice.equals(next.getDevice())) {
                arrayList.add(Integer.valueOf(next.getId()));
            }
        }
        for (Integer intValue : arrayList) {
            int intValue2 = intValue.intValue();
            this.mDevicesById.remove(Integer.valueOf(intValue2));
            this.mDevicesById.get(Integer.valueOf(intValue2)).shutdown();
            HIDDeviceDisconnected(intValue2);
        }
    }

    /* access modifiers changed from: private */
    public void handleUsbDevicePermission(UsbDevice usbDevice, boolean z) {
        for (HIDDevice next : this.mDevicesById.values()) {
            if (usbDevice.equals(next.getDevice())) {
                boolean z2 = false;
                if (z) {
                    z2 = next.open();
                }
                HIDDeviceOpenResult(next.getId(), z2);
            }
        }
    }

    private void connectHIDDeviceUSB(UsbDevice usbDevice) {
        UsbDevice usbDevice2 = usbDevice;
        synchronized (this) {
            int i = 0;
            for (int i2 = 0; i2 < usbDevice.getInterfaceCount(); i2++) {
                UsbInterface usbInterface = usbDevice2.getInterface(i2);
                if (isHIDDeviceInterface(usbDevice2, usbInterface)) {
                    int id = 1 << usbInterface.getId();
                    if ((i & id) == 0) {
                        int i3 = i | id;
                        HIDDeviceUSB hIDDeviceUSB = new HIDDeviceUSB(this, usbDevice2, i2);
                        int id2 = hIDDeviceUSB.getId();
                        this.mDevicesById.put(Integer.valueOf(id2), hIDDeviceUSB);
                        HIDDeviceConnected(id2, hIDDeviceUSB.getIdentifier(), hIDDeviceUSB.getVendorId(), hIDDeviceUSB.getProductId(), hIDDeviceUSB.getSerialNumber(), hIDDeviceUSB.getVersion(), hIDDeviceUSB.getManufacturerName(), hIDDeviceUSB.getProductName(), usbInterface.getId(), usbInterface.getInterfaceClass(), usbInterface.getInterfaceSubclass(), usbInterface.getInterfaceProtocol());
                        i = i3;
                    }
                }
            }
        }
    }

    private void initializeBluetooth() {
        BluetoothAdapter adapter;
        Log.d(TAG, "Initializing Bluetooth");
        if (Build.VERSION.SDK_INT <= 30 && this.mContext.getPackageManager().checkPermission("android.permission.BLUETOOTH", this.mContext.getPackageName()) != 0) {
            Log.d(TAG, "Couldn't initialize Bluetooth, missing android.permission.BLUETOOTH");
        } else if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le") || Build.VERSION.SDK_INT < 18) {
            Log.d(TAG, "Couldn't initialize Bluetooth, this version of Android does not support Bluetooth LE");
        } else {
            BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
            this.mBluetoothManager = bluetoothManager;
            if (bluetoothManager != null && (adapter = bluetoothManager.getAdapter()) != null) {
                for (BluetoothDevice next : adapter.getBondedDevices()) {
                    Log.d(TAG, "Bluetooth device available: " + next);
                    if (isSteamController(next)) {
                        connectBluetoothDevice(next);
                    }
                }
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
                intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
                this.mContext.registerReceiver(this.mBluetoothBroadcast, intentFilter);
                if (this.mIsChromebook) {
                    this.mHandler = new Handler(Looper.getMainLooper());
                    this.mLastBluetoothDevices = new ArrayList();
                }
            }
        }
    }

    private void shutdownBluetooth() {
        try {
            this.mContext.unregisterReceiver(this.mBluetoothBroadcast);
        } catch (Exception unused) {
        }
    }

    public void chromebookConnectionHandler() {
        if (this.mIsChromebook) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            List<BluetoothDevice> connectedDevices = this.mBluetoothManager.getConnectedDevices(7);
            for (BluetoothDevice next : connectedDevices) {
                if (!this.mLastBluetoothDevices.contains(next)) {
                    arrayList2.add(next);
                }
            }
            for (BluetoothDevice next2 : this.mLastBluetoothDevices) {
                if (!connectedDevices.contains(next2)) {
                    arrayList.add(next2);
                }
            }
            this.mLastBluetoothDevices = connectedDevices;
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                disconnectBluetoothDevice((BluetoothDevice) it.next());
            }
            Iterator it2 = arrayList2.iterator();
            while (it2.hasNext()) {
                connectBluetoothDevice((BluetoothDevice) it2.next());
            }
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    this.chromebookConnectionHandler();
                }
            }, 10000);
        }
    }

    public boolean connectBluetoothDevice(BluetoothDevice bluetoothDevice) {
        Log.v(TAG, "connectBluetoothDevice device=" + bluetoothDevice);
        synchronized (this) {
            if (this.mBluetoothDevices.containsKey(bluetoothDevice)) {
                Log.v(TAG, "Steam controller with address " + bluetoothDevice + " already exists, attempting reconnect");
                this.mBluetoothDevices.get(bluetoothDevice).reconnect();
                return false;
            }
            HIDDeviceBLESteamController hIDDeviceBLESteamController = new HIDDeviceBLESteamController(this, bluetoothDevice);
            int id = hIDDeviceBLESteamController.getId();
            this.mBluetoothDevices.put(bluetoothDevice, hIDDeviceBLESteamController);
            this.mDevicesById.put(Integer.valueOf(id), hIDDeviceBLESteamController);
            return true;
        }
    }

    public void disconnectBluetoothDevice(BluetoothDevice bluetoothDevice) {
        synchronized (this) {
            HIDDeviceBLESteamController hIDDeviceBLESteamController = this.mBluetoothDevices.get(bluetoothDevice);
            if (hIDDeviceBLESteamController != null) {
                int id = hIDDeviceBLESteamController.getId();
                this.mBluetoothDevices.remove(bluetoothDevice);
                this.mDevicesById.remove(Integer.valueOf(id));
                hIDDeviceBLESteamController.shutdown();
                HIDDeviceDisconnected(id);
            }
        }
    }

    public boolean isSteamController(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null || bluetoothDevice.getName() == null || !bluetoothDevice.getName().equals("SteamController") || (bluetoothDevice.getType() & 2) == 0) {
            return false;
        }
        return true;
    }

    private void close() {
        shutdownUSB();
        shutdownBluetooth();
        synchronized (this) {
            for (HIDDevice shutdown : this.mDevicesById.values()) {
                shutdown.shutdown();
            }
            this.mDevicesById.clear();
            this.mBluetoothDevices.clear();
            HIDDeviceReleaseCallback();
        }
    }

    public void setFrozen(boolean z) {
        synchronized (this) {
            for (HIDDevice frozen : this.mDevicesById.values()) {
                frozen.setFrozen(z);
            }
        }
    }

    private HIDDevice getDevice(int i) {
        HIDDevice hIDDevice;
        synchronized (this) {
            hIDDevice = this.mDevicesById.get(Integer.valueOf(i));
            if (hIDDevice == null) {
                Log.v(TAG, "No device for id: " + i);
                Log.v(TAG, "Available devices: " + this.mDevicesById.keySet());
            }
        }
        return hIDDevice;
    }

    public boolean initialize(boolean z, boolean z2) {
        Log.v(TAG, "initialize(" + z + ", " + z2 + ")");
        if (z) {
            initializeUSB();
        }
        if (!z2) {
            return true;
        }
        initializeBluetooth();
        return true;
    }

    public boolean openDevice(int i) {
        Log.v(TAG, "openDevice deviceID=" + i);
        HIDDevice device = getDevice(i);
        if (device == null) {
            HIDDeviceDisconnected(i);
            return false;
        }
        UsbDevice device2 = device.getDevice();
        if (device2 == null || this.mUsbManager.hasPermission(device2)) {
            try {
                return device.open();
            } catch (Exception e) {
                Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
                return false;
            }
        } else {
            HIDDeviceOpenPending(i);
            try {
                this.mUsbManager.requestPermission(device2, PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_USB_PERMISSION), Build.VERSION.SDK_INT >= 31 ? 33554432 : 0));
            } catch (Exception unused) {
                Log.v(TAG, "Couldn't request permission for USB device " + device2);
                HIDDeviceOpenResult(i, false);
            }
            return false;
        }
    }

    public int sendOutputReport(int i, byte[] bArr) {
        try {
            HIDDevice device = getDevice(i);
            if (device != null) {
                return device.sendOutputReport(bArr);
            }
            HIDDeviceDisconnected(i);
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
            return -1;
        }
    }

    public int sendFeatureReport(int i, byte[] bArr) {
        try {
            HIDDevice device = getDevice(i);
            if (device != null) {
                return device.sendFeatureReport(bArr);
            }
            HIDDeviceDisconnected(i);
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
            return -1;
        }
    }

    public boolean getFeatureReport(int i, byte[] bArr) {
        try {
            HIDDevice device = getDevice(i);
            if (device != null) {
                return device.getFeatureReport(bArr);
            }
            HIDDeviceDisconnected(i);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
            return false;
        }
    }

    public void closeDevice(int i) {
        try {
            Log.v(TAG, "closeDevice deviceID=" + i);
            HIDDevice device = getDevice(i);
            if (device == null) {
                HIDDeviceDisconnected(i);
            } else {
                device.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
        }
    }
}
