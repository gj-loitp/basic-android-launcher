package com.example.akki.basiclauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by akshay on 17-10-2017.
 */

public class DrawerAdapter extends BaseAdapter {
    Context mContext;
    Pac[] pacsForAdapter;

    public DrawerAdapter(Context c, Pac pacs[]) {
        mContext = c;
        pacsForAdapter = pacs;
    }

    @Override
    public int getCount() {
        return pacsForAdapter.length;
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
        ViewHolder viewHolder;
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = li.inflate(R.layout.drawer_items, null);

            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon_image);
            viewHolder.label = (TextView) convertView.findViewById(R.id.icon_label);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.icon.setImageDrawable(pacsForAdapter[position].icon);
        viewHolder.label.setText(pacsForAdapter[position].label);

        return convertView;
    }
}
