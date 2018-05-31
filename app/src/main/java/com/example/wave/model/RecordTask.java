package com.example.wave.model;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by jimin on 2018/5/30.
 */

public class RecordTask extends AsyncTask<Void, Integer, Void> {
    private static final String TAG = "RecordTask";

    private IRecordView mView;

    public RecordTask(IRecordView view) {
        this.mView = view;
    }

    private final int FREQUENCY = 16000;
    private final int CHANNEL_CONFIG = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private boolean bRecording = true;

    public void setRecording(boolean b) {
        this.bRecording = b;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.i(TAG, "RecordTask onPreExecute");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i(TAG, "RecordTask onPostExecute");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.i(TAG, "RecordTask doInBackground - ThreadID:" + Thread.currentThread().getId());
        try {
            int buffSize = AudioRecord.getMinBufferSize(FREQUENCY, CHANNEL_CONFIG, AUDIO_FORMAT);
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, FREQUENCY, CHANNEL_CONFIG, AUDIO_FORMAT, buffSize);
            byte[] buff = new byte[buffSize];

            Log.i(TAG, "startRecording: " + System.currentTimeMillis() + "");
            record.startRecording();

            Log.i(TAG, "after startRecording: " + System.currentTimeMillis() + "");

            while (bRecording) {
                int buffReadLen = record.read(buff, 0, buff.length);
                int volume = getVolume(buff, buffReadLen);
                publishProgress(volume);
            }
            record.stop();
            Log.i(TAG, "stopRecording: " + System.currentTimeMillis() + "");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "doInBackground Exception: " + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.i(TAG, "RecordTask onProgressUpdate volume" + values[0]);

        if (mView != null)
            mView.updateVolume(values[0]);
    }


    private int getVolume(byte[] buff, int buffReadLen) {
        if (buffReadLen <= 0 || buff == null)
            return 0;


        int total = 0;
        for (int i = 0; i < buffReadLen; i++) {
            total += Math.abs(buff[i]);
        }

        return total / buffReadLen;
    }
}
