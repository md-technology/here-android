package com.mdtech.here.panoramio;

import android.os.AsyncTask;
import android.util.Log;

import com.mdtech.social.api.PanoramioOperations;
import com.mdtech.social.api.model.Photo;

import java.util.List;

/**
 * Created by any on 2014/10/31.
 */
public abstract class AbstractPanoramioLayer implements PanoramioLayer {

    private static final String TAG = AbstractPanoramioLayer.class.getSimpleName();

    private boolean start = false;

    protected PanoramioOperations panoramioOperations;

    public PanoramioOperations getPanoramioOperations() {
        return panoramioOperations;
    }

    public void setPanoramioOperations(PanoramioOperations panoramioOperations) {
        this.panoramioOperations = panoramioOperations;
    }

    public void getPanoramio(double swLat, double swLng, double neLat, double neLng, float zoom) {

        // 如果开始标识为false，则不运行接口
        if(!this.start) {
            Log.d(TAG, "PanoramioLayer has not start");
            return;
        }

        // 如果还存在上次请求，则取消上次请求
        if(null != restCallTask && (restCallTask.getStatus() != AsyncTask.Status.FINISHED)) {
            restCallTask.cancel(true);
        }

        restCallTask = new AsyncTask<String, Void, List<Photo>>() {
            @Override
            protected List<Photo> doInBackground(String... params) {
                try {
                    return panoramioOperations.getPanoramio(params);
                }catch (Exception ex) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Photo> panoramioResponse) {
                super.onPostExecute(panoramioResponse);
                if(null != panoramioResponse) {
                    Log.d(TAG, "PanoramioResponse photos size is " + panoramioResponse.size());
                    displayPanoramioPhoto(panoramioResponse);
                }
            }
        };

        restCallTask.execute(Double.toString(swLat),
                Double.toString(swLng),
                Double.toString(neLat),
                Double.toString(neLng),
                Integer.toString((int)Math.ceil(zoom)));
    }

    protected AsyncTask<String, Void, List<Photo>> restCallTask;

    @Override
    public void start() {
        this.start = true;
        this.trigger();
    }

    @Override
    public void stop() {
        this.start = false;
    }

    @Override
    public void pause() {
        this.start = false;
    }

}
