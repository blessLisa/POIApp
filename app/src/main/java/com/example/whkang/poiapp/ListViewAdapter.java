package com.example.whkang.poiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by whkang on 2017-08-18.
 */

public class ListViewAdapter extends BaseAdapter {
    private  LayoutInflater inflater;
    private ArrayList<ListViewItem> item;
    private int layout;

    public  ListViewAdapter(Context context, int layout, ArrayList<ListViewItem> item){
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.item = item;
        this.layout = layout;
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

        ImageView thumbnail = (ImageView)convertView.findViewById(R.id.imageview1);
        if(listViewItem.getThumbnail()!=null) {
            thumbnail.setImageBitmap(listViewItem.getThumbnail());
        }
        TextView name = (TextView)convertView.findViewById(R.id.textview1);
        name.setText(listViewItem.getName());
        TextView address = (TextView)convertView.findViewById(R.id.textview2);
        address.setText(listViewItem.getAddress());

        return convertView;
    }
}
