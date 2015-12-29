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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdtech.here.R;
import com.mdtech.here.service.PhotoPublishService;
import com.mdtech.here.ui.BaseActivity;
import com.mdtech.here.util.FileUtils;
import com.mdtech.social.api.model.Image;
import com.mdtech.social.api.model.Location;
import com.mdtech.social.api.model.Photo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import butterknife.Bind;

import static com.mdtech.here.util.LogUtils.LOGD;
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

    // number of images to select
    static final int REQUEST_PICK_IMAGE = 1;
    static final int REQUEST_TAKE_PICTURE = 2;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "HereCamera";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    @Bind(R.id.iv_photo)
    ImageView ivPhoto;
    @Bind(R.id.fab)
    ImageButton btnUpload;
    @Bind(R.id.et_title)
    TextView etTitle;
    @Bind(R.id.et_description)
    TextView etDescription;

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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Photo photo = (Photo) bundle.getSerializable(PhotoPublishService.ARG_PHOTO);
                int resultCode = bundle.getInt(PhotoPublishService.ARG_RESULT);
                if (resultCode == RESULT_OK) {

                    Snackbar.make(CameraActivity.this.btnUpload, "Uploaded: " + photo.getId(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(CameraActivity.this.btnUpload, "Upload fail", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadPhoto();

                Snackbar.make(view, "uploading", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(null == photoUri) {
            if(ARG_PICK_IMAGE.equals(cameraAction)) {
                dispatchPickImageIntent();
            }else if(ARG_TAKE_PICTURE.equals(cameraAction)) {
                dispatchTakePictureIntent();
            }else {
                dispatchTakePictureIntent();
            }
        }
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

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK
                && null != data) {
            photoUri = data.getData();
            loadThumbnailPhoto();
        }else if (requestCode == REQUEST_TAKE_PICTURE && resultCode == RESULT_OK) {
            loadThumbnailPhoto();
        }else {
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
        photoUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_TAKE_PICTURE);
        }
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LOGD(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void uploadPhoto() {
        Image image = new Image();
        image.setTitle(etTitle.getText().toString());
        image.setDescription(etDescription.getText().toString());
        Location location = new Location();

        HashSet<String> tags = new HashSet<String>();

        PhotoPublishService.startService(this, image, location, tags, "26750881779292192047881277021", false, photoUri);
    }
}
