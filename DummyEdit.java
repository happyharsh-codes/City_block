package org.libsdl.app;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/* compiled from: SDLActivity */
class DummyEdit extends View implements View.OnKeyListener {
    InputConnection ic;

    public boolean onCheckIsTextEditor() {
        return true;
    }

    public DummyEdit(Context context) {
        super(context);
        setFocusableInTouchMode(true);
        setFocusable(true);
        setOnKeyListener(this);
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0) {
            if (SDLActivity.isTextInputEvent(keyEvent)) {
                this.ic.commitText(String.valueOf((char) keyEvent.getUnicodeChar()), 1);
                return true;
            }
            SDLActivity.onNativeKeyDown(i);
            return true;
        } else if (keyEvent.getAction() != 1) {
            return false;
        } else {
            SDLActivity.onNativeKeyUp(i);
            return true;
        }
    }

    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 1 && i == 4 && SDLActivity.mTextEdit != null && SDLActivity.mTextEdit.getVisibility() == 0) {
            SDLActivity.onNativeKeyboardFocusLost();
        }
        return super.onKeyPreIme(i, keyEvent);
    }

    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        this.ic = new SDLInputConnection(this, true);
        editorInfo.inputType = 1;
        editorInfo.imeOptions = 301989888;
        return this.ic;
    }
}
