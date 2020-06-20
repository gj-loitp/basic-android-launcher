package com.example.akki.basiclauncher;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by akshay on 18-10-2017.
 */

public class DrawerLongClickListener {
    Context mContext;
    SlidingDrawer drawerForAdapter;
    RelativeLayout homeViewForAdapter;
    Pac[] pacsForListener;

    public DrawerLongClickListener(Context c, SlidingDrawer slidingDrawer, RelativeLayout homeView, Pac[] pacs, AdapterView<?> parent, View view, int position, long id) {
        mContext = c;
        drawerForAdapter = slidingDrawer;
        homeViewForAdapter = homeView;
        pacsForListener = pacs;

        addToHome(parent, view, position, id);
    }

    public boolean addToHome(AdapterView<?> parent, View view, int position, long id) {
        MainActivity.appLaunchable = false;

        AppSerializableData objectData = SerializationTools.loadSerializedData();

        if(objectData == null) {
            objectData = new AppSerializableData();
        }

        if(objectData.apps == null) {
            objectData.apps = new ArrayList<Pac>();
        }

        Pac pacToAdd = pacsForListener[position];
        pacToAdd.x = (int) view.getX();
        pacToAdd.y = (int) view.getY();
        pacToAdd.position = position;
        MainActivity.pacForUpdateSerialization[position].position = position;

        if(MainActivity.activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            pacToAdd.landscape = true;
        }else {
            pacToAdd.landscape = false;
        }

        pacToAdd.cacheIcon();
        objectData.apps.add(pacToAdd);

        SerializationTools.serializeData(objectData);

        pacToAdd.addToHome(mContext, homeViewForAdapter);
        drawerForAdapter.animateClose();
        drawerForAdapter.bringToFront();

        return false;
    }
}
