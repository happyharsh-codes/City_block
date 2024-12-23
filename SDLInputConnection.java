package org.libsdl.app;

import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;

/* compiled from: SDLActivity */
class SDLInputConnection extends BaseInputConnection {
    public static native void nativeCommitText(String str, int i);

    public native void nativeGenerateScancodeForUnichar(char c);

    public native void nativeSetComposingText(String str, int i);

    public SDLInputConnection(View view, boolean z) {
        super(view, z);
    }

    public boolean sendKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() != 66 || !SDLActivity.onNativeSoftReturnKey()) {
            return super.sendKeyEvent(keyEvent);
        }
        return true;
    }

    public boolean commitText(CharSequence charSequence, int i) {
        Editable editable = getEditable();
        if (editable != null) {
            int composingSpanStart = getComposingSpanStart(editable);
            int composingSpanEnd = getComposingSpanEnd(editable);
            if (composingSpanStart == -1 || composingSpanEnd == -1) {
                composingSpanStart = Selection.getSelectionStart(editable);
                composingSpanEnd = Selection.getSelectionEnd(editable);
            }
            if (composingSpanStart < 0) {
                composingSpanStart = 0;
            }
            if (composingSpanEnd < 0) {
                composingSpanEnd = 0;
            }
            if (composingSpanEnd >= composingSpanStart) {
                int i2 = composingSpanEnd;
                composingSpanEnd = composingSpanStart;
                composingSpanStart = i2;
            }
            int i3 = composingSpanStart - composingSpanEnd;
            for (int i4 = 0; i4 < i3; i4++) {
                nativeGenerateScancodeForUnichar(8);
            }
        }
        for (int i5 = 0; i5 < charSequence.length(); i5++) {
            char charAt = charSequence.charAt(i5);
            if (charAt == 10 && SDLActivity.onNativeSoftReturnKey()) {
                return true;
            }
            nativeGenerateScancodeForUnichar(charAt);
        }
        nativeCommitText(charSequence.toString(), i);
        return super.commitText(charSequence, i);
    }

    public boolean setComposingText(CharSequence charSequence, int i) {
        nativeSetComposingText(charSequence.toString(), i);
        return super.setComposingText(charSequence, i);
    }

    public boolean deleteSurroundingText(int i, int i2) {
        if (i <= 0 || i2 != 0) {
            return super.deleteSurroundingText(i, i2);
        }
        while (true) {
            int i3 = i - 1;
            if (i <= 0) {
                return true;
            }
            nativeGenerateScancodeForUnichar(8);
            i = i3;
        }
    }
}
