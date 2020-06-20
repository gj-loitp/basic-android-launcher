package com.example.akki.basiclauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by akshay on 18-10-2017.
 */

public class DrawerClickListener implements AdapterView.OnItemClickListener {
    Context mContext;
    Pac[] pacsForListener;
    PackageManager pmForListener;

    public DrawerClickListener(Context c, Pac[] pacs, PackageManager pm) {
        mContext = c;
        pacsForListener = pacs;
        pmForListener = pm;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(MainActivity.appLaunchable) {
            Intent launchIntent = new Intent(Intent.ACTION_MAIN);
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cp = new ComponentName(pacsForListener[position].packageName, pacsForListener[position].name);
            launchIntent.setComponent(cp);

            mContext.startActivity(launchIntent);
        }
    }
}
