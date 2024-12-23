package org.libsdl.app;

import android.os.Process;
import android.util.Log;

/* compiled from: SDLActivity */
class SDLMain implements Runnable {
    SDLMain() {
    }

    public void run() {
        String mainSharedObject = SDLActivity.mSingleton.getMainSharedObject();
        String mainFunction = SDLActivity.mSingleton.getMainFunction();
        String[] arguments = SDLActivity.mSingleton.getArguments();
        try {
            Process.setThreadPriority(-4);
        } catch (Exception e) {
            Log.v("SDL", "modify thread properties failed " + e.toString());
        }
        Log.v("SDL", "Running main function " + mainFunction + " from library " + mainSharedObject);
        SDLActivity.nativeRunMain(mainSharedObject, mainFunction, arguments);
        Log.v("SDL", "Finished main function");
        if (SDLActivity.mSingleton != null && !SDLActivity.mSingleton.isFinishing()) {
            SDLActivity.mSDLThread = null;
            SDLActivity.mSingleton.finish();
        }
    }
}
