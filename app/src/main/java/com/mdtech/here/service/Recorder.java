package com.mdtech.here.service;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.widget.RemoteViews;

import com.mdtech.here.BuildConfig;
import com.mdtech.here.R;
import com.mdtech.here.album.TrackActivity;
import com.mdtech.here.dao.ArchiveMeta;
import com.mdtech.here.dao.Archiver;
import com.mdtech.here.settings.SettingsUtils;
import com.mdtech.here.util.UIUtils;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.mdtech.here.util.LogUtils.LOGE;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * @todo - need to fix recording flag errors
 */
interface Binder {
    public static final int STATUS_RECORDING = 0x0000;
    public static final int STATUS_STOPPED = 0x1111;

    /**
     * require startLocate()
     */
    void startRecord();

    void stopRecord();

    int getStatus();

    ArchiveMeta getMeta();

    Archiver getArchive();

    Location getLastRecord();

    void startLocate();

    void stopLocate();

    void setCurrentLocation(Location location);

    Location getCurrentLocation();
}

public class Recorder extends Service {
    private static final String TAG = makeLogTag(Recorder.class);
    public static final int NOTIFICATION_ID = 0x0001;

    protected Recorder.ServiceBinder serviceBinder = null;
    private SharedPreferences sharedPreferences;
    private Archiver archiver;

    private TrackLocationListener gpsListener;
    private TrackLocationListener netWorkListener;
    private GpsStatusListener statusListener;
    private LocationManager locationManager = null;

    private ArchiveNameHelper nameHelper;
    private String archivName;
    private Context context;
    private NotificationManager notificationManager;
    private Notification.Builder notification;

    private static final String RECORDER_SERVER_ID = "Tracker Service";
    private static final String PREF_STATUS_FLAG = "tracker_service_status";
    private TimerTask notifierTask;
    private Timer timer = null;

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        this.nameHelper = new ArchiveNameHelper(context);
//        this.helper = new Helper(context);
        if (serviceBinder == null) {
            serviceBinder = new ServiceBinder();
        }

        boolean alreadyStarted = (serviceBinder.getStatus() == ServiceBinder.STATUS_RECORDING);
        if(alreadyStarted) {
            serviceBinder.resetStatus();
            serviceBinder.startRecord();
        }

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification.Builder(context);
        notification.setOngoing(true);
        notification.setSmallIcon(R.drawable.ic_my_location_white_24dp);
        notification.setContentTitle("track title");
        notification.setContentText("track text");
        notification.setContentInfo("track info");
        Intent intent = new Intent(context, TrackActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notifier);
        notification.setContentIntent(contentIntent);
//        notification.setContent(contentView);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private void notificationPublish(float distance, double avgSpeed, double maxSpeed, String costTime) {
        // TODO FIX THIS BUG
        if(null == notification) {
            return;
        }
        //实例化Notification
        String title = getString(R.string.track_recording) + " " +
                String.format(getString(R.string.track_formatter), distance) + getString(R.string.track_km);
        notification.setContentTitle(title);

