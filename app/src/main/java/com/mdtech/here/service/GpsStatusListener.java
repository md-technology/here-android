package com.mdtech.here.service;

import android.location.GpsStatus;

import static com.mdtech.here.util.LogUtils.LOGI;
import static com.mdtech.here.util.LogUtils.makeLogTag;

public class GpsStatusListener implements GpsStatus.Listener {
    private static final String TAG = makeLogTag(GpsStatusListener.class);

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                LOGI(TAG, "GPS event is started.");
                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                LOGI(TAG, "GPS event is first fixed.");
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                LOGI(TAG, "GPS EVENT SATELLITE STATUS.");
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                LOGI(TAG, "GPS event is stopped.");
                break;
        }
    }
}
