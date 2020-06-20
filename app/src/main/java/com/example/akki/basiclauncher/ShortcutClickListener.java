package com.example.akki.basiclauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by akshay on 19-10-2017.
 */

public class ShortcutClickListener implements View.OnClickListener {
    Context mContext;

    public  ShortcutClickListener(Context c) {
        mContext = c;
    }

    @Override
    public void onClick(View v) {
        Intent data;
        data = (Intent) v.getTag();

        mContext.startActivity(data);
    }
}
