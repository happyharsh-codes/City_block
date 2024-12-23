package org.libsdl.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.PointerIcon;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Hashtable;
import java.util.Locale;

public class SDLActivity extends Activity implements View.OnSystemUiVisibilityChangeListener {
    static final int COMMAND_CHANGE_TITLE = 1;
    static final int COMMAND_CHANGE_WINDOW_STYLE = 2;
    static final int COMMAND_SET_KEEP_SCREEN_ON = 5;
    static final int COMMAND_TEXTEDIT_HIDE = 3;
    protected static final int COMMAND_USER = 32768;
    protected static final int SDL_ORIENTATION_LANDSCAPE = 1;
    protected static final int SDL_ORIENTATION_LANDSCAPE_FLIPPED = 2;
    protected static final int SDL_ORIENTATION_PORTRAIT = 3;
    protected static final int SDL_ORIENTATION_PORTRAIT_FLIPPED = 4;
    protected static final int SDL_ORIENTATION_UNKNOWN = 0;
    private static final int SDL_SYSTEM_CURSOR_ARROW = 0;
    private static final int SDL_SYSTEM_CURSOR_CROSSHAIR = 3;
    private static final int SDL_SYSTEM_CURSOR_HAND = 11;
    private static final int SDL_SYSTEM_CURSOR_IBEAM = 1;
    private static final int SDL_SYSTEM_CURSOR_NO = 10;
    private static final int SDL_SYSTEM_CURSOR_SIZEALL = 9;
    private static final int SDL_SYSTEM_CURSOR_SIZENESW = 6;
    private static final int SDL_SYSTEM_CURSOR_SIZENS = 8;
    private static final int SDL_SYSTEM_CURSOR_SIZENWSE = 5;
    private static final int SDL_SYSTEM_CURSOR_SIZEWE = 7;
    private static final int SDL_SYSTEM_CURSOR_WAIT = 2;
    private static final int SDL_SYSTEM_CURSOR_WAITARROW = 4;
    private static final String TAG = "SDL";
    public static boolean mBrokenLibraries = true;
    protected static SDLClipboardHandler mClipboardHandler;
    protected static Locale mCurrentLocale;
    public static NativeState mCurrentNativeState;
    protected static int mCurrentOrientation;
    protected static Hashtable<Integer, PointerIcon> mCursors;
    protected static boolean mFullscreenModeActive;
    protected static HIDDeviceManager mHIDDeviceManager;
    public static boolean mHasFocus;
    public static final boolean mHasMultiWindow = (Build.VERSION.SDK_INT >= 24);
    public static boolean mIsResumedCalled;
    protected static int mLastCursorID;
    protected static ViewGroup mLayout;
    protected static SDLGenericMotionListener_API12 mMotionListener;
    public static NativeState mNextNativeState;
    protected static Thread mSDLThread;
    protected static boolean mScreenKeyboardShown;
    protected static SDLActivity mSingleton;
    protected static SDLSurface mSurface;
    protected static View mTextEdit;
    Handler commandHandler = new SDLCommandHandler();
    protected final int[] messageboxSelection = new int[1];
    private final Runnable rehideSystemUi = new Runnable() {
        public void run() {
            if (Build.VERSION.SDK_INT >= 19) {
                SDLActivity.this.getWindow().getDecorView().setSystemUiVisibility(5894);
            }
        }
    };

    public enum NativeState {
        INIT,
        RESUMED,
        PAUSED
    }

    public static native void nativeAddTouch(int i, String str);

    public static native void nativeFocusChanged(boolean z);

    public static native String nativeGetHint(String str);

    public static native boolean nativeGetHintBoolean(String str, boolean z);

    public static native void nativeLowMemory();

    public static native void nativePause();

    public static native void nativePermissionResult(int i, boolean z);

    public static native void nativeQuit();

    public static native void nativeResume();

    public static native int nativeRunMain(String str, String str2, Object obj);

    public static native void nativeSendQuit();

    public static native void nativeSetScreenResolution(int i, int i2, int i3, int i4, float f);

    public static native void nativeSetenv(String str, String str2);

    public static native int nativeSetupJNI();

    public static native void onNativeAccel(float f, float f2, float f3);

    public static native void onNativeClipboardChanged();

    public static native void onNativeDropFile(String str);

    public static native void onNativeKeyDown(int i);

    public static native void onNativeKeyUp(int i);

    public static native void onNativeKeyboardFocusLost();

    public static native void onNativeLocaleChanged();

