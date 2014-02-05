package com.example.intents.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainActivity extends Activity implements View.OnClickListener{

    private View mGetImage;
    private View mEditImage;

    private final static int REQUEST_CODE_PICK_IMAGE = 1;

    private final static String DEFAULT_IMAGE_DIR = "Launcher_Images";
    private final static String DEFAULT_IMAGE_NAME = "image.tmp";

    Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGetImage = findViewById(R.id.get_image);
        mGetImage.setOnClickListener(this);
        mEditImage = findViewById(R.id.edit_image);
        mEditImage.setOnClickListener(this);

    }


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

    @Override
    public void onClick(View v) {
        if(v.getId() == mGetImage.getId()){
            getImage();
        } else if (v.getId() == mEditImage.getId()) {
            editImage();
        }
    }

    private void getImage() {
        Toast.makeText(this, "get image", Toast.LENGTH_SHORT).show();
        Intent getImageContent = new Intent(Intent.ACTION_GET_CONTENT);
        getImageContent.setType("image/*");
        Intent selector = Intent.createChooser(getImageContent, getString(R.id.get_image));
        startActivityForResult(selector, REQUEST_CODE_PICK_IMAGE);
    }

    private void editImage() {
        Toast.makeText(this, "edit image", Toast.LENGTH_SHORT).show();
        Intent editImage = new Intent(Intent.ACTION_EDIT);
        File imageOutputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), DEFAULT_IMAGE_DIR);
        File outFile = new File(imageOutputDir, DEFAULT_IMAGE_NAME);
        editImage.setDataAndType(Uri.fromFile(outFile), "image/*");
        Intent selector = Intent.createChooser(editImage, getString(R.id.edit_image));
        startActivity(selector);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mUri = null;
        if (intent != null) {
            mUri = intent.getData();
            String path = getPath(mUri);
            File imageInput = new File(path);
            File imageOutputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), DEFAULT_IMAGE_DIR);
            if (!imageOutputDir.exists()) {
                imageOutputDir.mkdir();
            }
            File imageOutput = new File(imageOutputDir, DEFAULT_IMAGE_NAME);
            try {
                copyFile(imageInput, imageOutput);
            } catch (IOException e) {
                Log.e(MainActivity.class.getSimpleName(), "Error getting image from " + mUri.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }
}
