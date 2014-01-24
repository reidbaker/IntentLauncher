package com.example.intents.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{

    private View mGetImage;
    private View mEditImage;

    private final static int REQUEST_CODE_PICK_IMAGE = 1;

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
        Intent selector = Intent.createChooser(getImageContent, "Select Picture");
        startActivityForResult(selector, REQUEST_CODE_PICK_IMAGE);
    }

    private void editImage() {
        Toast.makeText(this, "edit image", Toast.LENGTH_SHORT).show();
        PackageManager pm = getPackageManager();
        if (pm != null) {
            Intent sendIntent = pm.getLaunchIntentForPackage("com.evernote.skitch");
            if (sendIntent != null) {
                sendIntent.setDataAndType(mUri, "image/*");
                sendIntent.setAction(Intent.ACTION_EDIT);
                startActivity(sendIntent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mUri = null;
        if (intent != null) {
            mUri = intent.getData();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
}
