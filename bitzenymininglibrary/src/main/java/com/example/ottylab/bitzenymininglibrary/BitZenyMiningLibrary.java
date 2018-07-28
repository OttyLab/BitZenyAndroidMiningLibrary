package com.example.ottylab.bitzenymininglibrary;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class BitZenyMiningLibrary {
    private static final String TAG = "BitZenyMiningLibrary";

    static {
        System.loadLibrary("bitzenymininglibrary");
    }

    public enum Algorithm {
        YESCRYPT,
        YESPOWER,
    }

    private static Handler sHandler;

    public BitZenyMiningLibrary() {}
    public BitZenyMiningLibrary(Handler handler) {
        sHandler = handler;
    }
    public int startMining(String url, String user, String password, int n_threads, Algorithm algo) {
        switch (algo) {
            case YESCRYPT:
                return startMining(url, user, password, n_threads, 0);
            case YESPOWER:
                return startMining(url, user, password, n_threads, 1);
            default:
                return -1;
        }
    }

    public int startBenchmark(int n_threads, Algorithm algo) {
        switch (algo) {
            case YESCRYPT:
                return startBenchmark(n_threads, 0);
            case YESPOWER:
                return startBenchmark(n_threads, 1);
            default:
                return -1;
        }
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
    public native int stopMining();
    private native int startMining(String url, String user, String password, int n_threads, int algo);
    private native int startBenchmark(int n_threads, int algo);
}