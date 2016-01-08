/*
 * Copyright (C) 2015 The Here Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mdtech.here.explore;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdtech.here.R;
import com.mdtech.here.service.PhotoPublishService;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.here.util.FileUtils;
import com.mdtech.here.util.MapUtils;
import com.mdtech.social.api.model.Album;
import com.mdtech.social.api.model.Image;
import com.mdtech.social.api.model.Location;
import com.mdtech.social.api.model.Photo;
import com.squareup.picasso.Callback;

import java.math.BigInteger;
import java.util.HashSet;

import butterknife.Bind;

import static com.mdtech.here.util.LogUtils.makeLogTag;

public class CameraActivity extends BaseActivity {
    private static final String TAG = makeLogTag(CameraActivity.class);

    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";
    public static final String ARG_CAMERA_ACTION = "arg_camera_action";
    public static final String ARG_PICK_IMAGE = "arg_pick_image";
    public static final String ARG_TAKE_PICTURE = "arg_take_picture";

    private String cameraAction = ARG_TAKE_PICTURE;

    private Uri photoUri;
    private int photoSize;
    private boolean mUploading = false;

    // number of images to select
    static final int REQUEST_PICK_IMAGE = 1;
    static final int REQUEST_TAKE_PICTURE = 2;
    static final int REQUEST_PICK_ALBUM = 3;

    // permissions request
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Bind(R.id.iv_photo)
    ImageView ivPhoto;
    @Bind(R.id.fab)
    ImageButton mBtnUpload;
    @Bind(R.id.et_title)
    TextView etTitle;
    @Bind(R.id.et_description)
    TextView etDescription;
    @Bind(R.id.btn_location)
    Button btnLocation;
    @Bind(R.id.btn_album)
    Button btnAlbum;

    private Location mLocation;
    private Album mAlbum;

    // Acquire a reference to the system Location Manager
    LocationManager locationManager;

    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri) {
        Intent intent = new Intent(openingActivity, CameraActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        openingActivity.startActivity(intent);
    }

    public static void openPickImage(Activity openingActivity) {
        Intent intent = new Intent(openingActivity, CameraActivity.class);
        intent.putExtra(ARG_CAMERA_ACTION, ARG_PICK_IMAGE);
        openingActivity.startActivity(intent);
    }

    public static void openTakePicture(Activity openingActivity) {
        Intent intent = new Intent(openingActivity, CameraActivity.class);
        intent.putExtra(ARG_CAMERA_ACTION, ARG_TAKE_PICTURE);
        openingActivity.startActivity(intent);
    }

    /**
     * 接收上传结果消息
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Photo photo = (Photo) bundle.getSerializable(PhotoPublishService.ARG_PHOTO);
                int resultCode = bundle.getInt(PhotoPublishService.ARG_RESULT);
                if (resultCode == RESULT_OK) {
                    Snackbar.make(CameraActivity.this.mBtnUpload, "Uploaded: " + photo.getId(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    updateUploadUI(false);
                    Snackbar.make(CameraActivity.this.mBtnUpload, "Upload fail", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        setupAppbar(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navigateUpOrBack(CameraActivity.this, null);
//            }
//        });

        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);

        mBtnUpload.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
        btnLocation.setOnClickListener(this);

        // photo
        if (savedInstanceState == null) {
            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
            cameraAction = getIntent().getStringExtra(ARG_CAMERA_ACTION);
        } else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
        }

        ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                loadThumbnailPhoto();
                return true;
            }
        });

        registerReceiver(receiver, new IntentFilter(PhotoPublishService.NOTIFICATION));

        // location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted.
                    locate();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (null == photoUri) {
            if (ARG_PICK_IMAGE.equals(cameraAction)) {
                dispatchPickImageIntent();
            } else if (ARG_TAKE_PICTURE.equals(cameraAction)) {
                dispatchTakePictureIntent();
            } else {
                dispatchTakePictureIntent();
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_album:
                startActivityForResult(new Intent(this, AlbumSearchActivity.class), REQUEST_PICK_ALBUM);
                break;
            case R.id.btn_location:
                startActivity(new Intent(this, AlbumSearchActivity.class));
                break;
            case R.id.fab:
                uploadPhoto(v);
                break;
            default:
        }
    }

    private void loadImageInfo() {
        Location location = MapUtils.getImageLocation(FileUtils.getPath(this, photoUri));
        if(null == location) {
            locate();
        }else {
            setLocation(location);
        }

        loadThumbnailPhoto();
    }

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
        picasso.load(photoUri)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivPhoto.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(PhotoPublishService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_IMAGE && null != data) {
                photoUri = data.getData();
                loadImageInfo();
            } else if (requestCode == REQUEST_TAKE_PICTURE) {
                loadImageInfo();
            } else if(requestCode == REQUEST_PICK_ALBUM) {
                String id = data.getStringExtra(AlbumSearchActivity.ARG_ALBUM_ID);
                updateAlbum(id);
            }
        }
        else if(requestCode == REQUEST_PICK_IMAGE || requestCode == REQUEST_TAKE_PICTURE){
            finish();
        }
    }

    private void dispatchPickImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoUri = FileUtils.getOutputMediaFileUri(FileUtils.MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_TAKE_PICTURE);
        }
    }

    /**
     * 上传图片
     */
    private void uploadPhoto(View v) {
        Image image = new Image();
        image.setTitle(etTitle.getText().toString());
        image.setDescription(etDescription.getText().toString());
        Location location = mLocation;

        HashSet<String> tags = new HashSet<String>();

        if(null == mAlbum) {
            Snackbar.make(v, getString(R.string.info_album_empty), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        if(null == location) {
            location = new Location();
        }
        updateUploadUI(true);
        PhotoPublishService.startService(this, image, location, tags, mAlbum.getId().toString(), false, photoUri);
        Snackbar.make(v, getString(R.string.info_uploading), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void updateUploadUI(boolean uploading) {
        mUploading = uploading;
        if(uploading) {
            mBtnUpload.setClickable(false);
        }else {
            mBtnUpload.setClickable(true);
        }
    }

    /**
     * 定位设备现在的位置
     */
    private void locate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                setLocation(location);
                // TODO remove listener
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    /**
     * 设置gps位置
     * @param location
     */
    private void setLocation(Location location) {
        mLocation = location;
        btnLocation.setText(location.getPosition()[0] + "," + location.getPosition()[1]);
        MapUtils.newInstance().reverseGeoCode(new MapUtils.OnDeGeoCodeListener() {
            @Override
            public void onDeGeoCodeResult(Location location) {
                if (!TextUtils.isEmpty(location.getAddress())) {
                    btnLocation.setText(location.getAddress());
                    mLocation = location;
                }
            }
        }, location);
    }

    /**
     * 设置gps位置
     * @param location
     */
    private void setLocation(android.location.Location location) {
        setLocation(new Location(location.getLongitude(), location.getLatitude()));
    }

    private void updateAlbum(String id) {
        btnAlbum.setText(id);
        mAlbum = new Album();
        mAlbum.setId(new BigInteger(id));
    }
}
