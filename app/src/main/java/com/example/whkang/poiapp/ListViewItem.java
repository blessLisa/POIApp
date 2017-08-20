package com.example.whkang.poiapp;

/**
 * Created by whkang on 2017-08-18.
 */

public class ListViewItem {
    private int mThumbnail;
    private String mName;
    private String mAddress;

    public int getThumbnail() {
        return mThumbnail;
    }
    public String getName(){
        return mName;
    }

    public String getAddress(){
        return mAddress;
    }

    public ListViewItem(int thumbnail, String name, String address){
        this.mThumbnail = thumbnail;
        this.mName = name;
        this.mAddress = address;
    }
}