    public static native void onNativeMouse(int i, int i2, float f, float f2, boolean z);

    public static native void onNativeOrientationChanged(int i);

    public static native void onNativeResize();

    public static native boolean onNativeSoftReturnKey();

    public static native void onNativeSurfaceChanged();

    public static native void onNativeSurfaceCreated();

    public static native void onNativeSurfaceDestroyed();

    public static native void onNativeTouch(int i, int i2, int i3, float f, float f2, float f3);

    public static boolean shouldMinimizeOnFocusLoss() {
        return false;
    }

    /* access modifiers changed from: protected */
    public String[] getArguments() {
        return new String[0];
    }

    /* access modifiers changed from: protected */
    public String getMainFunction() {
        return "SDL_main";
    }

    /* access modifiers changed from: protected */
    public boolean onUnhandledMessage(int i, Object obj) {
        return false;
    }

    protected static SDLGenericMotionListener_API12 getMotionListener() {
        if (mMotionListener == null) {
            if (Build.VERSION.SDK_INT >= 26) {
                mMotionListener = new SDLGenericMotionListener_API26();
            } else if (Build.VERSION.SDK_INT >= 24) {
                mMotionListener = new SDLGenericMotionListener_API24();
            } else {
                mMotionListener = new SDLGenericMotionListener_API12();
            }
        }
        return mMotionListener;
    }

    /* access modifiers changed from: protected */
    public String getMainSharedObject() {
        String str;
        String[] libraries = mSingleton.getLibraries();
        if (libraries.length > 0) {
            str = "lib" + libraries[libraries.length - 1] + ".so";
        } else {
            str = "libmain.so";
        }
        return getContext().getApplicationInfo().nativeLibraryDir + "/" + str;
    }

    /* access modifiers changed from: protected */
    public String[] getLibraries() {
        return new String[]{"SDL2", "main"};
    }

    public void loadLibraries() {
        for (String loadLibrary : getLibraries()) {
            SDL.loadLibrary(loadLibrary);
        }
    }

