package com.mdtech.here.album;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdtech.geojson.FeatureCollection;
import com.mdtech.here.R;
import com.mdtech.here.dao.ArchiveMeta;
import com.mdtech.here.dao.Archiver;
import com.mdtech.here.dao.TrackExporter;
import com.mdtech.here.service.ArchiveNameHelper;
import com.mdtech.here.service.Recorder;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.here.ui.SwipeToAction;
import com.mdtech.here.util.SocialAsyncTask;
import com.mdtech.social.api.*;
import com.mdtech.social.api.model.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.mdtech.here.util.LogUtils.LOGE;
import static com.mdtech.here.util.LogUtils.LOGI;
import static com.mdtech.here.util.LogUtils.makeLogTag;


public class TrackActivity extends BaseActivity {
    private static final String TAG = makeLogTag(TrackActivity.class);

    public static final String BUNDLE_STATE_MAPVIEW = "mapview";

    private Context mContext;
    private HereApi mApi;

    // Track list
    @Bind(R.id.rv_tracks)
    RecyclerView mRecyclerView;
    TrackListAdapter mAdapter;
    SwipeToAction mSwipeToAction;
    private ArrayList<Archiver> archives = new ArrayList<Archiver>();
    private ArrayList<String> archiveFileNames;
    private ArchiveNameHelper archiveFileNameHelper;

    // map ui
    MapBaiduFragment mMapFragment;

    // track recording ui
    @Bind(R.id.fab_start)
    FloatingActionButton mFabStart;
    @Bind(R.id.fab_stop)
    FloatingActionButton mFabStop;
    @Bind(R.id.fab_finish)
    FloatingActionButton mFabFinish;
    @Bind(R.id.tv_cost_time)
    TextView mCostTime;

    // track recording flag
    private Timer updateViewTimer;
    private static final long TIMER_PERIOD = 2000;
    private boolean isRecording;
    public static final int MESSAGE_UPDATE_VIEW = 0x011;
    public static final int MESSAGE_START_RECORDING = 0x012;
    private static final int FLAG_RECORDING = 0x001;
    private static final int FLAG_ENDED = 0x002;
    private ArchiveMeta mArchiveMeta;

    // recording service
    private Recorder.ServiceBinder mServiceBinder;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServiceBinder = (Recorder.ServiceBinder) iBinder;
            Message message = new Message();
            message.what = MESSAGE_START_RECORDING;
            uiHandler.sendMessage(message);
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
        // fragments
        android.app.FragmentManager fm = getFragmentManager();
        mMapFragment = (MapBaiduFragment) fm.findFragmentByTag("map");

        setContentView(R.layout.track_activity);

        setupAppbar(this);

        this.mContext = getApplicationContext();
        // Track list
        setupTrackList();
        this.archiveFileNameHelper = new ArchiveNameHelper(mContext);

