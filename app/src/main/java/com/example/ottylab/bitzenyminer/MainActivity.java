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
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BitZenyMiner";
    private static final int LOG_LINES = 1000;

    private EditText editTextServer;
    private EditText editTextUser;
    private EditText editTextPassword;
    private Button buttonDrive;
    private TextView textViewLog;

    private boolean running;
    private BlockingQueue<String> logs = new LinkedBlockingQueue<>(LOG_LINES);

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static class JNICallbackHandler extends Handler {
        private final WeakReference<MainActivity> activity;

        public JNICallbackHandler(MainActivity activity) {
            this.activity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = this.activity.get();
            if (activity != null) {
                String logs = Utils.rotateStringQueue(activity.logs, msg.getData().getString("msg"));
                activity.textViewLog.setText(logs.toString());
            }
        }
    }

    private static JNICallbackHandler sHandler;

    public static void updateUI(String message) {
        // Generate message
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("msg", message);
        msg.setData(bundle);

        sHandler.sendMessage(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sHandler = new JNICallbackHandler(this);

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

        buttonDrive = (Button) findViewById(R.id.buttonDrive);
        buttonDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (running) {
                    Log.d("Java", "stop");
                    stopMining();
                } else {
                    Log.d("Java", "start");
                    startMining(
                        editTextServer.getText().toString(),
                        editTextUser.getText().toString(),
                        editTextPassword.getText().toString());
                }

                changeState(!running);
                storeSetting();
            }
        });

        textViewLog = (TextView) findViewById(R.id.textViewLog);
        textViewLog.setMovementMethod(new ScrollingMovementMethod());

        restoreSetting();
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
    }

    private void storeSetting() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("server", editTextServer.getText().toString());
        editor.putString("user", editTextUser.getText().toString());
        editor.putString("password", editTextPassword.getText().toString());
        editor.commit();
    }

    private void restoreSetting() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        editTextServer.setText(pref.getString("server", null));
        editTextUser.setText(pref.getString("user", null));
        editTextPassword.setText(pref.getString("password", null));
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native int startMining(String url, String user, String password);
    public native int stopMining();
}