    public static void initialize() {
        mSingleton = null;
        mSurface = null;
        mTextEdit = null;
        mLayout = null;
        mClipboardHandler = null;
        mCursors = new Hashtable<>();
        mLastCursorID = 0;
        mSDLThread = null;
        mIsResumedCalled = false;
        mHasFocus = true;
        mNextNativeState = NativeState.INIT;
        mCurrentNativeState = NativeState.INIT;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        String str;
        String path;
        Log.v(TAG, "Device: " + Build.DEVICE);
        Log.v(TAG, "Model: " + Build.MODEL);
        Log.v(TAG, "onCreate()");
        super.onCreate(bundle);
        try {
            Thread.currentThread().setName("SDLActivity");
        } catch (Exception e) {
            Log.v(TAG, "modify thread properties failed " + e.toString());
        }
        try {
            loadLibraries();
            mBrokenLibraries = false;
            str = "";
        } catch (UnsatisfiedLinkError e2) {
            System.err.println(e2.getMessage());
            mBrokenLibraries = true;
            str = e2.getMessage();
        } catch (Exception e3) {
            System.err.println(e3.getMessage());
            mBrokenLibraries = true;
            str = e3.getMessage();
        }
        if (mBrokenLibraries) {
            mSingleton = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("An error occurred while trying to start the application. Please try again and/or reinstall." + System.getProperty("line.separator") + System.getProperty("line.separator") + "Error: " + str);
            builder.setTitle("SDL Error");
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    SDLActivity.mSingleton.finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();
            return;
        }
        SDL.setupJNI();
        SDL.initialize();
        mSingleton = this;
        SDL.setContext(this);
        mClipboardHandler = new SDLClipboardHandler();
        mHIDDeviceManager = HIDDeviceManager.acquire(this);
        mSurface = new SDLSurface(getApplication());
        RelativeLayout relativeLayout = new RelativeLayout(this);
        mLayout = relativeLayout;
        relativeLayout.addView(mSurface);
        int currentOrientation = getCurrentOrientation();
        mCurrentOrientation = currentOrientation;
        onNativeOrientationChanged(currentOrientation);
        try {
            if (Build.VERSION.SDK_INT < 24) {
                mCurrentLocale = getContext().getResources().getConfiguration().locale;
            } else {
                mCurrentLocale = getContext().getResources().getConfiguration().getLocales().get(0);
            }
        } catch (Exception unused) {
        }
        setContentView(mLayout);
        setWindowStyle(false);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && (path = intent.getData().getPath()) != null) {
            Log.v(TAG, "Got filename: " + path);
            onNativeDropFile(path);
        }
    }

    /* access modifiers changed from: protected */
    public void pauseNativeThread() {
        mNextNativeState = NativeState.PAUSED;
        mIsResumedCalled = false;
        if (!mBrokenLibraries) {
            handleNativeState();
        }
    }

    /* access modifiers changed from: protected */
    public void resumeNativeThread() {
        mNextNativeState = NativeState.RESUMED;
        mIsResumedCalled = true;
        if (!mBrokenLibraries) {
            handleNativeState();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        Log.v(TAG, "onPause()");
        super.onPause();
        HIDDeviceManager hIDDeviceManager = mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            hIDDeviceManager.setFrozen(true);
        }
        if (!mHasMultiWindow) {
            pauseNativeThread();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Log.v(TAG, "onResume()");
        super.onResume();
        HIDDeviceManager hIDDeviceManager = mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            hIDDeviceManager.setFrozen(false);
        }
        if (!mHasMultiWindow) {
            resumeNativeThread();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        Log.v(TAG, "onStop()");
        super.onStop();
        if (mHasMultiWindow) {
            pauseNativeThread();
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        Log.v(TAG, "onStart()");
        super.onStart();
        if (mHasMultiWindow) {
            resumeNativeThread();
        }
    }

    public static int getCurrentOrientation() {
        Activity activity = (Activity) getContext();
        if (activity == null) {
            return 0;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        if (rotation == 0) {
            return 3;
        }
        if (rotation == 1) {
            return 1;
        }
        if (rotation == 2) {
            return 4;
        }
        if (rotation != 3) {
            return 0;
        }
        return 2;
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        Log.v(TAG, "onWindowFocusChanged(): " + z);
        if (!mBrokenLibraries) {
            mHasFocus = z;
            if (z) {
                mNextNativeState = NativeState.RESUMED;
                getMotionListener().reclaimRelativeMouseModeIfNeeded();
                handleNativeState();
                nativeFocusChanged(true);
                return;
            }
            nativeFocusChanged(false);
            if (!mHasMultiWindow) {
                mNextNativeState = NativeState.PAUSED;
                handleNativeState();
            }
        }
    }

    public void onLowMemory() {
        Log.v(TAG, "onLowMemory()");
        super.onLowMemory();
        if (!mBrokenLibraries) {
            nativeLowMemory();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        Log.v(TAG, "onConfigurationChanged()");
        super.onConfigurationChanged(configuration);
        if (!mBrokenLibraries) {
            Locale locale = mCurrentLocale;
            if (locale == null || !locale.equals(configuration.locale)) {
                mCurrentLocale = configuration.locale;
                onNativeLocaleChanged();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        Log.v(TAG, "onDestroy()");
        HIDDeviceManager hIDDeviceManager = mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            HIDDeviceManager.release(hIDDeviceManager);
            mHIDDeviceManager = null;
        }
        if (mBrokenLibraries) {
            super.onDestroy();
            return;
        }
        if (mSDLThread != null) {
            nativeSendQuit();
            try {
                mSDLThread.join();
            } catch (Exception e) {
                Log.v(TAG, "Problem stopping SDLThread: " + e);
            }
        }
        nativeQuit();
        super.onDestroy();
    }

    public void onBackPressed() {
        if (!nativeGetHintBoolean("SDL_ANDROID_TRAP_BACK_BUTTON", false) && !isFinishing()) {
            super.onBackPressed();
        }
    }

    public static void manualBackButton() {
        mSingleton.pressBackButton();
    }

    public void pressBackButton() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (!SDLActivity.this.isFinishing()) {
                    SDLActivity.this.superOnBackPressed();
                }
            }
        });
    }

    public void superOnBackPressed() {
        super.onBackPressed();
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int keyCode;
        if (mBrokenLibraries || (keyCode = keyEvent.getKeyCode()) == 25 || keyCode == 24 || keyCode == 27 || keyCode == 168 || keyCode == 169) {
            return false;
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public static void handleNativeState() {
        NativeState nativeState = mNextNativeState;
        if (nativeState != mCurrentNativeState) {
            if (nativeState == NativeState.INIT) {
                mCurrentNativeState = mNextNativeState;
            } else if (mNextNativeState == NativeState.PAUSED) {
                if (mSDLThread != null) {
                    nativePause();
                }
                SDLSurface sDLSurface = mSurface;
                if (sDLSurface != null) {
                    sDLSurface.handlePause();
                }
                mCurrentNativeState = mNextNativeState;
            } else if (mNextNativeState == NativeState.RESUMED && mSurface.mIsSurfaceReady && mHasFocus && mIsResumedCalled) {
                if (mSDLThread == null) {
                    mSDLThread = new Thread(new SDLMain(), "SDLThread");
                    mSurface.enableSensor(1, true);
                    mSDLThread.start();
                } else {
                    nativeResume();
                }
                mSurface.handleResume();
                mCurrentNativeState = mNextNativeState;
            }
        }
    }

    protected static class SDLCommandHandler extends Handler {
        protected SDLCommandHandler() {
        }

        public void handleMessage(Message message) {
            Window window;
            Context context = SDL.getContext();
            if (context == null) {
                Log.e(SDLActivity.TAG, "error handling message, getContext() returned null");
                return;
            }
            int i = message.arg1;
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 5) {
                            if ((context instanceof SDLActivity) && !((SDLActivity) context).onUnhandledMessage(message.arg1, message.obj)) {
                                Log.e(SDLActivity.TAG, "error handling message, command is " + message.arg1);
                            }
                        } else if ((context instanceof Activity) && (window = ((Activity) context).getWindow()) != null) {
                            if (!(message.obj instanceof Integer) || ((Integer) message.obj).intValue() == 0) {
                                window.clearFlags(128);
                            } else {
                                window.addFlags(128);
                            }
                        }
                    } else if (SDLActivity.mTextEdit != null) {
                        SDLActivity.mTextEdit.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(SDLActivity.mTextEdit.getWindowToken(), 0);
                        SDLActivity.mScreenKeyboardShown = false;
                        SDLActivity.mSurface.requestFocus();
                    }
                } else if (Build.VERSION.SDK_INT < 19) {
                } else {
                    if (context instanceof Activity) {
                        Window window2 = ((Activity) context).getWindow();
                        if (window2 == null) {
                            return;
                        }
                        if (!(message.obj instanceof Integer) || ((Integer) message.obj).intValue() == 0) {
                            window2.getDecorView().setSystemUiVisibility(256);
                            window2.addFlags(2048);
                            window2.clearFlags(1024);
                            SDLActivity.mFullscreenModeActive = false;
                            return;
                        }
                        window2.getDecorView().setSystemUiVisibility(5894);
                        window2.addFlags(1024);
                        window2.clearFlags(2048);
                        SDLActivity.mFullscreenModeActive = true;
                        return;
                    }
                    Log.e(SDLActivity.TAG, "error handling message, getContext() returned no Activity");
                }
            } else if (context instanceof Activity) {
                ((Activity) context).setTitle((String) message.obj);
            } else {
                Log.e(SDLActivity.TAG, "error handling message, getContext() returned no Activity");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean sendCommand(int i, Object obj) {
        Message obtainMessage = this.commandHandler.obtainMessage();
        obtainMessage.arg1 = i;
        obtainMessage.obj = obj;
        boolean sendMessage = this.commandHandler.sendMessage(obtainMessage);
        if (Build.VERSION.SDK_INT >= 19 && i == 2) {
            boolean z = false;
            if (obj instanceof Integer) {
                Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                defaultDisplay.getRealMetrics(displayMetrics);
                if (displayMetrics.widthPixels == mSurface.getWidth() && displayMetrics.heightPixels == mSurface.getHeight()) {
                    z = true;
                }
                if (((Integer) obj).intValue() == 1) {
                    z = !z;
                }
            }
            if (z && getContext() != null) {
                synchronized (getContext()) {
                    try {
                        getContext().wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sendMessage;
    }

    public static boolean setActivityTitle(String str) {
        return mSingleton.sendCommand(1, str);
    }

    public static void setWindowStyle(boolean z) {
        mSingleton.sendCommand(2, Integer.valueOf(z ? 1 : 0));
    }

    public static void setOrientation(int i, int i2, boolean z, String str) {
        SDLActivity sDLActivity = mSingleton;
        if (sDLActivity != null) {
            sDLActivity.setOrientationBis(i, i2, z, str);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x006b, code lost:
        if (r3 != false) goto L_0x0079;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setOrientationBis(int r10, int r11, boolean r12, java.lang.String r13) {
        /*
            r9 = this;
            java.lang.String r0 = "LandscapeRight"
            boolean r1 = r13.contains(r0)
            r2 = 6
            java.lang.String r3 = "LandscapeLeft"
            r4 = 0
            r5 = -1
            if (r1 == 0) goto L_0x0015
            boolean r1 = r13.contains(r3)
            if (r1 == 0) goto L_0x0015
            r0 = 6
            goto L_0x0027
        L_0x0015:
            boolean r0 = r13.contains(r0)
            if (r0 == 0) goto L_0x001d
            r0 = 0
            goto L_0x0027
        L_0x001d:
            boolean r0 = r13.contains(r3)
            if (r0 == 0) goto L_0x0026
            r0 = 8
            goto L_0x0027
        L_0x0026:
            r0 = -1
        L_0x0027:
            java.lang.String r1 = "Portrait"
            boolean r3 = r13.contains(r1)
            r6 = 7
            java.lang.String r7 = "PortraitUpsideDown"
            r8 = 1
            if (r3 == 0) goto L_0x003b
            boolean r3 = r13.contains(r7)
            if (r3 == 0) goto L_0x003b
            r1 = 7
            goto L_0x004d
        L_0x003b:
            boolean r1 = r13.contains(r1)
            if (r1 == 0) goto L_0x0043
            r1 = 1
            goto L_0x004d
        L_0x0043:
            boolean r1 = r13.contains(r7)
            if (r1 == 0) goto L_0x004c
            r1 = 9
            goto L_0x004d
        L_0x004c:
            r1 = -1
        L_0x004d:
            if (r0 == r5) goto L_0x0051
            r3 = 1
            goto L_0x0052
        L_0x0051:
            r3 = 0
        L_0x0052:
            if (r1 == r5) goto L_0x0055
            r4 = 1
        L_0x0055:
            r5 = 10
            if (r4 != 0) goto L_0x0064
            if (r3 != 0) goto L_0x0064
            if (r12 == 0) goto L_0x005e
            goto L_0x007a
        L_0x005e:
            if (r10 <= r11) goto L_0x0061
            goto L_0x0062
        L_0x0061:
            r2 = 7
        L_0x0062:
            r5 = r2
            goto L_0x007a
        L_0x0064:
            if (r12 == 0) goto L_0x006e
            if (r4 == 0) goto L_0x006b
            if (r3 == 0) goto L_0x006b
            goto L_0x007a
        L_0x006b:
            if (r3 == 0) goto L_0x0078
            goto L_0x0079
        L_0x006e:
            if (r4 == 0) goto L_0x0075
            if (r3 == 0) goto L_0x0075
            if (r10 <= r11) goto L_0x0078
            goto L_0x0079
        L_0x0075:
            if (r3 == 0) goto L_0x0078
            goto L_0x0079
        L_0x0078:
            r0 = r1
        L_0x0079:
            r5 = r0
        L_0x007a:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "setOrientation() requestedOrientation="
            r0.append(r1)
            r0.append(r5)
            java.lang.String r1 = " width="
            r0.append(r1)
            r0.append(r10)
            java.lang.String r10 = " height="
            r0.append(r10)
            r0.append(r11)
            java.lang.String r10 = " resizable="
            r0.append(r10)
            r0.append(r12)
            java.lang.String r10 = " hint="
            r0.append(r10)
            r0.append(r13)
            java.lang.String r10 = r0.toString()
            java.lang.String r11 = "SDL"
            android.util.Log.v(r11, r10)
            org.libsdl.app.SDLActivity r10 = mSingleton
            r10.setRequestedOrientation(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.libsdl.app.SDLActivity.setOrientationBis(int, int, boolean, java.lang.String):void");
    }

    public static void minimizeWindow() {
        if (mSingleton != null) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.setFlags(268435456);
            mSingleton.startActivity(intent);
        }
    }

    public static boolean isScreenKeyboardShown() {
        if (mTextEdit != null && mScreenKeyboardShown) {
            return ((InputMethodManager) SDL.getContext().getSystemService("input_method")).isAcceptingText();
        }
        return false;
    }

    public static boolean supportsRelativeMouse() {
        if (Build.VERSION.SDK_INT >= 27 || !isDeXMode()) {
            return getMotionListener().supportsRelativeMouse();
        }
        return false;
    }

    public static boolean setRelativeMouseEnabled(boolean z) {
        if (!z || supportsRelativeMouse()) {
            return getMotionListener().setRelativeMouseEnabled(z);
        }
        return false;
    }

    public static boolean sendMessage(int i, int i2) {
        SDLActivity sDLActivity = mSingleton;
        if (sDLActivity == null) {
            return false;
        }
        return sDLActivity.sendCommand(i, Integer.valueOf(i2));
    }

    public static Context getContext() {
        return SDL.getContext();
    }

    public static boolean isAndroidTV() {
        if (((UiModeManager) getContext().getSystemService("uimode")).getCurrentModeType() == 4) {
            return true;
        }
        if (Build.MANUFACTURER.equals("MINIX") && Build.MODEL.equals("NEO-U1")) {
            return true;
        }
        if (Build.MANUFACTURER.equals("Amlogic") && Build.MODEL.equals("X96-W")) {
            return true;
        }
        if (!Build.MANUFACTURER.equals("Amlogic") || !Build.MODEL.startsWith("TV")) {
            return false;
        }
        return true;
    }

    public static double getDiagonal() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Activity activity = (Activity) getContext();
        if (activity == null) {
            return 0.0d;
        }
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        double d = (double) displayMetrics.widthPixels;
        double d2 = (double) displayMetrics.xdpi;
        Double.isNaN(d);
        Double.isNaN(d2);
        double d3 = d / d2;
        double d4 = (double) displayMetrics.heightPixels;
        double d5 = (double) displayMetrics.ydpi;
        Double.isNaN(d4);
        Double.isNaN(d5);
        double d6 = d4 / d5;
        return Math.sqrt((d3 * d3) + (d6 * d6));
    }

    public static boolean isTablet() {
        return getDiagonal() >= 7.0d;
    }

    public static boolean isChromebook() {
        if (getContext() == null) {
            return false;
        }
        return getContext().getPackageManager().hasSystemFeature("org.chromium.arc.device_management");
    }

    public static boolean isDeXMode() {
        if (Build.VERSION.SDK_INT < 24) {
            return false;
        }
        try {
            Configuration configuration = getContext().getResources().getConfiguration();
            Class<?> cls = configuration.getClass();
            if (cls.getField("SEM_DESKTOP_MODE_ENABLED").getInt(cls) == cls.getField("semDesktopModeEnabled").getInt(configuration)) {
                return true;
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }

    public static DisplayMetrics getDisplayDPI() {
        return getContext().getResources().getDisplayMetrics();
    }

    public static boolean getManifestEnvironmentVariables() {
        Bundle bundle;
        try {
            if (getContext() == null || (bundle = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), 128).metaData) == null) {
                return false;
            }
            for (String str : bundle.keySet()) {
                if (str.startsWith("SDL_ENV.")) {
                    nativeSetenv(str.substring(SDL_SYSTEM_CURSOR_SIZENS), bundle.get(str).toString());
                }
            }
            return true;
        } catch (Exception e) {
            Log.v(TAG, "exception " + e.toString());
            return false;
        }
    }

    public static View getContentView() {
        return mLayout;
    }

    static class ShowTextInputTask implements Runnable {
        static final int HEIGHT_PADDING = 15;
        public int h;
        public int w;
        public int x;
        public int y;

        public ShowTextInputTask(int i, int i2, int i3, int i4) {
            this.x = i;
            this.y = i2;
            this.w = i3;
            this.h = i4;
            if (i3 <= 0) {
                this.w = 1;
            }
            if (i4 + HEIGHT_PADDING <= 0) {
                this.h = -14;
            }
        }

        public void run() {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(this.w, this.h + HEIGHT_PADDING);
            layoutParams.leftMargin = this.x;
            layoutParams.topMargin = this.y;
            if (SDLActivity.mTextEdit == null) {
                SDLActivity.mTextEdit = new DummyEdit(SDL.getContext());
                SDLActivity.mLayout.addView(SDLActivity.mTextEdit, layoutParams);
            } else {
                SDLActivity.mTextEdit.setLayoutParams(layoutParams);
            }
            SDLActivity.mTextEdit.setVisibility(0);
            SDLActivity.mTextEdit.requestFocus();
            ((InputMethodManager) SDL.getContext().getSystemService("input_method")).showSoftInput(SDLActivity.mTextEdit, 0);
            SDLActivity.mScreenKeyboardShown = true;
        }
    }

    public static boolean showTextInput(int i, int i2, int i3, int i4) {
        return mSingleton.commandHandler.post(new ShowTextInputTask(i, i2, i3, i4));
    }

    public static boolean isTextInputEvent(KeyEvent keyEvent) {
        if (keyEvent.isCtrlPressed()) {
            return false;
        }
        if (keyEvent.isPrintingKey() || keyEvent.getKeyCode() == 62) {
            return true;
        }
        return false;
    }

    public static Surface getNativeSurface() {
        SDLSurface sDLSurface = mSurface;
        if (sDLSurface == null) {
            return null;
        }
        return sDLSurface.getNativeSurface();
    }

    public static void initTouch() {
        for (int device : InputDevice.getDeviceIds()) {
            InputDevice device2 = InputDevice.getDevice(device);
            if (device2 != null && ((device2.getSources() & 4098) == 4098 || device2.isVirtual())) {
                int id = device2.getId();
                if (id < 0) {
                    id--;
                }
                nativeAddTouch(id, device2.getName());
            }
        }
    }

    public int messageboxShowMessageBox(int i, String str, String str2, int[] iArr, int[] iArr2, String[] strArr, int[] iArr3) {
        this.messageboxSelection[0] = -1;
        if (iArr.length != iArr2.length && iArr2.length != strArr.length) {
            return -1;
        }
        final Bundle bundle = new Bundle();
        bundle.putInt("flags", i);
        bundle.putString("title", str);
        bundle.putString("message", str2);
        bundle.putIntArray("buttonFlags", iArr);
        bundle.putIntArray("buttonIds", iArr2);
        bundle.putStringArray("buttonTexts", strArr);
        bundle.putIntArray("colors", iArr3);
        runOnUiThread(new Runnable() {
            public void run() {
                SDLActivity.this.messageboxCreateAndShow(bundle);
            }
        });
        synchronized (this.messageboxSelection) {
            try {
                this.messageboxSelection.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return this.messageboxSelection[0];
    }

    /* access modifiers changed from: protected */
    public void messageboxCreateAndShow(Bundle bundle) {
        int i;
        int i2;
        int i3;
        Bundle bundle2 = bundle;
        int[] intArray = bundle2.getIntArray("colors");
        if (intArray != null) {
            i3 = intArray[0];
            i2 = intArray[1];
            int i4 = intArray[2];
            i = intArray[3];
            int i5 = intArray[4];
        } else {
            i3 = 0;
            i2 = 0;
            i = 0;
        }
        final AlertDialog create = new AlertDialog.Builder(this).create();
        create.setTitle(bundle2.getString("title"));
        create.setCancelable(false);
        create.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                synchronized (SDLActivity.this.messageboxSelection) {
                    SDLActivity.this.messageboxSelection.notify();
                }
            }
        });
        TextView textView = new TextView(this);
        textView.setGravity(17);
        textView.setText(bundle2.getString("message"));
        if (i2 != 0) {
            textView.setTextColor(i2);
        }
        int[] intArray2 = bundle2.getIntArray("buttonFlags");
        int[] intArray3 = bundle2.getIntArray("buttonIds");
        String[] stringArray = bundle2.getStringArray("buttonTexts");
        final SparseArray sparseArray = new SparseArray();
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(17);
        for (int i6 = 0; i6 < stringArray.length; i6++) {
            Button button = new Button(this);
            final int i7 = intArray3[i6];
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    SDLActivity.this.messageboxSelection[0] = i7;
                    create.dismiss();
                }
            });
            if (intArray2[i6] != 0) {
                if ((intArray2[i6] & 1) != 0) {
                    sparseArray.put(66, button);
                }
                if ((intArray2[i6] & 2) != 0) {
                    sparseArray.put(111, button);
                }
            }
            button.setText(stringArray[i6]);
            if (i2 != 0) {
                button.setTextColor(i2);
            }
            if (i != 0) {
                Drawable background = button.getBackground();
                if (background == null) {
                    button.setBackgroundColor(i);
                } else {
                    background.setColorFilter(i, PorterDuff.Mode.MULTIPLY);
                }
            }
            linearLayout.addView(button);
        }
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(1);
        linearLayout2.addView(textView);
        linearLayout2.addView(linearLayout);
        if (i3 != 0) {
            linearLayout2.setBackgroundColor(i3);
        }
        create.setView(linearLayout2);
        create.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                Button button = (Button) sparseArray.get(i);
                if (button == null) {
                    return false;
                }
                if (keyEvent.getAction() == 1) {
                    button.performClick();
                }
                return true;
            }
        });
        create.show();
    }

    public void onSystemUiVisibilityChange(int i) {
        Handler handler;
        if (!mFullscreenModeActive) {
            return;
        }
        if (((i & 4) == 0 || (i & 2) == 0) && (handler = getWindow().getDecorView().getHandler()) != null) {
            handler.removeCallbacks(this.rehideSystemUi);
            handler.postDelayed(this.rehideSystemUi, 2000);
        }
    }

    public static boolean clipboardHasText() {
        return mClipboardHandler.clipboardHasText();
    }

    public static String clipboardGetText() {
        return mClipboardHandler.clipboardGetText();
    }

    public static void clipboardSetText(String str) {
        mClipboardHandler.clipboardSetText(str);
    }

    public static int createCustomCursor(int[] iArr, int i, int i2, int i3, int i4) {
        Bitmap createBitmap = Bitmap.createBitmap(iArr, i, i2, Bitmap.Config.ARGB_8888);
        mLastCursorID++;
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                mCursors.put(Integer.valueOf(mLastCursorID), PointerIcon.create(createBitmap, (float) i3, (float) i4));
                return mLastCursorID;
            } catch (Exception unused) {
            }
        }
        return 0;
    }

    public static void destroyCustomCursor(int i) {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                mCursors.remove(Integer.valueOf(i));
            } catch (Exception unused) {
            }
        }
    }

    public static boolean setCustomCursor(int i) {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                mSurface.setPointerIcon(mCursors.get(Integer.valueOf(i)));
                return true;
            } catch (Exception unused) {
            }
        }
        return false;
    }

    public static boolean setSystemCursor(int i) {
        int i2 = 1004;
        switch (i) {
            case 0:
                i2 = 1000;
                break;
            case 1:
                i2 = 1008;
                break;
            case 2:
            case 4:
                break;
            case 3:
                i2 = 1007;
                break;
            case 5:
                i2 = 1017;
                break;
            case SDL_SYSTEM_CURSOR_SIZENESW /*6*/:
                i2 = 1016;
                break;
            case SDL_SYSTEM_CURSOR_SIZEWE /*7*/:
                i2 = 1014;
                break;
            case SDL_SYSTEM_CURSOR_SIZENS /*8*/:
                i2 = 1015;
                break;
            case SDL_SYSTEM_CURSOR_SIZEALL /*9*/:
                i2 = 1020;
                break;
            case SDL_SYSTEM_CURSOR_NO /*10*/:
                i2 = 1012;
                break;
            case SDL_SYSTEM_CURSOR_HAND /*11*/:
                i2 = 1002;
                break;
            default:
                i2 = 0;
                break;
        }
        if (Build.VERSION.SDK_INT < 24) {
            return true;
        }
        try {
            mSurface.setPointerIcon(PointerIcon.getSystemIcon(SDL.getContext(), i2));
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static void requestPermission(String str, int i) {
        if (Build.VERSION.SDK_INT < 23) {
            nativePermissionResult(i, true);
            return;
        }
        Activity activity = (Activity) getContext();
        if (activity.checkSelfPermission(str) != 0) {
            activity.requestPermissions(new String[]{str}, i);
            return;
        }
        nativePermissionResult(i, true);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        boolean z = false;
        if (iArr.length > 0 && iArr[0] == 0) {
            z = true;
        }
        nativePermissionResult(i, z);
    }

    public static int openURL(String str) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(str));
            int i = Build.VERSION.SDK_INT;
            intent.addFlags(1208483840);
            mSingleton.startActivity(intent);
            return 0;
        } catch (Exception unused) {
            return -1;
        }
    }

    public static int showToast(String str, int i, int i2, int i3, int i4) {
        SDLActivity sDLActivity = mSingleton;
        if (sDLActivity == null) {
            return -1;
        }
        try {
            sDLActivity.runOnUiThread(new Runnable(str, i, i2, i3, i4) {
                int mDuration;
                int mGravity;
                String mMessage;
                int mXOffset;
                int mYOffset;

                {
                    this.mMessage = r1;
                    this.mDuration = r2;
                    this.mGravity = r3;
                    this.mXOffset = r4;
                    this.mYOffset = r5;
                }

                public void run() {
                    try {
                        Toast makeText = Toast.makeText(SDLActivity.mSingleton, this.mMessage, this.mDuration);
                        int i = this.mGravity;
                        if (i >= 0) {
                            makeText.setGravity(i, this.mXOffset, this.mYOffset);
                        }
                        makeText.show();
                    } catch (Exception e) {
                        Log.e(SDLActivity.TAG, e.getMessage());
                    }
                }
            });
            return 0;
        } catch (Exception unused) {
            return -1;
        }
    }
}
