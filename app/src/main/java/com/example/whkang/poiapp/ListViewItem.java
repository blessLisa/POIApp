package com.example.whkang.poiapp;

/**
 * Created by whkang on 2017-08-18.
 */

public class ListViewItem {
    private String mThumbnail;
    private String mName;
    private String mAddress;

    public String getThumbnail() {

        return mThumbnail;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public ListViewItem(String thumbnail, String name, String address) {
        this.mThumbnail = thumbnail;
        this.mName = name;
        this.mAddress = address;
    }


}