        String text = getString(R.string.track_average_speed) +
                String.format(getString(R.string.track_formatter), avgSpeed) + getString(R.string.track_kmh)
                + " " + getString(R.string.track_max_speed)
                + String.format(getString(R.string.track_formatter), maxSpeed)
                + getString(R.string.track_kmh);
        notification.setContentText(text);
        notification.setContentInfo(costTime);
        Notification noti = notification.build();
        noti.flags = Notification.FLAG_NO_CLEAR;
        notificationManager.notify(NOTIFICATION_ID, noti);
    }

    private void notificationCancel() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onDestroy() {
        serviceBinder.stopRecord();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    public class ServiceBinder extends android.os.Binder implements Binder {

        private Location currentLocation;

        ServiceBinder() {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            archiver = new Archiver(getApplicationContext());
            gpsListener = new TrackLocationListener(archiver, this);
            statusListener = new GpsStatusListener();
        }

        @Override
        public void startRecord() {
            if (getStatus() != ServiceBinder.STATUS_RECORDING) {
                // 如果没有外置存储卡
                if (!nameHelper.isExternalStoragePresent()) {
//                    UIUtils.makeSnackbar(null, getString(R.string.external_storage_not_present));
//                    helper.showLongToast(getString(R.string.external_storage_not_present));
                    return;
                }

                // 判定是否上次为异常退出
                boolean hasResumeName = nameHelper.hasResumeName();
                if (hasResumeName) {
                    archivName = nameHelper.getResumeName();
//                    helper.showLongToast(
//                            String.format(
//                                    getString(R.string.use_resume_archive_file, archivName)
//                            ));
                } else {
                    archivName = nameHelper.getNewName();
                }

                try {
                    archiver.open(archivName, Archiver.MODE_READ_WRITE);

                    // Set start time, if not resume from recovery
                    if (!hasResumeName) {
                        getMeta().setStartTime(new Date());
                    }
                    // start recording
                    requestLocationUpdates(gpsListener);

                    // 标记打开的文件，方便奔溃时恢复
                    nameHelper.setLastOpenedName(archivName);
                } catch (SQLiteException e) {
                    LOGE(TAG, e.getMessage());
                }

                // 另开个线程展示通知信息
                notifierTask = new TimerTask() {
                    @Override
                    public void run() {
                        switch (serviceBinder.getStatus()) {
                            case ServiceBinder.STATUS_RECORDING:
                                ArchiveMeta meta = getMeta();
                                float distance = meta.getDistance() / ArchiveMeta.TO_KILOMETRE;
                                double avgSpeed = meta.getAverageSpeed() * ArchiveMeta.KM_PER_HOUR_CNT;
                                double maxSpeed = meta.getMaxSpeed() * ArchiveMeta.KM_PER_HOUR_CNT;
                                String costTime = meta.getCostTimeStringByNow();

                                notificationPublish(distance, avgSpeed, maxSpeed, costTime);
                                break;

                            case ServiceBinder.STATUS_STOPPED:
                                notificationCancel();
                                break;
                        }
                    }
                };
                timer = new Timer();
                timer.schedule(notifierTask, 0, 5000);

                // Set status from shared preferences, default is stopped.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(PREF_STATUS_FLAG, ServiceBinder.STATUS_RECORDING);
                editor.commit();

                // for umeng
//                MobclickAgent.onEventBegin(context, RECORDER_SERVER_ID);
            }
        }

        public GpsStatus getGpsStatus() {
            return locationManager.getGpsStatus(null);
        }

        public void resetStatus() {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(PREF_STATUS_FLAG, ServiceBinder.STATUS_STOPPED);
            editor.commit();
        }

        @Override
        public void stopRecord() {
            if (getStatus() == ServiceBinder.STATUS_RECORDING) {
                // Flush listener cache
                gpsListener.flushCache();
                // stop recording
                removeUpdates();

                ArchiveMeta meta = getMeta();
                if(null == meta) {
//                    (new File(archivName)).delete();
                }else {
                    long totalCount = meta.getCount();
                    if (totalCount <= 0) {
                        (new File(archivName)).delete();
//                    helper.showLongToast(getString(R.string.not_record_anything));
                    } else {
                        meta.setEndTime(new Date());

                        // Show record result by toast
//                    helper.showLongToast(String.format(
//                            getString(R.string.result_report), String.valueOf(totalCount)
//                    ));
                    }
                }

                // 清除操作
                archiver.close();
                notificationCancel();

                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }

                nameHelper.clearLastOpenedName();

                // Set status from preference as stopped.
                resetStatus();

//                MobclickAgent.onEventEnd(context, RECORDER_SERVER_ID);
            }
        }

        @Override
        public int getStatus() {
            return sharedPreferences.getInt(PREF_STATUS_FLAG, ServiceBinder.STATUS_STOPPED);
        }

        @Override
        public ArchiveMeta getMeta() {
            return archiver.getMeta();
        }

        @Override
        public Archiver getArchive() {
            return archiver;
        }

        @Override
        public Location getLastRecord() {
            return archiver.getLastRecord();
        }

        private void requestLocationUpdates(TrackLocationListener listener) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                LOGE(TAG, "No GPS Permission");
                return;
            }
            // 从配置文件获取距离和精度选项
            long minTime = Long.parseLong(sharedPreferences.getString(SettingsUtils.PREF_GPS_MINTIME,
                    BuildConfig.DEFAULT_GPS_MINTIME));
            float minDistance = Float.parseFloat(sharedPreferences.getString(SettingsUtils.PREF_GPS_MINDISTANCE,
                    BuildConfig.DEFAULT_GPS_MINDISTANCE));
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    minTime, minDistance, listener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    minTime, minDistance, listener);
            locationManager.addGpsStatusListener(statusListener);
        }

        private void removeUpdates() {
            // 绑定 GPS 回调
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                LOGE(TAG, "No GPS Permission");
                return;
            }
            locationManager.removeUpdates(gpsListener);
            locationManager.removeGpsStatusListener(statusListener);
        }

        @Override
        public void startLocate() {
            requestLocationUpdates(gpsListener);
        }

        @Override
        public void stopLocate() {
            removeUpdates();
        }

        @Override
        public void setCurrentLocation(Location location) {
            this.currentLocation = location;
        }

        @Override
        public Location getCurrentLocation() {
            return this.currentLocation;
        }
    }
}
