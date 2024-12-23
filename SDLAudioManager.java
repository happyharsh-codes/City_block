package org.libsdl.app;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Process;
import android.util.Log;

public class SDLAudioManager {
    protected static final String TAG = "SDLAudio";
    protected static AudioRecord mAudioRecord;
    protected static AudioTrack mAudioTrack;

    public static native int nativeSetupJNI();

    public static void initialize() {
        mAudioTrack = null;
        mAudioRecord = null;
    }

    protected static String getAudioFormatString(int i) {
        if (i == 2) {
            return "16-bit";
        }
        if (i != 3) {
            return i != 4 ? Integer.toString(i) : "float";
        }
        return "8-bit";
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00cf  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0122  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0185  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static int[] open(boolean r24, int r25, int r26, int r27, int r28) {
        /*
            r0 = r25
            r1 = r27
            r2 = r28
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Opening "
            r3.append(r4)
            java.lang.String r5 = "capture"
            java.lang.String r6 = "playback"
            if (r24 == 0) goto L_0x0018
            r7 = r5
            goto L_0x0019
        L_0x0018:
            r7 = r6
        L_0x0019:
            r3.append(r7)
            java.lang.String r7 = ", requested "
            r3.append(r7)
            r3.append(r2)
            java.lang.String r7 = " frames of "
            r3.append(r7)
            r3.append(r1)
            java.lang.String r8 = " channel "
            r3.append(r8)
            java.lang.String r9 = getAudioFormatString(r26)
            r3.append(r9)
            java.lang.String r9 = " audio at "
            r3.append(r9)
            r3.append(r0)
            java.lang.String r10 = " Hz"
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            java.lang.String r11 = "SDLAudio"
            android.util.Log.v(r11, r3)
            int r3 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            r13 = 2
            if (r3 >= r12) goto L_0x0058
            if (r1 <= r13) goto L_0x0058
            r1 = 2
        L_0x0058:
            int r3 = android.os.Build.VERSION.SDK_INT
            r14 = 22
            r15 = 48000(0xbb80, float:6.7262E-41)
            r12 = 8000(0x1f40, float:1.121E-41)
            if (r3 >= r14) goto L_0x006c
            if (r0 >= r12) goto L_0x0066
            goto L_0x006d
        L_0x0066:
            if (r0 <= r15) goto L_0x006c
            r12 = 48000(0xbb80, float:6.7262E-41)
            goto L_0x006d
        L_0x006c:
            r12 = r0
        L_0x006d:
            r3 = 4
            r14 = r26
            if (r14 != r3) goto L_0x007e
            if (r24 == 0) goto L_0x0077
            r15 = 23
            goto L_0x0079
        L_0x0077:
            r15 = 21
        L_0x0079:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 >= r15) goto L_0x007e
            r14 = 2
        L_0x007e:
            r0 = 3
            r15 = 1
            if (r14 == r13) goto L_0x00a6
            if (r14 == r0) goto L_0x00a4
            if (r14 == r3) goto L_0x00a2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Requested format "
            r0.append(r3)
            r0.append(r14)
            java.lang.String r3 = ", getting ENCODING_PCM_16BIT"
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r11, r0)
            r0 = 2
            r14 = 2
            goto L_0x00a7
        L_0x00a2:
            r0 = 4
            goto L_0x00a7
        L_0x00a4:
            r0 = 1
            goto L_0x00a7
        L_0x00a6:
            r0 = 2
        L_0x00a7:
            java.lang.String r3 = " channels, getting stereo"
            r16 = 252(0xfc, float:3.53E-43)
            java.lang.String r13 = "Requested "
            r17 = 12
            if (r24 == 0) goto L_0x00cf
            if (r1 == r15) goto L_0x00cc
            r15 = 2
            if (r1 == r15) goto L_0x011a
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r13)
            r15.append(r1)
            r15.append(r3)
            java.lang.String r1 = r15.toString()
            android.util.Log.v(r11, r1)
            goto L_0x00e7
        L_0x00cc:
            r3 = 16
            goto L_0x011e
        L_0x00cf:
            switch(r1) {
                case 1: goto L_0x011d;
                case 2: goto L_0x011a;
                case 3: goto L_0x0117;
                case 4: goto L_0x0114;
                case 5: goto L_0x0111;
                case 6: goto L_0x010e;
                case 7: goto L_0x010b;
                case 8: goto L_0x00e9;
                default: goto L_0x00d2;
            }
        L_0x00d2:
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r13)
            r15.append(r1)
            r15.append(r3)
            java.lang.String r1 = r15.toString()
            android.util.Log.v(r11, r1)
        L_0x00e7:
            r1 = 2
            goto L_0x011a
        L_0x00e9:
            int r3 = android.os.Build.VERSION.SDK_INT
            r15 = 23
            if (r3 < r15) goto L_0x00f2
            r3 = 6396(0x18fc, float:8.963E-42)
            goto L_0x011e
        L_0x00f2:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r13)
            r3.append(r1)
            java.lang.String r1 = " channels, getting 5.1 surround"
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            android.util.Log.v(r11, r1)
            r1 = 6
            goto L_0x010e
        L_0x010b:
            r3 = 1276(0x4fc, float:1.788E-42)
            goto L_0x011e
        L_0x010e:
            r3 = 252(0xfc, float:3.53E-43)
            goto L_0x011e
        L_0x0111:
            r3 = 220(0xdc, float:3.08E-43)
            goto L_0x011e
        L_0x0114:
            r3 = 204(0xcc, float:2.86E-43)
            goto L_0x011e
        L_0x0117:
            r3 = 28
            goto L_0x011e
        L_0x011a:
            r3 = 12
            goto L_0x011e
        L_0x011d:
            r3 = 4
        L_0x011e:
            int r0 = r0 * r1
            if (r24 == 0) goto L_0x0127
            int r1 = android.media.AudioRecord.getMinBufferSize(r12, r3, r14)
            goto L_0x012b
        L_0x0127:
            int r1 = android.media.AudioTrack.getMinBufferSize(r12, r3, r14)
        L_0x012b:
            int r1 = r1 + r0
            r13 = 1
            int r1 = r1 - r13
            int r1 = r1 / r0
            int r1 = java.lang.Math.max(r2, r1)
            r2 = 4
            int[] r2 = new int[r2]
            r13 = 0
            r15 = 0
            if (r24 == 0) goto L_0x0185
            android.media.AudioRecord r16 = mAudioRecord
            if (r16 != 0) goto L_0x016a
            android.media.AudioRecord r16 = new android.media.AudioRecord
            r18 = 0
            int r22 = r1 * r0
            r17 = r16
            r19 = r12
            r20 = r3
            r21 = r14
            r17.<init>(r18, r19, r20, r21, r22)
            mAudioRecord = r16
            int r0 = r16.getState()
            r3 = 1
            if (r0 == r3) goto L_0x0165
            java.lang.String r0 = "Failed during initialization of AudioRecord"
            android.util.Log.e(r11, r0)
            android.media.AudioRecord r0 = mAudioRecord
            r0.release()
            mAudioRecord = r15
            return r15
        L_0x0165:
            android.media.AudioRecord r0 = mAudioRecord
            r0.startRecording()
        L_0x016a:
            android.media.AudioRecord r0 = mAudioRecord
            int r0 = r0.getSampleRate()
            r2[r13] = r0
            android.media.AudioRecord r0 = mAudioRecord
            int r0 = r0.getAudioFormat()
            r3 = 1
            r2[r3] = r0
            android.media.AudioRecord r0 = mAudioRecord
            int r0 = r0.getChannelCount()
            r3 = 2
            r2[r3] = r0
            goto L_0x01d1
        L_0x0185:
            android.media.AudioTrack r16 = mAudioTrack
            if (r16 != 0) goto L_0x01b7
            android.media.AudioTrack r16 = new android.media.AudioTrack
            r18 = 3
            int r22 = r1 * r0
            r23 = 1
            r17 = r16
            r19 = r12
            r20 = r3
            r21 = r14
            r17.<init>(r18, r19, r20, r21, r22, r23)
            mAudioTrack = r16
            int r0 = r16.getState()
            r3 = 1
            if (r0 == r3) goto L_0x01b2
            java.lang.String r0 = "Failed during initialization of Audio Track"
            android.util.Log.e(r11, r0)
            android.media.AudioTrack r0 = mAudioTrack
            r0.release()
            mAudioTrack = r15
            return r15
        L_0x01b2:
            android.media.AudioTrack r0 = mAudioTrack
            r0.play()
        L_0x01b7:
            android.media.AudioTrack r0 = mAudioTrack
            int r0 = r0.getSampleRate()
            r2[r13] = r0
            android.media.AudioTrack r0 = mAudioTrack
            int r0 = r0.getAudioFormat()
            r3 = 1
            r2[r3] = r0
            android.media.AudioTrack r0 = mAudioTrack
            int r0 = r0.getChannelCount()
            r3 = 2
            r2[r3] = r0
        L_0x01d1:
            r0 = 3
            r2[r0] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            if (r24 == 0) goto L_0x01df
            goto L_0x01e0
        L_0x01df:
            r5 = r6
        L_0x01e0:
            r1.append(r5)
            java.lang.String r3 = ", got "
            r1.append(r3)
            r0 = r2[r0]
            r1.append(r0)
            r1.append(r7)
            r0 = 2
            r0 = r2[r0]
            r1.append(r0)
            r1.append(r8)
            r0 = 1
            r0 = r2[r0]
            java.lang.String r0 = getAudioFormatString(r0)
            r1.append(r0)
            r1.append(r9)
            r0 = r2[r13]
            r1.append(r0)
            r1.append(r10)
            java.lang.String r0 = r1.toString()
            android.util.Log.v(r11, r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.libsdl.app.SDLAudioManager.open(boolean, int, int, int, int):int[]");
    }

    public static int[] audioOpen(int i, int i2, int i3, int i4) {
        return open(false, i, i2, i3, i4);
    }

    public static void audioWriteFloatBuffer(float[] fArr) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < fArr.length) {
            int write = mAudioTrack.write(fArr, i, fArr.length - i, 0);
            if (write > 0) {
                i += write;
            } else if (write == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException unused) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(float)");
                return;
            }
        }
    }

    public static void audioWriteShortBuffer(short[] sArr) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < sArr.length) {
            int write = mAudioTrack.write(sArr, i, sArr.length - i);
            if (write > 0) {
                i += write;
            } else if (write == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException unused) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(short)");
                return;
            }
        }
    }

    public static void audioWriteByteBuffer(byte[] bArr) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < bArr.length) {
            int write = mAudioTrack.write(bArr, i, bArr.length - i);
            if (write > 0) {
                i += write;
            } else if (write == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException unused) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(byte)");
                return;
            }
        }
    }

    public static int[] captureOpen(int i, int i2, int i3, int i4) {
        return open(true, i, i2, i3, i4);
    }

    public static int captureReadFloatBuffer(float[] fArr, boolean z) {
        return mAudioRecord.read(fArr, 0, fArr.length, z ^ true ? 1 : 0);
    }

    public static int captureReadShortBuffer(short[] sArr, boolean z) {
        if (Build.VERSION.SDK_INT < 23) {
            return mAudioRecord.read(sArr, 0, sArr.length);
        }
        return mAudioRecord.read(sArr, 0, sArr.length, z ^ true ? 1 : 0);
    }

    public static int captureReadByteBuffer(byte[] bArr, boolean z) {
        if (Build.VERSION.SDK_INT < 23) {
            return mAudioRecord.read(bArr, 0, bArr.length);
        }
        return mAudioRecord.read(bArr, 0, bArr.length, z ^ true ? 1 : 0);
    }

    public static void audioClose() {
        AudioTrack audioTrack = mAudioTrack;
        if (audioTrack != null) {
            audioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public static void captureClose() {
        AudioRecord audioRecord = mAudioRecord;
        if (audioRecord != null) {
            audioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    public static void audioSetThreadPriority(boolean z, int i) {
        if (z) {
            try {
                Thread currentThread = Thread.currentThread();
                currentThread.setName("SDLAudioC" + i);
            } catch (Exception e) {
                Log.v(TAG, "modify thread properties failed " + e.toString());
                return;
            }
        } else {
            Thread currentThread2 = Thread.currentThread();
            currentThread2.setName("SDLAudioP" + i);
        }
        Process.setThreadPriority(-16);
    }
}
