package com.example.akki.basiclauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by akshay on 23-10-2017.
 */

public class FavAppsAdapter extends BaseAdapter {
    Context mContext;
    FavPac[] favPacsForAdapter;

    public FavAppsAdapter(Context c, FavPac[] favPacs) {
        mContext = c;
        favPacsForAdapter = favPacs;
    }

    @Override
    public int getCount() {
        return favPacsForAdapter.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView label;
        ImageView icon;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FavAppsAdapter.ViewHolder viewHolder;
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = li.inflate(R.layout.fav_apps_layout, null);

            viewHolder = new FavAppsAdapter.ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.fav_icon);
            viewHolder.label = (TextView) convertView.findViewById(R.id.fav_label);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (FavAppsAdapter.ViewHolder) convertView.getTag();
        }

        if(favPacsForAdapter[position] != null) {
            viewHolder.icon.setImageDrawable(favPacsForAdapter[position].icon);
            viewHolder.label.setText(favPacsForAdapter[position].label);
        }

        return convertView;
    }
}
