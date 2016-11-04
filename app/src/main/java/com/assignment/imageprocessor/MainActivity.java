package com.assignment.imageprocessor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

public class MainActivity extends AppCompatActivity {

    private static final String TAG= MainActivity.class.getSimpleName();
    private static final int GALLERY_PICK_IMAGE_REQUEST_CODE = 19;
    private ImageView selectedImg;
    private Button selectImgBtn;
    private Button uploadImgBtn;
    private  Uri fileUri;
    private CoordinatorLayout  coordinatorLayout;
    private static  final String uploadUri="http://localhost:8080/images";
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity=this;
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        selectedImg=(ImageView)findViewById(R.id.selectedImg);
        selectImgBtn=(Button)findViewById(R.id.selectImgBtn);
        uploadImgBtn=(Button)findViewById(R.id.uploadImgBtn);

        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fileUri == null)
                    Snackbar.make(coordinatorLayout, "No Image selected", Snackbar.LENGTH_SHORT).show();
                else {

                    UploadAsyncTask uploadAsyncTask=new UploadAsyncTask(activity, uploadUri, fileUri, new NetworkListener<String,String>() {
                        @Override
                        public void onSuccess(String data) {



                        }

                        @Override
                        public void onFailure(final String data) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MaterialDialog dialog = new MaterialDialog.Builder(activity)
                                            .content(data)
                                            .positiveText("OK")
                                            .show();
                                }
                            });

                        }
                    });

                    uploadAsyncTask.execute();
                }
            }
        });


        selectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_PICK_IMAGE_REQUEST_CODE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GALLERY_PICK_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                     fileUri = data.getData();
                    Log.d(TAG, "GALLERY_PICK_IMAGE_REQUEST_CODE " + fileUri);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selectedImg.setImageURI(fileUri);
                        }
                    });
                }
        }
    }

}
