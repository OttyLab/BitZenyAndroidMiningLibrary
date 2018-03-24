package com.example.ottylab.bitzenymininglibrary;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class BitZenyMiningLibrary {
    private static final String TAG = "BitZenyMiningLibrary";

    static {
        System.loadLibrary("bitzenymininglibrary");
    }

    private static Handler sHandler;

    public BitZenyMiningLibrary() {}
    public BitZenyMiningLibrary(Handler handler) {
        sHandler = handler;
    }

    private static void output(String message) {
        // Generate message
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("log", message);
        msg.setData(bundle);

        if (sHandler != null) {
            sHandler.sendMessage(msg);
        }
    }

    public native boolean isMiningRunning();
    public native int startMining(String url, String user, String password, int n_threads);
    public native int startBenchmark(int n_threads);
    public native int stopMining();
}