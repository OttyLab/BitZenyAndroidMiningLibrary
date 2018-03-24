package com.example.ottylab.bitzenyminer;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
                String logs = Utils.rotateStringQueue(activity.logs, msg.getData().getString("log"));
                activity.textViewLog.setText(logs);
            }
        }
    }

    private static JNICallbackHandler sHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    Log.d("Java", "stop");
                    miner.stopMining();
                } else {
                    Log.d("Java", "start");
                    int n_threads = 0;
                    try {
                        n_threads = Integer.parseInt(editTextNThreads.getText().toString());
                    } catch (NumberFormatException e){}

                    if (checkBoxBenchmark.isChecked()) {
                        miner.startBenchmark(n_threads);
                    } else {
                        miner.startMining(
                            editTextServer.getText().toString(),
                            editTextUser.getText().toString(),
                            editTextPassword.getText().toString(),
                            n_threads);
                    }
                }

                changeState(!running);
                storeSetting();
            }
        });

        checkBoxBenchmark = (CheckBox) findViewById(R.id.checkBoxBenchmark);

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
    }

    private void storeSetting() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("server", editTextServer.getText().toString());
        editor.putString("user", editTextUser.getText().toString());
        editor.putString("password", editTextPassword.getText().toString());
        editor.putString("n_threads", editTextNThreads.getText().toString());
        editor.commit();
    }

    private void restoreSetting() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        editTextServer.setText(pref.getString("server", null));
        editTextUser.setText(pref.getString("user", null));
        editTextPassword.setText(pref.getString("password", null));
        editTextNThreads.setText(pref.getString("n_threads", null));
    }
}
