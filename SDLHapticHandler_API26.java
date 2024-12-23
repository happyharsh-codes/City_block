package org.libsdl.app;

import android.os.VibrationEffect;
import android.util.Log;
import org.libsdl.app.SDLHapticHandler;

/* compiled from: SDLControllerManager */
class SDLHapticHandler_API26 extends SDLHapticHandler {
    SDLHapticHandler_API26() {
    }

    public void run(int i, float f, int i2) {
        SDLHapticHandler.SDLHaptic haptic = getHaptic(i);
        if (haptic != null) {
            Log.d("SDL", "Rtest: Vibe with intensity " + f + " for " + i2);
            if (f == 0.0f) {
                stop(i);
                return;
            }
            int round = Math.round(f * 255.0f);
            if (round > 255) {
                round = 255;
            }
            if (round < 1) {
                stop(i);
                return;
            }
            try {
                haptic.vib.vibrate(VibrationEffect.createOneShot((long) i2, round));
            } catch (Exception unused) {
                haptic.vib.vibrate((long) i2);
            }
        }
    }
}
