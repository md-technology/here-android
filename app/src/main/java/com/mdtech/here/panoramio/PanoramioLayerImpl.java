//package com.mdtech.here.panoramio;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.util.Log;
//
//import com.mapbox.mapboxsdk.annotations.Marker;
//import com.mapbox.mapboxsdk.annotations.MarkerOptions;
//import com.mapbox.mapboxsdk.annotations.SpriteFactory;
//import com.mapbox.mapboxsdk.geometry.LatLng;
//import com.mapbox.mapboxsdk.views.MapView;
//import com.mdtech.here.R;
//import com.mdtech.social.api.model.Photo;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Target;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by any on 2014/9/28.
// */
//public class PanoramioLayerImpl extends AbstractPanoramioLayer
//        implements PanoramioLayer, MapView.OnMapChangedListener {
//
//    private Context context;
//
//    private static final String TAG = "PanoramioLayerImpl";
//
//    private MapView mapView;
////    private SpiceManager spiceManager;
//    private Map<String, Photo> photos = new HashMap<String, Photo>(0);
//    private Map<String, CustMarker> markers = new HashMap<String, CustMarker>(0);
//
//    private Picasso picasso;
//    private SpriteFactory spriteFactory = null;
//
//    public PanoramioLayerImpl(Context context) {
//        this.context = context;
//        picasso = new Picasso.Builder(context).build();
////        picasso.setLoggingEnabled(true);
////        picasso.setIndicatorsEnabled(true);
//    }
//
//    public void setMap(MapView mapView) {
//        this.mapView = mapView;
//        spriteFactory = mapView.getSpriteFactory();
//        // 添加map changed监听
//        this.mapView.addOnMapChangedListener(this);
//
//    }
//
////    private void request(MapStatus mapStatus) {
////        Projection proj = map.getProjection();
////
////        Log.d(TAG, "panoramio request map size is " + mapView.getWidth() + " " + mapView.getHeight());
////
////        LatLng southwest = proj.fromScreenLocation(new Point(0, mapView.getHeight()));
////        LatLng northeast = proj.fromScreenLocation(new Point(mapView.getWidth(), 0));
////
////        Log.d(TAG, "panoramio request map bounds is (" + southwest.latitude + ", "
////                + southwest.longitude + ") (" + northeast.latitude + ", "
////                + northeast.longitude + ")");
////        Log.d(TAG, "panoramio request zoom is " + mapStatus.zoom + " vendor is ");
//
//        // switch RetrofitSpice to spring social lib
////        PanoramioRetrofitSpiceRequest request = new PanoramioRetrofitSpiceRequest(
////                southwest.latitude, southwest.longitude,
////                northeast.latitude, northeast.longitude,
////                mapStatus.zoom,
////                vendor,
////                mapView.getWidth(), mapView.getHeight());
////        // 不能使用缓存
////        spiceManager.execute(request, new PanoramioRequestListener());
//
////        getPanoramio(southwest.latitude,
////                southwest.longitude,
////                northeast.latitude,
////                northeast.longitude,
////                mapStatus.zoom);
//
////    }
//
//    @Override
//    public void displayPanoramioPhoto(List<Photo> photoList) {
////        ObjectMapper mapper = new ObjectMapper();
////        try {
////            Log.d(TAG, mapper.writeValueAsString(photos));
////        } catch (JsonProcessingException e) {
////            e.printStackTrace();
////        }
//        Log.d(TAG, "photos size is " + String.valueOf(photoList.size()));
//        if(null != photoList) {
//
//            Iterator<Map.Entry<String, Photo>> iter = photos.entrySet().iterator();
//            while (iter.hasNext()) {
//                Map.Entry<String, Photo> entry = iter.next();
//
//                boolean exist = false;
//                for (Photo photo : photoList) {
//                    if (photo.getId().equals(entry.getKey())) {
//                        exist = true;
//                        break;
//                    }
//                }
//                if (!exist) {
//                    Marker marker = markers.get(entry.getKey()).marker;
////                    marker.setVisible(false);
//                    iter.remove();
//                }
//            }
//
//            for(Photo photo : photoList) {
////                    Log.d(TAG, photo.getId());
//
//                if(!photos.containsKey(photo.getId())) {
//                    photos.put(photo.getId(), photo);
//                }
//
//                if(markers.containsKey(photo.getId())) {
//                    Marker marker = markers.get(photo.getId()).marker;
////                    marker.setVisible(true);
//                }else {
//                    markers.put(photo.getId(), createMarker(photo));
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onMapChanged(int change) {
////        request();
//    }
//
//    public class CustMarker implements Target {
//
//        Marker marker;
//        Photo photo;
//
//        public CustMarker(Marker marker, Photo photo) {
//            this.marker = marker;
//            this.photo = photo;
//            Log.d(TAG, "Picasso Bitmap create marker, photo is " + getPhoto().getId());
//        }
//
//        @Override
//        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//            marker.setIcon(spriteFactory.fromBitmap(bitmap));
//            Log.d(TAG, "Picasso Bitmap Loaded, marker setIcon Photo is " + getPhoto().getId());
//        }
//
//        @Override
//        public void onBitmapFailed(Drawable errorDrawable) {
//            Log.d(TAG, "Picasso Bitmap failed, photo id is " + getPhoto().getId());
//        }
//
//        @Override
//        public void onPrepareLoad(Drawable placeHolderDrawable) {
//            Log.d(TAG, "Picasso Bitmap prepareLoad, photo id is " + getPhoto().getId());
//        }
//
//        public Photo getPhoto() {
////            Bundle bundle = marker.getExtraInfo();
////            Photo photo = (Photo)bundle.get(MARKER_DATA);
//            return photo;
//        }
//    }
//
//    @Override
//    public CustMarker createMarker(Photo photo) {
//        MarkerOptions markerOptions = new MarkerOptions();
//        double[] position = photo.getLocation().getPosition();
//        markerOptions.position(new LatLng(position[1], position[0]));
//        markerOptions.title(photo.getTitle());
//        markerOptions.icon(spriteFactory.fromResource(R.drawable.dic_launcher));
//
//        String url = "http://static.photoshows.cn/" + photo.getOssKey() + "@!panor-lg";
//
//        Marker marker = this.mapView.addMarker(markerOptions);
//
////        Bundle extraInfo = new Bundle();
////        extraInfo.putSerializable(MARKER_DATA, photo);
////        marker.setExtraInfo(extraInfo);
//
//        CustMarker cMarker = new CustMarker(marker, photo);
//        // Trigger the download of the URL asynchronously into the image view.
//        picasso.load(url) //
//                .placeholder(R.drawable.dic_launcher) //
//                .resize(60, 60)
//                .into(cMarker);
//        Log.d(TAG, "Picasso Bitmap loading, photo id is " + photo.getId());
//
//        return cMarker;
//    }
//
//    @Override
//    public void clearMap() {
//
//    }
//
//    @Override
//    public void trigger() {
////        request(map.getMapStatus());
//    }
//}
