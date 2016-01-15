package com.mdtech.here.service;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;

import com.mdtech.here.BuildConfig;
import com.mdtech.here.dao.ArchiveMeta;
import com.mdtech.here.dao.Archiver;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

import static com.mdtech.here.util.LogUtils.LOGD;
import static com.mdtech.here.util.LogUtils.LOGI;
import static com.mdtech.here.util.LogUtils.LOGW;
import static com.mdtech.here.util.LogUtils.makeLogTag;

/**
 * 绑定 LocationListener 回调并记录到数据库
 *
 */
public class TrackLocationListener implements LocationListener {
    private static final String TAG = makeLogTag(TrackLocationListener.class);

    private final static int ACCURACY = 3;
    private final static int CACHE_SIZE = 5;

    private Archiver archiver;
    private ArchiveMeta meta = null;
    private BigDecimal lastLatitude;
    private BigDecimal lastLongitude;
    private HashMap<Long, Location> locationCache;
    private Recorder.ServiceBinder binder = null;

    public boolean isRecording = false;

    public TrackLocationListener(Archiver archiver) {
        this.archiver = archiver;
        this.locationCache = new HashMap<Long, Location>();
    }

    public TrackLocationListener(Archiver archiver, Recorder.ServiceBinder binder) {
        this.archiver = archiver;
        this.locationCache = new HashMap<Long, Location>();
        this.binder = binder;
    }

    private boolean filter(Location location) {
        BigDecimal longitude = (new BigDecimal(location.getLongitude()))
                .setScale(ACCURACY, BigDecimal.ROUND_HALF_UP);

        BigDecimal latitude = (new BigDecimal(location.getLatitude()))
                .setScale(ACCURACY, BigDecimal.ROUND_HALF_UP);

        if (latitude.equals(lastLatitude) && longitude.equals(lastLongitude)) {
            return false;
        }

        lastLatitude = latitude;
        lastLongitude = longitude;
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        LOGD(TAG, "Location Provider:" + location.getProvider() + ", Accuracy:" + location.getAccuracy());
        // Save fitted location into database
        if (BuildConfig.ENABLE_DEBUG_TOOLS || filter(location)) {
            locationCache.put(System.currentTimeMillis(), location);
            // 第一个位置先写入数据库，为了能在UI显示
            long size = this.archiver.getMeta().getCount();
            if (size == 0 || locationCache.size() > CACHE_SIZE) {
                flushCache();
            }
//            if(isRecording) {
//
//            }else {
//                binder.setCurrentLocation(location);
//            }
        }
    }

    /**
     * flush cache
     */
    public void flushCache() {
        Iterator<Long> iterator = locationCache.keySet().iterator();
        while (iterator.hasNext()) {
            Long timeMillis = iterator.next();
            Location location = locationCache.get(timeMillis);
            if (archiver.add(location, timeMillis)) {
                LOGI(TAG, String.format(
                        "Location(%f, %f) has been saved into database.", location.getLatitude(), location.getLongitude()));
            }
        }

        locationCache.clear();

        // 计算动态路径
        this.meta = archiver.getMeta();
        if (meta != null) {
            meta.setRawDistance();
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
//                    binder.startRecord();
                LOGI(TAG, "Location provider is available.");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                LOGI(TAG, "Location provider is out of service.");
                //binder.stopRecord();
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                LOGI(TAG, "Location provider is temporarily unavailable.");
                //binder.stopRecord();
                break;
        }
    }

    @Override
    public void onProviderEnabled(String s) {
        LOGI(TAG, "Location provider is enabled.");
    }

    @Override
    public void onProviderDisabled(String s) {
        LOGW(TAG, "Location provider is disabled.");
    }

    private static final int CHECK_INTERVAL = 1000 * 30;

    protected boolean isBetterLocation(Location location,
                                       Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > CHECK_INTERVAL;
        boolean isSignificantlyOlder = timeDelta < -CHECK_INTERVAL;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location,
        // use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must
            // be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate
                && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
