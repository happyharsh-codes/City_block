package org.libsdl.app;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

/* compiled from: SDLControllerManager */
class SDLGenericMotionListener_API26 extends SDLGenericMotionListener_API24 {
    private boolean mRelativeModeEnabled;

    SDLGenericMotionListener_API26() {
    }

    public boolean onGenericMotion(View view, MotionEvent motionEvent) {
        int source = motionEvent.getSource();
        if (source == 8194 || source == 12290) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 7) {
                SDLActivity.onNativeMouse(0, actionMasked, motionEvent.getX(0), motionEvent.getY(0), false);
                return true;
            } else if (actionMasked == 8) {
                SDLActivity.onNativeMouse(0, actionMasked, motionEvent.getAxisValue(10, 0), motionEvent.getAxisValue(9, 0), false);
                return true;
            }
        } else if (source == 131076) {
            int actionMasked2 = motionEvent.getActionMasked();
            if (actionMasked2 == 7) {
                SDLActivity.onNativeMouse(0, actionMasked2, motionEvent.getX(0), motionEvent.getY(0), true);
                return true;
            } else if (actionMasked2 == 8) {
                SDLActivity.onNativeMouse(0, actionMasked2, motionEvent.getAxisValue(10, 0), motionEvent.getAxisValue(9, 0), false);
                return true;
            }
        } else if (source == 16777232) {
            return SDLControllerManager.handleJoystickMotionEvent(motionEvent);
        }
        return false;
    }

    public boolean supportsRelativeMouse() {
        return !SDLActivity.isDeXMode() || Build.VERSION.SDK_INT >= 27;
    }

    public boolean inRelativeMode() {
        return this.mRelativeModeEnabled;
    }

    public boolean setRelativeMouseEnabled(boolean z) {
        if (SDLActivity.isDeXMode() && Build.VERSION.SDK_INT < 27) {
            return false;
        }
        if (z) {
            SDLActivity.getContentView().requestPointerCapture();
        } else {
            SDLActivity.getContentView().releasePointerCapture();
        }
        this.mRelativeModeEnabled = z;
        return true;
    }

    public void reclaimRelativeMouseModeIfNeeded() {
        if (this.mRelativeModeEnabled && !SDLActivity.isDeXMode()) {
            SDLActivity.getContentView().requestPointerCapture();
        }
    }

    public float getEventX(MotionEvent motionEvent) {
        return motionEvent.getX(0);
    }

    public float getEventY(MotionEvent motionEvent) {
        return motionEvent.getY(0);
    }
}
