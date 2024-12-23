package org.libsdl.app;

import android.view.MotionEvent;
import android.view.View;

/* compiled from: SDLControllerManager */
class SDLGenericMotionListener_API12 implements View.OnGenericMotionListener {
    public boolean inRelativeMode() {
        return false;
    }

    public void reclaimRelativeMouseModeIfNeeded() {
    }

    public boolean setRelativeMouseEnabled(boolean z) {
        return false;
    }

    public boolean supportsRelativeMouse() {
        return false;
    }

    SDLGenericMotionListener_API12() {
    }

    public boolean onGenericMotion(View view, MotionEvent motionEvent) {
        int source = motionEvent.getSource();
        if (source == 8194) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 7) {
                SDLActivity.onNativeMouse(0, actionMasked, motionEvent.getX(0), motionEvent.getY(0), false);
                return true;
            } else if (actionMasked == 8) {
                SDLActivity.onNativeMouse(0, actionMasked, motionEvent.getAxisValue(10, 0), motionEvent.getAxisValue(9, 0), false);
                return true;
            }
        } else if (source == 16777232) {
            return SDLControllerManager.handleJoystickMotionEvent(motionEvent);
        }
        return false;
    }

    public float getEventX(MotionEvent motionEvent) {
        return motionEvent.getX(0);
    }

    public float getEventY(MotionEvent motionEvent) {
        return motionEvent.getY(0);
    }
}
