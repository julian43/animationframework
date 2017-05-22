package com.dookoonu.animationframework;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by cowell on 7/14/15.
 */
public class DownloadImagesTask extends AsyncTask<String, Integer, Integer>  {
    String TAG = "DownloadTask";
    ArrayList<Bitmap> images;
    Boolean done;
    public DownloadImagesTask(ArrayList<Bitmap> bms, Boolean done) {
        images = bms;
        this.done = done;
    }

    @Override
    protected Integer doInBackground(String... urls) {
        int count = urls.length;
        int num=0;
        //long totalSize = 0;

        for (int i = 0; i < count; i++) {
            try {
                images.add(downloadUrlBitmap(new URL(urls[i])));
                num++;
            } catch (IOException e) {
                e.printStackTrace();
            }
            publishProgress((int) ((i / (float) count) * 100));
            // Escape early if cancel() is called
            if (isCancelled()) break;
        }

        return num;
    }


    protected void onProgressUpdate(Integer...progress){

    }

    protected void onPostExecute(int filesDownloaded){
        done = true;
    }

    private Bitmap downloadUrlBitmap(URL url) throws IOException{
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String downloadUrlText(URL url) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string  -------FIXXXXXXXX
            String contentAsString = readIt(is, len);//Bitmap bitmap = BitmapFactory.decodeStream(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