        // actions
        mFabStart.setOnClickListener(this);
        mFabFinish.setOnClickListener(this);
    }

    /**
     * 设置右边栏track列表
     */
    private void setupTrackList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new TrackListAdapter(mContext, this.archives);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeToAction = new SwipeToAction(mRecyclerView, new SwipeToAction.SwipeListener<Archiver>() {
            @Override
            public boolean swipeLeft(final Archiver itemData) {
                closeDrawer();
//                final int pos = removeArchiver(itemData);
                displaySnackbar(getString(R.string.track_remove) + "? " + itemData.getName(),
                        getString(R.string.confirm_yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        addArchiver(pos, itemData);
                        removeArchiver(itemData);
                    }
                }, new Snackbar.Callback() {
                    public void onDismissed(Snackbar snackbar, @DismissEvent int event) {
                        // empty
//                        if(archives.indexOf(itemData)==-1) {
//                            itemData.delete();
//                        }
                    }
                });
                return true;
            }

            @Override
            public boolean swipeRight(Archiver itemData) {
                displaySnackbar(itemData.getName() + " uploading", null, null, null);
                upload(itemData);
                return true;
            }

            @Override
            public void onClick(Archiver itemData) {
                displayArchiver(itemData);
            }

            @Override
            public void onLongClick(Archiver itemData) {
                swipeLeft(itemData);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the mapview state in a separate bundle parameter
        final Bundle mapviewState = new Bundle();
        mMapFragment.onSaveInstanceState(mapviewState);
        outState.putBundle(BUNDLE_STATE_MAPVIEW, mapviewState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mMapFragment == null) {
            addMapFragment(savedInstanceState);
        }
        mApi = getApi();
    }

    private void addMapFragment(Bundle savedInstanceState) {
        if (mMapFragment == null) {
            if(null != savedInstanceState) {
                Bundle previousState = savedInstanceState.getBundle(BUNDLE_STATE_MAPVIEW);
                mMapFragment = MapBaiduFragment.newInstance(previousState);
            }else {
                mMapFragment = MapBaiduFragment.newInstance();
            }
            getFragmentManager().beginTransaction()
                    .add(R.id.fl_container_map, mMapFragment, "map")
                    .commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent recordServerIntent = new Intent(getApplicationContext(), Recorder.class);
        startService(recordServerIntent);
        bindService(recordServerIntent, mServiceConnection, BIND_AUTO_CREATE);

        // Track list
        long selectedTime = new Date().getTime();
        getArchiveFilesByMonth(new Date(selectedTime));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.fab_start:
                if (mServiceBinder != null && !isRecording) {
//                    addMapFragment(null);
                    mServiceBinder.startRecord();
                    updateRecording(true);
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
        if (isRecording && mServiceBinder != null) {
            mServiceBinder.stopRecord();
            notifyUpdateView();
        }
        updateRecording(false);
        Archiver archiver = mServiceBinder.getArchive();
        if(archiver.exists()) {
            insertArchiver(archiver);
        }
        return true;
    }

    private void notifyUpdateView() {
        Message message = new Message();
        message.what = MESSAGE_UPDATE_VIEW;
        uiHandler.sendMessage(message);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            updateViewTimer = new Timer();
        }
        Message message = new Message();
        message.what = MESSAGE_START_RECORDING;
        uiHandler.sendMessage(message);
    }

    // 控制界面显示 UI
    private Handler uiHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_VIEW:

                    if (mServiceBinder == null) {
                        LOGI(TAG, "not available");
                        return;
                    }

                    mArchiveMeta = mServiceBinder.getMeta();

                    switch (mServiceBinder.getStatus()) {
                        case Recorder.ServiceBinder.STATUS_RECORDING:
                            setViewStatus(FLAG_RECORDING);
                            break;
                        case Recorder.ServiceBinder.STATUS_STOPPED:
                            setViewStatus(FLAG_ENDED);
                    }
                    break;
                case MESSAGE_START_RECORDING:
                    if (mServiceBinder == null) {
                        LOGI(TAG, "not available");
                        return;
                    }

                    mArchiveMeta = mServiceBinder.getMeta();

                    switch (mServiceBinder.getStatus()) {
                        case Recorder.ServiceBinder.STATUS_RECORDING:
                            updateRecording(true);
                            break;
                        case Recorder.ServiceBinder.STATUS_STOPPED:
                            updateRecording(false);
                    }
            }
        }
    };

    private void setViewStatus(int status) {
        switch (status) {
            case FLAG_RECORDING:
                if (mArchiveMeta != null) {
                    mCostTime.setText(mArchiveMeta.getCostTimeStringByNow());
                    setmArchiveMeta(mArchiveMeta);
                    addArchiverToMap(mServiceBinder.getArchive());
                }
                break;
            case FLAG_ENDED:
                mCostTime.setText("");
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mServiceBinder != null) {
            unbindService(mServiceConnection);
        }
        // Track list
        closeArchives();
    }

    /**
     * 关闭右边栏
     */
    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
    }

    /**
     * 显示某个track
     * @param archiver
     */
    private void displayArchiver(Archiver archiver) {
        closeDrawer();
        if(isRecording) {
            displaySnackbar(getString(R.string.track_recording_info), null, null, null);
            return;
        }
        clearMap();
        addArchiverToMap(archiver);
    }

    /**
     * 清空地图
     */
    private void clearMap() {
        mMapFragment.clear();
    }

    private void addArchiverToMap(Archiver archiver) {

        mLayoutMeta.setVisibility(View.VISIBLE);
        mArchiveMeta = archiver.getMeta();
        if(mArchiveMeta.getCount() < 1) {
            return;
        }
        FeatureCollection featureCollection = new TrackExporter(archiver).execute();

//        ObjectMapper serializer = new ObjectMapper();
//        String out = "FeatureCollection serializer error!";
//        try {
//            out = serializer.writeValueAsString(featureCollection);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        LOGE(TAG, out);

        mMapFragment.addGeoJSON(featureCollection);
        setmArchiveMeta(mArchiveMeta);
    }

    private void getArchiveFilesByMonth(Date date) {
        archiveFileNames = archiveFileNameHelper.getArchiveFilesNameByMonth(date);
        openArchivesFromFileNames();
    }

    /**
     * 从指定目录读取所有已保存的列表
     */
    private void openArchivesFromFileNames() {
        Iterator<String> iterator = archiveFileNames.iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            Archiver archive = new Archiver(mContext, name);
            archives.add(archive);
//            if (archive.getMeta().getCount() > 0) {
//
//            }
        }
    }

    private boolean insertArchiver(Archiver archiver) {
        Iterator<Archiver> iterator = archives.iterator();
        boolean exist = false;
        while (iterator.hasNext()) {
            if(iterator.next().getName().equals(archiver.getName())) {
                exist = true;
                break;
            }
        }

        if(exist) {
            return false;
        }else {
            archives.add(0, new Archiver(mContext, archiver.getName()));
            mAdapter.notifyDataSetChanged();
            return true;
        }
    }

    /**
     * 清除列表
     */
    private void closeArchives() {
        Iterator<Archiver> iterator = archives.iterator();
        while (iterator.hasNext()) {
            Archiver archive = (Archiver) iterator.next();
            if (archive != null) {
                archive.close();
            }
        }
        archives.clear();
    }

    /**
     * 显示通知
     * @param text
     * @param actionName
     * @param action
     * @param callback
     */
    private void displaySnackbar(String text, String actionName, View.OnClickListener action, Snackbar.Callback callback) {
        Snackbar snack = Snackbar.make(mFabStart, text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);
        if(null != callback) {
            snack.setCallback(callback);
        }
        View v = snack.getView();
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(getResources().getColor(R.color.colorAccent));

        snack.show();
    }

    // track meta pane
    @Bind(R.id.tv_start_time)
    TextView mStartTime;
    @Bind(R.id.tv_end_time)
    TextView mEndTime;
    @Bind(R.id.tv_distance)
    TextView mDistance;
    @Bind(R.id.tv_average_speed)
    TextView mAvgSpeed;
    @Bind(R.id.tv_max_speed)
    TextView mMaxSpeed;
    @Bind(R.id.tv_records)
    TextView mRecords;
    @Bind(R.id.layout_meta)
    View mLayoutMeta;

    /**
     * 设置pane各属性值
     * @param meta
     */
    private void setmArchiveMeta(ArchiveMeta meta) {
        mLayoutMeta.setVisibility(View.VISIBLE);
        String formatter = getString(R.string.track_formatter);
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.track_time_format), Locale.CHINA);
        Date startTime = meta.getStartTime();
        Date endTime = meta.getEndTime();
        try {
            mStartTime.setText(
                    startTime != null ?
                            dateFormat.format(startTime) : getString(R.string.track_not_available));

            mEndTime.setText(
                    endTime != null ?
                            dateFormat.format(endTime) : getString(R.string.track_not_available));

            mDistance.setText(String.format(formatter, meta.getDistance() / ArchiveMeta.TO_KILOMETRE));
            mMaxSpeed.setText(String.format(formatter, meta.getMaxSpeed() * ArchiveMeta.KM_PER_HOUR_CNT));
            mAvgSpeed.setText(String.format(formatter, meta.getAverageSpeed() * ArchiveMeta.KM_PER_HOUR_CNT));
            mRecords.setText(String.valueOf(meta.getCount()));
        } catch (Exception e) {
            LOGE(TAG, e.getMessage());
        }
    }

    /**
     * 更新pane显示
     * @param isRecording
     */
    private void updateRecording(boolean isRecording) {
        this.isRecording = isRecording;
        if(isRecording) {
            mFabStart.setVisibility(View.GONE);
            mFabStop.setVisibility(View.VISIBLE);
            mFabFinish.setVisibility(View.VISIBLE);
            scheduleRefreshUI();
        }else {
            mFabStart.setVisibility(View.VISIBLE);
            mFabStop.setVisibility(View.GONE);
            mFabFinish.setVisibility(View.GONE);
            stopRefreshUI();
        }
    }

    /**
     * 设置定时刷新info pane
     */
    private void scheduleRefreshUI() {
        stopRefreshUI();
        updateViewTimer = new Timer();
        updateViewTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        notifyUpdateView();
                    }
                }, 0, TIMER_PERIOD);
    }

    private void stopRefreshUI() {
        if(null != updateViewTimer) {
            updateViewTimer.cancel();
            updateViewTimer.purge();
        }
    }

    /**
     * 从列表中删除track，并删除本地暂存track文件
     * @param archiver
     * @return
     */
    private int removeArchiver(Archiver archiver) {
        archiver.delete();
        int pos = archives.indexOf(archiver);
        archives.remove(archiver);
        mAdapter.notifyItemRemoved(pos);
        return pos;
    }

    private void upload(final Archiver archiver) {
        if(!TextUtils.isEmpty(archiver.getName()) && archiver.getName().equals(mArchiveMeta.getName())) {
            showSnackbarInfo(mFabStart, "recording");
            return;
        }

        FeatureCollection featureCollection = new TrackExporter(archiver).execute();
        final Track track = new Track();
        track.setName("");
        track.setTitle("");
        track.setDescription("");
        track.setFeatureCollection(featureCollection);

        new SocialAsyncTask<Track, Void, Track>() {

            @Override
            protected Track request(Track... params) {
                return mApi.trackOperations().create(params[0]);
            }

            @Override
            protected void error(com.mdtech.social.api.Error error) {
                showSnackbarInfo(mFabStart, error.info);
            }

            @Override
            protected void onPostExecute(Track track) {
                super.onPostExecute(track);
                if(null != track) {
                    removeArchiver(archiver);
                }
            }
        }.execute(track);

    }

