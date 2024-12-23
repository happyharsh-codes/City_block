package org.libsdl.app;

import android.view.MotionEvent;
import android.view.View;

/* compiled from: SDLControllerManager */
class SDLGenericMotionListener_API24 extends SDLGenericMotionListener_API12 {
    private boolean mRelativeModeEnabled;

    public boolean supportsRelativeMouse() {
        return true;
    }

    SDLGenericMotionListener_API24() {
    }

    public boolean onGenericMotion(View view, MotionEvent motionEvent) {
        int actionMasked;
        if (!this.mRelativeModeEnabled || motionEvent.getSource() != 8194 || (actionMasked = motionEvent.getActionMasked()) != 7) {
            return super.onGenericMotion(view, motionEvent);
        }
        SDLActivity.onNativeMouse(0, actionMasked, motionEvent.getAxisValue(27), motionEvent.getAxisValue(28), true);
        return true;
    }

    public boolean inRelativeMode() {
        return this.mRelativeModeEnabled;
    }

    public boolean setRelativeMouseEnabled(boolean z) {
        this.mRelativeModeEnabled = z;
        return true;
    }

    public float getEventX(MotionEvent motionEvent) {
        if (this.mRelativeModeEnabled) {
            return motionEvent.getAxisValue(27);
        }
        return motionEvent.getX(0);
    }

    public float getEventY(MotionEvent motionEvent) {
        if (this.mRelativeModeEnabled) {
            return motionEvent.getAxisValue(28);
        }
        return motionEvent.getY(0);
    }
}
