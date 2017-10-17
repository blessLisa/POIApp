package com.example.whkang.poiapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by whkang on 2017-08-18.
 */

public class ListViewAdapter extends BaseAdapter {
    private  LayoutInflater inflater;
    private ArrayList<ListViewItem> item;
    private int layout;
    ImageView thumbnail;
    String thumurl;

    public  ListViewAdapter(Context context, int layout, ArrayList<ListViewItem> item){
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.item = item;
        this.layout = layout;
    }

    private void getBitmap(String url) {

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
                    thumbnail.setImageBitmap(result);
                }
            }
        }.execute(thumurl);
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
            convertView=inflater.inflate(layout, parent, false);

        ListViewItem listViewItem=item.get(position);

        thumbnail = (ImageView)convertView.findViewById(R.id.imageview1);
//        thumurl = listViewItem.getThumbnail();
//        getBitmap(thumurl);
        thumbnail.setImageBitmap(listViewItem.getmThumbnailBitmap());

        TextView name = (TextView)convertView.findViewById(R.id.textview1);
        name.setText(listViewItem.getName());
        TextView address = (TextView)convertView.findViewById(R.id.textview2);
        address.setText(listViewItem.getAddress());

        return convertView;
    }
}
