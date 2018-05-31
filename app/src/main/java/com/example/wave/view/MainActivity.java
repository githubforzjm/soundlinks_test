package com.example.wave.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.wave.R;
import com.example.wave.model.IRecordView;
import com.example.wave.presenter.RecordPresenter;

public class MainActivity extends Activity implements IRecordView {
    private static final String TAG = "MainActivity";
    private RecordPresenter recordPresenter;

    View viewAnim;
    Button btnRecord;
    MyRingWave waveView;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordPresenter = new RecordPresenter(this);
        findView();
        addListener();
    }


    private void findView() {
        viewAnim = findViewById(R.id.view_anim);
        btnRecord = (Button) findViewById(R.id.btn_record);
        waveView = (MyRingWave) findViewById(R.id.wave_view);
        seekBar = (SeekBar) findViewById(R.id.sb_frequence);

    }


    private void addListener() {
        btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "ACTION_DOWN");
                        recordPresenter.startRecord();
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, "ACTION_UP");
                        recordPresenter.stopRecord();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        Log.i(TAG, "ACTION_CANCEL");
                        break;

                    case MotionEvent.ACTION_OUTSIDE:
                        Log.i(TAG, "ACTION_OUTSIDE");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i(TAG, "ACTION_MOVE");
                        break;
                }


                return false;
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 控制颜色深度，值越大，颜色越深
                waveView.addPoint(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    Toast toast;

    @Override
    public void updateVolume(int volume) {
        if (toast == null)
            toast = Toast.makeText(this, "Volume: " + volume, Toast.LENGTH_SHORT);

        toast.setText("Volume: " + volume);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        // 根据音量大小，调节颜色深度
        waveView.addPoint(volume);
    }
}
