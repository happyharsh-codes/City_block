package com.mkaygames.cityblock;

import org.libsdl.app.SDLActivity;

public class CityBlock extends SDLActivity {
    private static final String TAG = "CityBlock";

    /* access modifiers changed from: protected */
    public String[] getLibraries() {
        return new String[]{"SDL2", "openal", "main"};
    }

    public void onWindowFocusChanged(boolean z) {
        getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | 2 | 4096);
        super.onWindowFocusChanged(z);
    }
}
