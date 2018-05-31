package com.example.wave.presenter;

import com.example.wave.model.IRecordView;
import com.example.wave.model.RecordTask;

/**
 * Created by jimin on 2018/5/30.
 */

public class RecordPresenter {
    IRecordView mView;
    RecordTask recordTask;

    public RecordPresenter(IRecordView view) {
        this.mView = view;
    }

    public void startRecord() {
        if (mView == null)
            return;

        if (recordTask == null) {
            recordTask = new RecordTask(mView);
            recordTask.execute();
        }
    }

    public void stopRecord() {
        if (recordTask != null) {
            recordTask.setRecording(false);
            recordTask.cancel(true);
            recordTask = null;
        }
    }

}