//    /**
//     * 添加track到右边栏列表位置
//     * @param pos
//     * @param archiver
//     */
//    private void addArchiver(int pos, Archiver archiver) {
//        archives.add(pos, archiver);
//        mAdapter.notifyItemInserted(pos);
//    }

    /**
     * Track draw list adapter
     */
    static class TrackListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mContext;
        private ArrayList<Archiver> archives;

        /** References to the views for each data item **/
        public class TrackViewHolder extends SwipeToAction.ViewHolder<Archiver> {
            @Bind(R.id.description)
            TextView mDescription;
            @Bind(R.id.cost_time)
            TextView mCostTime;
            @Bind(R.id.distance)
            TextView mDistance;

            public TrackViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);
            }
        }

        public TrackListAdapter(Context context, ArrayList<Archiver> archives) {
            mContext = context;
            this.archives = archives;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.track_list_row, parent, false);

            return new TrackViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Archiver archive = archives.get(position);
            TrackViewHolder vh = (TrackViewHolder) holder;

            ArchiveMeta archiveMeta = archive.getMeta();

            vh.mDistance.setText(String.format(mContext.getString(R.string.track_formatter),
                    archiveMeta.getDistance() / ArchiveMeta.TO_KILOMETRE));

            String costTime = archiveMeta.getRawCostTimeString();
            vh.mCostTime.setText(costTime.length() > 0 ? costTime : mContext.getString(R.string.track_not_available));

            String description = archiveMeta.getDescription();
            if (description.length() <= 0) {
                description = mContext.getString(R.string.track_no_description);
                vh.mDescription.setTextColor(mContext.getResources().getColor(R.color.aluminum));
            }
            vh.mDescription.setText(description);
            vh.data = archive;
        }

        @Override
        public int getItemCount() {
            return archives.size();
        }
    }
}
