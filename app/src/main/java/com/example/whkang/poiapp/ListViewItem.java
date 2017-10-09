package com.example.whkang.poiapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

    public Bitmap getThumbnail() {
        Bitmap mBitmap = getBitmap(mThumbnail);

        return mBitmap;
    }
    public String getName(){
        return mName;
    }

    public String getAddress(){
        return mAddress;
    }

    public ListViewItem(String thumbnail, String name, String address){
        this.mThumbnail = thumbnail;
        this.mName = name;
        this.mAddress = address;
    }

    private Bitmap getBitmap(String url) {
        URL imgUrl = null;
        HttpURLConnection connection = null;
        InputStream is = null;

        Bitmap retBitmap = null;

        try{
            imgUrl = new URL(url);
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true); //url로 input받는 flag 허용
            connection.connect(); //연결
            is = connection.getInputStream(); // get inputstream
            retBitmap = BitmapFactory.decodeStream(is);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if(connection!=null) {
                connection.disconnect();
            }
            return retBitmap;
        }
    }

}
