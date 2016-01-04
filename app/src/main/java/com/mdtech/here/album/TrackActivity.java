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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdtech.geojson.FeatureCollection;
import com.mdtech.here.R;
import com.mdtech.here.dao.ArchiveMeta;
import com.mdtech.here.dao.Archiver;
import com.mdtech.here.dao.TrackExporter;
import com.mdtech.here.service.ArchiveNameHelper;
import com.mdtech.here.service.Recorder;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.social.api.HereApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;

import static com.mdtech.here.util.LogUtils.LOGE;
import static com.mdtech.here.util.LogUtils.LOGI;
import static com.mdtech.here.util.LogUtils.makeLogTag;


public class TrackActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = makeLogTag(TrackActivity.class);

    public static final String BUNDLE_STATE_MAPVIEW = "mapview";

    private Context mContext;
    private HereApi mApi;
    // Track list
    private ArrayList<Archiver> archives;
    private ArchivesAdapter archivesAdapter;
    private ArrayList<String> archiveFileNames;
    private ArchiveNameHelper archiveFileNameHelper;
    @Bind(R.id.rv_tracks)
    ListView listView;

    // map ui
    MapBaiduFragment mMapFragment;

    @Bind(R.id.fab_start)
    FloatingActionButton mFabStart;
    @Bind(R.id.fab_stop)
    FloatingActionButton mFabStop;
    @Bind(R.id.fab_finish)
    FloatingActionButton mFabFinish;
    @Bind(R.id.tv_cost_time)
    TextView mCostTime;

    private Timer updateViewTimer;
    private static final long TIMER_PERIOD = 2000;
    private boolean isRecording;
    public static final int MESSAGE_UPDATE_VIEW = 0x011;
    private static final int FLAG_RECORDING = 0x001;
    private static final int FLAG_ENDED = 0x002;
    private ArchiveMeta archiveMeta;

    // service
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
        // fragments
        android.app.FragmentManager fm = getFragmentManager();
        mMapFragment = (MapBaiduFragment) fm.findFragmentByTag("map");

        setContentView(R.layout.track_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Add the back button to the toolbar.
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateUpOrBack(TrackActivity.this, null);
            }
        });

        this.mContext = getApplicationContext();
        // Track list
        this.archives = new ArrayList<Archiver>();
        this.archivesAdapter = new ArchivesAdapter(archives);
        this.listView.setAdapter(archivesAdapter);
        this.archiveFileNameHelper = new ArchiveNameHelper(mContext);

        // actions
        mFabStart.setOnClickListener(this);
        mFabFinish.setOnClickListener(this);

        mApi = getApi();
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
            // Either restore the state of the map or read it from the Intent extras.
            if (savedInstanceState != null) {
                // Restore state from existing bundle
                Bundle previousState = savedInstanceState.getBundle(BUNDLE_STATE_MAPVIEW);
                mMapFragment = MapBaiduFragment.newInstance(previousState);
            } else {
                // Get highlight room id (if specified in intent extras)
//                final String highlightRoomId = getIntent().hasExtra(EXTRA_ROOM) ? getIntent()
//                        .getExtras().getString(EXTRA_ROOM) : null;
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
        listView.setOnItemClickListener(this);
        getArchiveFilesByMonth(new Date(selectedTime));
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
            updateViewTimer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            notifyUpdateView();
                        }
                    }, 0, TIMER_PERIOD);
        }
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
//                    archiveMetaFragment = new ArchiveMetaFragment(mContext, archiveMeta);
//                    fragmentTransaction.replace(R.id.status_layout, archiveMetaFragment);
//                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                    mCostTime.setText(archiveMeta.getCostTimeStringByNow());
                    addArchiverToMap(mServiceBinder.getArchive());
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
        // Track list
        closeArchives();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Archiver archiver = archives.get(position);
        addArchiverToMap(archiver);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
    }

    private void addArchiverToMap(Archiver archiver) {
        FeatureCollection featureCollection = new TrackExporter(archiver).execute();

//        ObjectMapper serializer = new ObjectMapper();
//        String out = "FeatureCollection serializer error!";
//        try {
//            out = serializer.writeValueAsString(featureCollection);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        LOGE(TAG, out);
        // 先清空地图
        mMapFragment.clear();
        mMapFragment.addGeoJSON(featureCollection);
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

            if (archive.getMeta().getCount() > 0) {
                archives.add(archive);
            }
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
     * ListView Adapter
     */
    public class ArchivesAdapter extends ArrayAdapter<Archiver> {

        public ArchivesAdapter(ArrayList<Archiver> archives) {
            super(mContext, R.layout.track_list_row, archives);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Archiver archive = archives.get(position);
            ArchiveMeta archiveMeta = archive.getMeta();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.track_list_row, parent, false);

            TextView mDescription = (TextView) rowView.findViewById(R.id.description);
            TextView mCostTime = (TextView) rowView.findViewById(R.id.cost_time);
            TextView mDistance = (TextView) rowView.findViewById(R.id.distance);

            mDistance.setText(String.format(getString(R.string.track_formatter),
                    archiveMeta.getDistance() / ArchiveMeta.TO_KILOMETRE));

            String costTime = archiveMeta.getRawCostTimeString();
            mCostTime.setText(costTime.length() > 0 ? costTime : getString(R.string.track_not_available));

            String description = archiveMeta.getDescription();
            if (description.length() <= 0) {
                description = getString(R.string.track_no_description);
                mDescription.setTextColor(getResources().getColor(R.color.aluminum));
            }
            mDescription.setText(description);

            return rowView;
        }
    }
}
