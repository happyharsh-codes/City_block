package org.libsdl.app;

import android.content.Context;

public class SDL {
    protected static Context mContext;

    public static void setupJNI() {
        SDLActivity.nativeSetupJNI();
        SDLAudioManager.nativeSetupJNI();
        SDLControllerManager.nativeSetupJNI();
    }

    public static void initialize() {
        setContext((Context) null);
        SDLActivity.initialize();
        SDLAudioManager.initialize();
        SDLControllerManager.initialize();
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|9) */
    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0071, code lost:
        r11 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0072, code lost:
        throw r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0073, code lost:
        r11 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0074, code lost:
        throw r11;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x006d */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void loadLibrary(java.lang.String r11) throws java.lang.UnsatisfiedLinkError, java.lang.SecurityException, java.lang.NullPointerException {
        /*
            java.lang.String r0 = "No library name provided."
            java.util.Objects.requireNonNull(r11, r0)
            android.content.Context r0 = mContext     // Catch:{ all -> 0x006d }
            java.lang.ClassLoader r0 = r0.getClassLoader()     // Catch:{ all -> 0x006d }
            java.lang.String r1 = "com.getkeepsafe.relinker.ReLinker"
            java.lang.Class r0 = r0.loadClass(r1)     // Catch:{ all -> 0x006d }
            android.content.Context r1 = mContext     // Catch:{ all -> 0x006d }
            java.lang.ClassLoader r1 = r1.getClassLoader()     // Catch:{ all -> 0x006d }
            java.lang.String r2 = "com.getkeepsafe.relinker.ReLinker$LoadListener"
            java.lang.Class r1 = r1.loadClass(r2)     // Catch:{ all -> 0x006d }
            android.content.Context r2 = mContext     // Catch:{ all -> 0x006d }
            java.lang.ClassLoader r2 = r2.getClassLoader()     // Catch:{ all -> 0x006d }
            java.lang.String r3 = "android.content.Context"
            java.lang.Class r2 = r2.loadClass(r3)     // Catch:{ all -> 0x006d }
            android.content.Context r3 = mContext     // Catch:{ all -> 0x006d }
            java.lang.ClassLoader r3 = r3.getClassLoader()     // Catch:{ all -> 0x006d }
            java.lang.String r4 = "java.lang.String"
            java.lang.Class r3 = r3.loadClass(r4)     // Catch:{ all -> 0x006d }
            java.lang.String r4 = "force"
            r5 = 0
            java.lang.Class[] r6 = new java.lang.Class[r5]     // Catch:{ all -> 0x006d }
            java.lang.reflect.Method r0 = r0.getDeclaredMethod(r4, r6)     // Catch:{ all -> 0x006d }
            java.lang.Object[] r4 = new java.lang.Object[r5]     // Catch:{ all -> 0x006d }
            r6 = 0
            java.lang.Object r0 = r0.invoke(r6, r4)     // Catch:{ all -> 0x006d }
            java.lang.Class r4 = r0.getClass()     // Catch:{ all -> 0x006d }
            java.lang.String r7 = "loadLibrary"
            r8 = 4
            java.lang.Class[] r9 = new java.lang.Class[r8]     // Catch:{ all -> 0x006d }
            r9[r5] = r2     // Catch:{ all -> 0x006d }
            r2 = 1
            r9[r2] = r3     // Catch:{ all -> 0x006d }
            r10 = 2
            r9[r10] = r3     // Catch:{ all -> 0x006d }
            r3 = 3
            r9[r3] = r1     // Catch:{ all -> 0x006d }
            java.lang.reflect.Method r1 = r4.getDeclaredMethod(r7, r9)     // Catch:{ all -> 0x006d }
            java.lang.Object[] r4 = new java.lang.Object[r8]     // Catch:{ all -> 0x006d }
            android.content.Context r7 = mContext     // Catch:{ all -> 0x006d }
            r4[r5] = r7     // Catch:{ all -> 0x006d }
            r4[r2] = r11     // Catch:{ all -> 0x006d }
            r4[r10] = r6     // Catch:{ all -> 0x006d }
            r4[r3] = r6     // Catch:{ all -> 0x006d }
            r1.invoke(r0, r4)     // Catch:{ all -> 0x006d }
            goto L_0x0070
        L_0x006d:
            java.lang.System.loadLibrary(r11)     // Catch:{ UnsatisfiedLinkError -> 0x0073, SecurityException -> 0x0071 }
        L_0x0070:
            return
        L_0x0071:
            r11 = move-exception
            throw r11
        L_0x0073:
            r11 = move-exception
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.libsdl.app.SDL.loadLibrary(java.lang.String):void");
    }
}
