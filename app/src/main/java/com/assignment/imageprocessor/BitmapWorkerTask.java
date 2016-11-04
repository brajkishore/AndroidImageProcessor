package com.assignment.imageprocessor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by braj.kishore on 2/10/2016.
 */

public  class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG=BitmapWorkerTask.class.getSimpleName();
    private final WeakReference<ImageView> imageViewReference;
    private String imageUrl;

    public BitmapWorkerTask(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        String src = params[0];
        imageUrl=src;

        if(imageUrl==null)
            return  null;

        Bitmap myBitmap=null;

        try {
            Log.d(TAG, "getBitmapFromURL " + src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);
            Log.d(TAG,"Returning from network image cache");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Exception", e.getMessage());
        }
        return null;
    }
    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null && imageView.getTag()!=null &&
                    TextUtils.equals(imageView.getTag().toString(), imageUrl)) {

                    imageView.setImageBitmap(bitmap);

            }
        }
    }
}
