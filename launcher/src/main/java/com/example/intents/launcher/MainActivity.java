package com.example.intents.launcher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener{

    private View mGetImage;
    private View mEditImage;

    private final static int REQUEST_CODE_TAKE_PHOTO = 1;
    private final static int REQUEST_CODE_EDIT_PHOTO = 2;

    public final static String DEFAULT_IMAGE_DIR = "/Launcher_Images/";
    public final static String DEFAULT_IMAGE_NAME = "image.tmp";

    private static final String PHOTO_KEY = "photo_key";

    private File mPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Tell's the activity how to layout the visuals
        setContentView(R.layout.main_activity); // File located in res/layout

        mGetImage = findViewById(R.id.get_image);
        mGetImage.setOnClickListener(this);

        mEditImage = findViewById(R.id.edit_image);
        mEditImage.setOnClickListener(this);

        //If this is not the first time we launched then get the photo file
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PHOTO_KEY)) {
                mPhotoFile = (File) savedInstanceState.getSerializable(PHOTO_KEY);
                setImageView(mPhotoFile);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == mGetImage.getId()){
            getImage();
        } else if (v.getId() == mEditImage.getId()) {
            editImage();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //If we have the photo file then save it on rotation
        if (mPhotoFile != null) {
            outState.putSerializable(PHOTO_KEY, mPhotoFile);
        }
    }

    private void getImage() {
        if (!CameraUtils.hasCamera(this)) {
            Toast.makeText(this, R.string.feature_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mPhotoFile = CameraUtils.getPhotoFile(this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.no_pic_captured, Toast.LENGTH_LONG).show();
            CameraUtils.removeTemporaryPhotoFile(mPhotoFile);
            return;
        }

        Intent cameraIntent = new Intent();
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PHOTO);
        } catch (Exception ex) {
            Toast.makeText(this, R.string.no_capture_activity_found, Toast.LENGTH_SHORT).show();

        }
    }

    private void editImage() {
        Toast.makeText(this, R.string.edit_image, Toast.LENGTH_SHORT).show();

        Intent editImage = new Intent(Intent.ACTION_SEND);
        editImage.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mPhotoFile));
        editImage.setType("image/jpeg");
        Intent selector = Intent.createChooser(editImage, getString(R.id.edit_image));
        startActivity(selector);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_CANCELED && requestCode == REQUEST_CODE_TAKE_PHOTO)
            CameraUtils.removeTemporaryPhotoFile(mPhotoFile);
        else if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {
            //Converts java.net.uri to android.net uri
            CameraUtils.addPhotoToMediaStore(this, Uri.fromFile(mPhotoFile));

            setImageView(mPhotoFile);
        }

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_EDIT_PHOTO) {
            Toast.makeText(this, getString(R.string.edit_image) + "success", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void setImageView(File image) {
        Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());

        ImageView myImage = (ImageView) findViewById(R.id.image_view);
        myImage.setImageBitmap(myBitmap);
    }

    //Boiler plate code below here

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
