package com.example.akki.basiclauncher;

import android.content.res.Configuration;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

/**
 * Created by akshay on 18-10-2017.
 */

public class AppTouchListener implements View.OnTouchListener {
    int leftMargin;
    int topMargin;
    int removeX;
    int removeY;
    int oldX;
    int oldY;
    int newX;
    int newY;
    RelativeLayout homeViewForAdapter;
    LinearLayout removeItems;

    public AppTouchListener(int x, int y) {
        oldX = x;
        oldY = y;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        homeViewForAdapter = (RelativeLayout) MainActivity.activity.findViewById(R.id.home_view);
        removeItems = (LinearLayout) MainActivity.activity.findViewById(R.id.remove_items);
        RelativeLayout.LayoutParams lp;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                homeViewForAdapter.findViewById(R.id.remove_items).setVisibility(View.VISIBLE);
                ((AppCompatActivity) MainActivity.activity).getSupportActionBar().hide();

                removeX = (int) removeItems.getX();
                removeY = (int) removeItems.getY() + (removeItems.getHeight());

                lp = new RelativeLayout.LayoutParams(v.getWidth(), v.getHeight());

                leftMargin = (int) event.getRawX() - v.getWidth()/2;
                topMargin = (int) event.getRawY() - v.getHeight()/2;

                if(leftMargin + v.getWidth() > v.getRootView().getWidth()) {
                    leftMargin = v.getRootView().getWidth() - v.getWidth();
                }

                if(leftMargin <0) {
                    leftMargin = 0;
                }

                if(topMargin + v.getHeight() > ((View) v.getParent()).getHeight()) {
                    topMargin = ((View) v.getParent()).getHeight() - v.getHeight();
                }

                if(topMargin < 0) {
                    topMargin = 0;
                }

                if(topMargin <= removeY) {
                    v.bringToFront();
                    removeItems.setBackgroundColor(MainActivity.activity.getResources().getColor(R.color.redTransparent));
                    removeItems.findViewById(R.id.remove_items_icon).setBackground(MainActivity.activity.getResources().getDrawable(R.drawable.removehomeitems_icon_white));
                }else {
                    removeItems.setBackgroundColor(MainActivity.activity.getResources().getColor(R.color.blackTransparent));
                    removeItems.findViewById(R.id.remove_items_icon).setBackground(MainActivity.activity.getResources().getDrawable(R.drawable.removehomeitems_icon_red));
                }

                lp.leftMargin = leftMargin;
                lp.topMargin = topMargin;
                v.setLayoutParams(lp);
                break;
            case MotionEvent.ACTION_UP:
                v.setOnTouchListener(null);

                newX = (int) v.getX();
                newY = (int) v.getY();

                if(topMargin <= removeY) {
                    new Pac().removeFromHome(homeViewForAdapter, v);
                    UpdateSerializedData.removeSerializedData(v, homeViewForAdapter, oldX, oldY);
                }else {
                    removeItems.setBackgroundColor(MainActivity.activity.getResources().getColor(R.color.blackTransparent));
                    removeItems.findViewById(R.id.remove_items_icon).setBackground(MainActivity.activity.getResources().getDrawable(R.drawable.removehomeitems_icon_red));
                    UpdateSerializedData.update(v, oldX, oldY, newX, newY);
                }

                homeViewForAdapter.findViewById(R.id.remove_items).setVisibility(View.INVISIBLE);
                ((AppCompatActivity) MainActivity.activity).getSupportActionBar().show();
                break;
        }
        return true;
    }
}
