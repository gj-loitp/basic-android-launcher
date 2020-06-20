package com.example.akki.basiclauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by akshay on 24-10-2017.
 */

public class FavAppClickListener implements ListView.OnItemClickListener {
    Context mContext;
    FavPac[] favPacForListener;

    public FavAppClickListener(Context c, FavPac[] favPac) {
        mContext = c;
        favPacForListener = favPac;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cp = new ComponentName(favPacForListener[position].packageName, favPacForListener[position].name);
        launchIntent.setComponent(cp);

        mContext.startActivity(launchIntent);
    }
}
