package com.example.intents.launcher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class CameraUtils {

    public static void addPhotoToMediaStoreSynchronously(Context context, Uri uri) {
        MediaScannerConnection.scanFile(context, new String[]{uri.getPath()}, new String[]{"image/*"},
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("MediaStore", "Scanned " + path + ":");
                        Log.i("MediaStore", "-> uri=" + uri);
                    }
                });
    }

    public static boolean hasCamera(Context context) {
        if (context == null || context.getPackageManager() == null) {
            throw new IllegalArgumentException("Context and package manager must not be null");
        }
        PackageManager pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        } else {
            return false;
        }
    }


    public static File getPhotoFile(Context context){
        File path;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = new File(Environment.getExternalStorageDirectory().getPath() + MainActivity.DEFAULT_IMAGE_DIR);
        } else {
            path = new File(context.getFilesDir().getPath());
        }
        path.mkdirs();
        return new File(path, MainActivity.DEFAULT_IMAGE_NAME);
    }

    public static boolean removeTemporaryPhotoFile(File file) {
        boolean success = false;
        try {
            success = file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }
}
