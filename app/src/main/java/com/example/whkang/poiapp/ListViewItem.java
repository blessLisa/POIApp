package com.example.whkang.poiapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by whkang on 2017-08-18.
 */

public class ListViewItem {
    private String mThumbnail;
    private String mName;
    private String mAddress;
    private Bitmap mThumbnailBitmap;

    public Bitmap getThumbnailBitmap() { return mThumbnailBitmap; }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public  String getThumbnail(){ return mThumbnail; }

    public ListViewItem(String photoUrl, String name, String address) {
        this.mThumbnail = photoUrl;
        this.mName = name;
        this.mAddress = address;
    }

    private void getBitmap(String url) {
        if(url == null)
            return;

        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... urls) {
                URL imgUrl = null;
                HttpURLConnection connection = null;
                InputStream is = null;

                Bitmap retBitmap = null;
                try {
                    imgUrl = new URL(urls[0]);
                    connection = (HttpURLConnection) imgUrl.openConnection();
                    connection.setDoInput(true); //url로 input받는 flag 허용
                    connection.connect(); //연결
                    is = connection.getInputStream(); // get inputstream
                    retBitmap = BitmapFactory.decodeStream(is);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    return retBitmap;
                }
            }

            protected void onPostExecute(Bitmap result) {
                if (result != null) {
                    mThumbnailBitmap = result;
                }
            }
        }.execute(mThumbnail);
    }


}
