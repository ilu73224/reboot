package com.stc.reboot;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    public static final String TAG = "reboot";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new mainTask().execute();
        finish();
    }
    private class mainTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            tryReboot();
            return null;
        }
    }
    public void tryReboot() {
        try {
            Process pReboot = Runtime.getRuntime().exec("su");
            DataOutputStream opt = new DataOutputStream(pReboot.getOutputStream());
            opt.writeBytes("sendevent /dev/input/event2 1 116 1\n");
            opt.writeBytes("sendevent /dev/input/event2 0 0 0\n");
            opt.writeBytes("sleep 1\n");
            opt.writeBytes("sendevent /dev/input/event2 1 116 0\n");
            opt.writeBytes("sendevent /dev/input/event2 0 0 0\n");
            opt.writeBytes("exit\n");
            opt.flush();
            pReboot.waitFor();
            if(0 == pReboot.exitValue()) {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(pReboot.getInputStream()));
                StringBuilder log = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    log.append(String.format("%s\n", line));
                }
                Log.d(TAG, "run success result = \n " + log.toString());
            } else {
                Log.d(TAG, "run failed exit value =  " + pReboot.exitValue());
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(pReboot.getErrorStream()));
                StringBuilder log = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    log.append(String.format("%s\n", line));
                }
                Log.d(TAG, "Error = \n " + log.toString());
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
