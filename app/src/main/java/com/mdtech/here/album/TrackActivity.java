package com.mdtech.here.album;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.mdtech.here.R;
import com.mdtech.here.dao.ArchiveMeta;
import com.mdtech.here.service.Recorder;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.social.api.HereApi;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;

import static com.mdtech.here.util.LogUtils.LOGI;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * Created by Xiaoniu on 2016/1/3.
 */
public class TrackActivity extends BaseActivity {
    private static final String TAG = makeLogTag(TrackActivity.class);

    private HereApi mApi;

    @Bind(R.id.fab_start)
    FloatingActionButton mFabStart;
    @Bind(R.id.fab_stop)
    FloatingActionButton mFabStop;
    @Bind(R.id.fab_finish)
    FloatingActionButton mFabFinish;
    @Bind(R.id.tv_cost_time)
    TextView mCostTime;

    private boolean isRecording;

    private Recorder.ServiceBinder mServiceBinder;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServiceBinder = (Recorder.ServiceBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBinder = null;
        }
    };

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, TrackActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.track_activity);

        mFabStart.setOnClickListener(this);
        mFabFinish.setOnClickListener(this);

        mApi = getApi();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent recordServerIntent = new Intent(getApplicationContext(), Recorder.class);
        startService(recordServerIntent);
        bindService(recordServerIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.fab_start:
                if (mServiceBinder != null && !isRecording) {

                    mServiceBinder.startRecord();
                    mFabStart.setVisibility(View.GONE);
                    mFabStop.setVisibility(View.VISIBLE);
                    mFabFinish.setVisibility(View.VISIBLE);
                    notifyUpdateView();
                }
                break;
            case R.id.fab_stop:
                stopTrack();
                mFabStart.setVisibility(View.VISIBLE);
                mFabStop.setVisibility(View.GONE);
                break;

            case R.id.fab_finish:
                finishTrack();

                break;
        }
    }

    private boolean stopTrack() {

        return true;
    }

    private boolean finishTrack() {
        mFabStart.setVisibility(View.VISIBLE);
        mFabStop.setVisibility(View.GONE);
        mFabFinish.setVisibility(View.GONE);
        if (isRecording && mServiceBinder != null) {
            mServiceBinder.stopRecord();
            notifyUpdateView();

//            if (archiveMeta != null) {
//                long count = archiveMeta.getCount();
//                if (count > MINI_RECORDS) {
//                    Intent intent = new Intent(context, Detail.class);
//                    intent.putExtra(Records.INTENT_ARCHIVE_FILE_NAME, archiveMeta.getName());
//                    startActivity(intent);
//                }
//            }
        }

//        setViewStatus(FLAG_ENDED);
        return true;
    }

    private void notifyUpdateView() {
        Message message = new Message();
        message.what = MESSAGE_UPDATE_VIEW;
        uiHandler.sendMessage(message);

    }

    private Timer updateViewTimer;
    private static final long TIMER_PERIOD = 1000;

    @Override
    public void onResume() {
        super.onResume();
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            updateViewTimer = new Timer();
            updateViewTimer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            notifyUpdateView();
                        }
                    }, 0, TIMER_PERIOD);
        }
    }

    public static final int MESSAGE_UPDATE_VIEW = 0x011;
    private static final int FLAG_RECORDING = 0x001;
    private static final int FLAG_ENDED = 0x002;
    protected FragmentManager fragmentManager;
    private ArchiveMeta archiveMeta;
    // 控制界面显示 UI
    private Handler uiHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_VIEW:

                    if (mServiceBinder == null) {
                        LOGI(TAG, "not available");
                        return;
                    }

                    archiveMeta = mServiceBinder.getMeta();

                    switch (mServiceBinder.getStatus()) {
                        case Recorder.ServiceBinder.STATUS_RECORDING:
                            setViewStatus(FLAG_RECORDING);
                            isRecording = true;
                            break;
                        case Recorder.ServiceBinder.STATUS_STOPPED:
                            setViewStatus(FLAG_ENDED);
                            isRecording = false;
                    }
            }
        }
    };

    private void setViewStatus(int status) {
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (status) {
            case FLAG_RECORDING:
                mFabStart.setVisibility(View.GONE);
                mFabStop.setVisibility(View.VISIBLE);
                mFabFinish.setVisibility(View.VISIBLE);
                if (archiveMeta != null) {
//                    archiveMetaFragment = new ArchiveMetaFragment(context, archiveMeta);
//                    fragmentTransaction.replace(R.id.status_layout, archiveMetaFragment);
//                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                    mCostTime.setText(archiveMeta.getCostTimeStringByNow());
                }
                break;
            case FLAG_ENDED:
                mFabStart.setVisibility(View.VISIBLE);
                mFabStop.setVisibility(View.GONE);
                mFabFinish.setVisibility(View.GONE);
//                if (archiveMetaFragment != null) {
//                    fragmentTransaction.remove(archiveMetaFragment);
//                }
                mCostTime.setText("");
                break;
        }

//        fragmentTransaction.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mServiceBinder != null) {
            unbindService(mServiceConnection);
        }
    }

}
