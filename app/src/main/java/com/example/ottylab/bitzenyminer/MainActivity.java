package com.example.ottylab.bitzenyminer;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ottylab.bitzenymininglibrary.BitZenyMiningLibrary;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BitZenyMiner";
    private static final int LOG_LINES = 1000;

    private BitZenyMiningLibrary miner;

    private EditText editTextServer;
    private EditText editTextUser;
    private EditText editTextPassword;
    private EditText editTextNThreads;
    private Button buttonDrive;
    private CheckBox checkBoxBenchmark;
    private Spinner spinnerAlgorithm;
    private TextView textViewLog;

    private boolean running;
    private BlockingQueue<String> logs = new LinkedBlockingQueue<>(LOG_LINES);

    private static class JNICallbackHandler extends Handler {
        private final WeakReference<MainActivity> activity;

        public JNICallbackHandler(MainActivity activity) {
            this.activity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = this.activity.get();
            if (activity != null) {
                String log = msg.getData().getString("log");
                String logs = Utils.rotateStringQueue(activity.logs, log);
                activity.textViewLog.setText(logs);
                Log.d(TAG, log);
            }
        }
    }

    private static JNICallbackHandler sHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showDeviceInfo();

        sHandler = new JNICallbackHandler(this);
        miner = new BitZenyMiningLibrary(sHandler);

        editTextServer = (EditText) findViewById(R.id.editTextServer);
        editTextServer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                storeSetting();
            }
        });

        editTextUser = (EditText) findViewById(R.id.editTextUser);
        editTextUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                storeSetting();
            }
        });

        editTextPassword= (EditText) findViewById(R.id.editTextPassword);
        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                storeSetting();
            }
        });

        editTextNThreads = (EditText) findViewById(R.id.editTextNThreads);
        editTextNThreads.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                storeSetting();
            }
        });

        buttonDrive = (Button) findViewById(R.id.buttonDrive);
        buttonDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (running) {
                    Log.d(TAG, "Stop");
                    miner.stopMining();
                } else {
                    Log.d(TAG, "Start");
                    int n_threads = 0;
                    try {
                        n_threads = Integer.parseInt(editTextNThreads.getText().toString());
                    } catch (NumberFormatException e){}

                    BitZenyMiningLibrary.Algorithm algorithm =
                            spinnerAlgorithm.getSelectedItemPosition() == 0 ?
                                    BitZenyMiningLibrary.Algorithm.YESCRYPT : BitZenyMiningLibrary.Algorithm.YESPOWER;
                    if (checkBoxBenchmark.isChecked()) {
                        miner.startBenchmark(n_threads, algorithm);
                    } else {
                        miner.startMining(
                            editTextServer.getText().toString(),
                            editTextUser.getText().toString(),
                            editTextPassword.getText().toString(),
                            n_threads,
                            algorithm);
                    }
                }

                changeState(!running);
                storeSetting();
            }
        });

        checkBoxBenchmark = (CheckBox) findViewById(R.id.checkBoxBenchmark);
        spinnerAlgorithm = (Spinner) findViewById(R.id.spinnerAlgorithm);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.algorithms, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlgorithm.setAdapter(adapter);

        textViewLog = (TextView) findViewById(R.id.textViewLog);
        textViewLog.setMovementMethod(new ScrollingMovementMethod());

        restoreSetting();
        changeState(miner.isMiningRunning());
    }

    private void changeState(boolean running) {
        buttonDrive.setText(running ? "Stop" : "Start");
        disableSetting(running);
        this.running = running;
    }

    private void disableSetting(boolean running) {
        editTextServer.setEnabled(!running);
        editTextUser.setEnabled(!running);
        editTextPassword.setEnabled(!running);
        editTextNThreads.setEnabled(!running);
        spinnerAlgorithm.setEnabled(!running);
    }

    private void storeSetting() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("server", editTextServer.getText().toString());
        editor.putString("user", editTextUser.getText().toString());
        editor.putString("password", editTextPassword.getText().toString());
        editor.putString("n_threads", editTextNThreads.getText().toString());
        editor.putInt("algorithm", spinnerAlgorithm.getSelectedItemPosition());
        editor.commit();
    }

    private void restoreSetting() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        editTextServer.setText(pref.getString("server", null));
        editTextUser.setText(pref.getString("user", null));
        editTextPassword.setText(pref.getString("password", null));
        editTextNThreads.setText(pref.getString("n_threads", null));
        spinnerAlgorithm.setSelection(pref.getInt("algorithm", 0));
    }

    private void showDeviceInfo() {
        String[] keys = new String[]{ "os.arch", "os.name", "os.version" };
        for (String key : keys) {
            Log.d(TAG, key + ": " + System.getProperty(key));
        }
        Log.d(TAG, "CODE NAME: " + Build.VERSION.CODENAME);
        Log.d(TAG, "SDK INT: " + Build.VERSION.SDK_INT);
        Log.d(TAG, "MANUFACTURER: " + Build.MANUFACTURER);
        Log.d(TAG, "MODEL: " + Build.MODEL);
        Log.d(TAG, "PRODUCT: " + Build.PRODUCT);
    }
}
