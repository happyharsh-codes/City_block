package org.libsdl.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import org.libsdl.app.SDLActivity;

/* compiled from: SDLActivity */
class SDLSurface extends SurfaceView implements SurfaceHolder.Callback, View.OnKeyListener, View.OnTouchListener, SensorEventListener {
    protected Display mDisplay;
    protected float mHeight = 1.0f;
    public boolean mIsSurfaceReady = false;
    protected SensorManager mSensorManager;
    protected float mWidth = 1.0f;

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public SDLSurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        this.mDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        this.mSensorManager = (SensorManager) context.getSystemService("sensor");
        setOnGenericMotionListener(SDLActivity.getMotionListener());
    }

    public void handlePause() {
        enableSensor(1, false);
    }

    public void handleResume() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        enableSensor(1, true);
    }

    public Surface getNativeSurface() {
        return getHolder().getSurface();
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.v("SDL", "surfaceCreated()");
        SDLActivity.onNativeSurfaceCreated();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.v("SDL", "surfaceDestroyed()");
        SDLActivity.mNextNativeState = SDLActivity.NativeState.PAUSED;
        SDLActivity.handleNativeState();
        this.mIsSurfaceReady = false;
        SDLActivity.onNativeSurfaceDestroyed();
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    public void surfaceChanged(android.view.SurfaceHolder r5, int r6, int r7, int r8) {
        /*
            r4 = this;
            java.lang.String r5 = "SDL"
            java.lang.String r6 = "surfaceChanged()"
            android.util.Log.v(r5, r6)
            org.libsdl.app.SDLActivity r5 = org.libsdl.app.SDLActivity.mSingleton
            if (r5 != 0) goto L_0x000c
            return
        L_0x000c:
            float r5 = (float) r7
            r4.mWidth = r5
            float r5 = (float) r8
            r4.mHeight = r5
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0027 }
            r6 = 17
            if (r5 < r6) goto L_0x0027
            android.util.DisplayMetrics r5 = new android.util.DisplayMetrics     // Catch:{ Exception -> 0x0027 }
            r5.<init>()     // Catch:{ Exception -> 0x0027 }
            android.view.Display r6 = r4.mDisplay     // Catch:{ Exception -> 0x0027 }
            r6.getRealMetrics(r5)     // Catch:{ Exception -> 0x0027 }
            int r6 = r5.widthPixels     // Catch:{ Exception -> 0x0027 }
            int r5 = r5.heightPixels     // Catch:{ Exception -> 0x0028 }
            goto L_0x0029
        L_0x0027:
            r6 = r7
        L_0x0028:
            r5 = r8
        L_0x0029:
            android.content.Context r0 = org.libsdl.app.SDLActivity.getContext()
            monitor-enter(r0)
            android.content.Context r1 = org.libsdl.app.SDLActivity.getContext()     // Catch:{ all -> 0x0102 }
            r1.notifyAll()     // Catch:{ all -> 0x0102 }
            monitor-exit(r0)     // Catch:{ all -> 0x0102 }
            java.lang.String r0 = "SDL"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Window size: "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r2 = "x"
            r1.append(r2)
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            android.util.Log.v(r0, r1)
            java.lang.String r0 = "SDL"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Device size: "
            r1.append(r2)
            r1.append(r6)
            java.lang.String r2 = "x"
            r1.append(r2)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            android.util.Log.v(r0, r1)
            android.view.Display r0 = r4.mDisplay
            float r0 = r0.getRefreshRate()
            org.libsdl.app.SDLActivity.nativeSetScreenResolution(r7, r8, r6, r5, r0)
            org.libsdl.app.SDLActivity.onNativeResize()
            org.libsdl.app.SDLActivity r5 = org.libsdl.app.SDLActivity.mSingleton
            int r5 = r5.getRequestedOrientation()
            r6 = 1
            r7 = 0
            if (r5 == r6) goto L_0x009a
            r8 = 7
            if (r5 != r8) goto L_0x008c
            goto L_0x009a
        L_0x008c:
            if (r5 == 0) goto L_0x0091
            r8 = 6
            if (r5 != r8) goto L_0x00a4
        L_0x0091:
            float r5 = r4.mWidth
            float r8 = r4.mHeight
            int r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r5 >= 0) goto L_0x00a4
            goto L_0x00a2
        L_0x009a:
            float r5 = r4.mWidth
            float r8 = r4.mHeight
            int r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r5 <= 0) goto L_0x00a4
        L_0x00a2:
            r5 = 1
            goto L_0x00a5
        L_0x00a4:
            r5 = 0
        L_0x00a5:
            if (r5 == 0) goto L_0x00d1
            float r8 = r4.mWidth
            float r0 = r4.mHeight
            float r8 = java.lang.Math.min(r8, r0)
            double r0 = (double) r8
            float r8 = r4.mWidth
            float r2 = r4.mHeight
            float r8 = java.lang.Math.max(r8, r2)
            double r2 = (double) r8
            java.lang.Double.isNaN(r2)
            java.lang.Double.isNaN(r0)
            double r2 = r2 / r0
            r0 = 4608083138725491507(0x3ff3333333333333, double:1.2)
            int r8 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r8 >= 0) goto L_0x00d1
            java.lang.String r5 = "SDL"
            java.lang.String r8 = "Don't skip on such aspect-ratio. Could be a square resolution."
            android.util.Log.v(r5, r8)
            r5 = 0
        L_0x00d1:
            if (r5 == 0) goto L_0x00e9
            int r8 = android.os.Build.VERSION.SDK_INT
            r0 = 24
            if (r8 < r0) goto L_0x00e9
            org.libsdl.app.SDLActivity r8 = org.libsdl.app.SDLActivity.mSingleton
            boolean r8 = r8.isInMultiWindowMode()
            if (r8 == 0) goto L_0x00e9
            java.lang.String r5 = "SDL"
            java.lang.String r8 = "Don't skip in Multi-Window"
            android.util.Log.v(r5, r8)
            r5 = 0
        L_0x00e9:
            if (r5 == 0) goto L_0x00f5
            java.lang.String r5 = "SDL"
            java.lang.String r6 = "Skip .. Surface is not ready."
            android.util.Log.v(r5, r6)
            r4.mIsSurfaceReady = r7
            return
        L_0x00f5:
            org.libsdl.app.SDLActivity.onNativeSurfaceChanged()
            r4.mIsSurfaceReady = r6
            org.libsdl.app.SDLActivity$NativeState r5 = org.libsdl.app.SDLActivity.NativeState.RESUMED
            org.libsdl.app.SDLActivity.mNextNativeState = r5
            org.libsdl.app.SDLActivity.handleNativeState()
            return
        L_0x0102:
            r5 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0102 }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.libsdl.app.SDLSurface.surfaceChanged(android.view.SurfaceHolder, int, int, int):void");
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        InputDevice device;
        int deviceId = keyEvent.getDeviceId();
        int source = keyEvent.getSource();
        if (source == 0 && (device = InputDevice.getDevice(deviceId)) != null) {
            source = device.getSources();
        }
        if (SDLControllerManager.isDeviceSDLJoystick(deviceId)) {
            if (keyEvent.getAction() == 0) {
                if (SDLControllerManager.onNativePadDown(deviceId, i) == 0) {
                    return true;
                }
            } else if (keyEvent.getAction() == 1 && SDLControllerManager.onNativePadUp(deviceId, i) == 0) {
                return true;
            }
        }
        if ((source & 257) == 257) {
            if (keyEvent.getAction() == 0) {
                if (SDLActivity.isTextInputEvent(keyEvent)) {
                    SDLInputConnection.nativeCommitText(String.valueOf((char) keyEvent.getUnicodeChar()), 1);
                }
                SDLActivity.onNativeKeyDown(i);
                return true;
            } else if (keyEvent.getAction() == 1) {
                SDLActivity.onNativeKeyUp(i);
                return true;
            }
        }
        if ((source & 8194) != 8194) {
            return false;
        }
        if (i != 4 && i != 125) {
            return false;
        }
        int action = keyEvent.getAction();
        if (action == 0 || action == 1) {
            return true;
        }
        return false;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        int i;
        int deviceId = motionEvent.getDeviceId();
        int pointerCount = motionEvent.getPointerCount();
        int actionMasked = motionEvent.getActionMasked();
        if (deviceId < 0) {
            deviceId--;
        }
        int i2 = 0;
        if (motionEvent.getSource() == 8194 || motionEvent.getSource() == 12290) {
            try {
                Object invoke = motionEvent.getClass().getMethod("getButtonState", new Class[0]).invoke(motionEvent, new Object[0]);
                if (invoke != null) {
                    i = ((Integer) invoke).intValue();
                    SDLGenericMotionListener_API12 motionListener = SDLActivity.getMotionListener();
                    SDLActivity.onNativeMouse(i, actionMasked, motionListener.getEventX(motionEvent), motionListener.getEventY(motionEvent), motionListener.inRelativeMode());
                }
            } catch (Exception unused) {
            }
            i = 1;
            SDLGenericMotionListener_API12 motionListener2 = SDLActivity.getMotionListener();
            SDLActivity.onNativeMouse(i, actionMasked, motionListener2.getEventX(motionEvent), motionListener2.getEventY(motionEvent), motionListener2.inRelativeMode());
        } else {
            if (!(actionMasked == 0 || actionMasked == 1)) {
                if (actionMasked == 2) {
                    for (int i3 = 0; i3 < pointerCount; i3++) {
                        int pointerId = motionEvent.getPointerId(i3);
                        float x = motionEvent.getX(i3) / this.mWidth;
                        float y = motionEvent.getY(i3) / this.mHeight;
                        float pressure = motionEvent.getPressure(i3);
                        SDLActivity.onNativeTouch(deviceId, pointerId, actionMasked, x, y, pressure > 1.0f ? 1.0f : pressure);
                    }
                } else if (actionMasked == 3) {
                    for (int i4 = 0; i4 < pointerCount; i4++) {
                        int pointerId2 = motionEvent.getPointerId(i4);
                        float x2 = motionEvent.getX(i4) / this.mWidth;
                        float y2 = motionEvent.getY(i4) / this.mHeight;
                        float pressure2 = motionEvent.getPressure(i4);
                        SDLActivity.onNativeTouch(deviceId, pointerId2, 1, x2, y2, pressure2 > 1.0f ? 1.0f : pressure2);
                    }
                } else if (actionMasked == 5 || actionMasked == 6) {
                    i2 = -1;
                }
            }
            if (i2 == -1) {
                i2 = motionEvent.getActionIndex();
            }
            int pointerId3 = motionEvent.getPointerId(i2);
            float x3 = motionEvent.getX(i2) / this.mWidth;
            float y3 = motionEvent.getY(i2) / this.mHeight;
            float pressure3 = motionEvent.getPressure(i2);
            SDLActivity.onNativeTouch(deviceId, pointerId3, actionMasked, x3, y3, pressure3 > 1.0f ? 1.0f : pressure3);
        }
        return true;
    }

    public void enableSensor(int i, boolean z) {
        if (z) {
            SensorManager sensorManager = this.mSensorManager;
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(i), 1, (Handler) null);
            return;
        }
        SensorManager sensorManager2 = this.mSensorManager;
        sensorManager2.unregisterListener(this, sensorManager2.getDefaultSensor(i));
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        float f;
        float f2;
        int i = 1;
        if (sensorEvent.sensor.getType() == 1) {
            int rotation = this.mDisplay.getRotation();
            if (rotation == 1) {
                f2 = -sensorEvent.values[1];
                f = sensorEvent.values[0];
            } else if (rotation == 2) {
                f2 = -sensorEvent.values[0];
                f = -sensorEvent.values[1];
                i = 4;
            } else if (rotation != 3) {
                f2 = sensorEvent.values[0];
                f = sensorEvent.values[1];
                i = 3;
            } else {
                f2 = sensorEvent.values[1];
                f = -sensorEvent.values[0];
                i = 2;
            }
            if (i != SDLActivity.mCurrentOrientation) {
                SDLActivity.mCurrentOrientation = i;
                SDLActivity.onNativeOrientationChanged(i);
            }
            SDLActivity.onNativeAccel((-f2) / 9.80665f, f / 9.80665f, sensorEvent.values[2] / 9.80665f);
        }
    }

    public boolean onCapturedPointerEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 2 || actionMasked == 7) {
            SDLActivity.onNativeMouse(0, actionMasked, motionEvent.getX(0), motionEvent.getY(0), true);
            return true;
        } else if (actionMasked == 8) {
            SDLActivity.onNativeMouse(0, actionMasked, motionEvent.getAxisValue(10, 0), motionEvent.getAxisValue(9, 0), false);
            return true;
        } else if (actionMasked != 11 && actionMasked != 12) {
            return false;
        } else {
            SDLActivity.onNativeMouse(motionEvent.getButtonState(), actionMasked == 11 ? 0 : 1, motionEvent.getX(0), motionEvent.getY(0), true);
            return true;
        }
    }
}
