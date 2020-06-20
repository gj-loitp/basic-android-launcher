package com.example.akki.basiclauncher;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.List;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    DrawerAdapter drawerAdapterObject;
    GridView drawerGrid;
    SlidingDrawer slidingDrawer;
    RelativeLayout homeView;
    static boolean appLaunchable = true;
    static Pac[] pacForUpdateSerialization;
    Pac[] pacs;
    FavPac[] favPacs;
    PackageManager pm;
    AppWidgetManager mAppWidgetManager;
    LauncherAppWidgetHost mAppWidgetHost;
    int numWidgets = 0;
    SharedPreferences globalPrefs;
    static Activity activity;
    ImageView handler;
    LinearLayout homeItems;
    static DrawerLayout favDrawer;
    static ListView favList;
    FavAppsAdapter favAppsAdapterObject;
    ActionBarDrawerToggle favDrawerToggle;
    Toolbar actionBar;
    static int currentFavAppPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        homeView = (RelativeLayout) findViewById(R.id.home_view);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        homeView.post(new Runnable() {

            @Override
            public void run() {
                homeView.setBackground(wallpaperDrawable);
            }
        });

        actionBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(actionBar);

        activity = this;
        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new LauncherAppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

        globalPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        pm = getPackageManager();
        drawerGrid = (GridView) findViewById(R.id.content);
        slidingDrawer = (SlidingDrawer) findViewById(R.id.drawer);
        handler = (ImageView) findViewById(R.id.handle);
        favDrawer = (DrawerLayout) findViewById(R.id.fav_drawer);
        favList = (ListView) findViewById(R.id.left_drawer);
        favPacs = new FavPac[10];

        favDrawerToggle = new ActionBarDrawerToggle(this, favDrawer, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        favDrawer.setDrawerListener(favDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        favDrawer.setScrimColor(Color.TRANSPARENT);

        /*FavoriteApps favoriteApps = SerializationTools.loadFavApps();
        if(favoriteApps != null) {
            Log.i("INFO", "NOT NULL");
            int count = 0;
            try {
                for (FavPac favPac : favoriteApps.apps) {
                    if(count == 0) {
                        favPacs[count] = new FavPac();
                        favPacs[count].icon = favPac.icon;
                        favPacs[count].label = favPac.label;
                        favPacs[count].name = favPac.name;
                        favPacs[count].packageName = favPac.packageName;
                        count++;

                        favAppsAdapterObject = new FavAppsAdapter(activity, favPacs);
                        favList.setAdapter(favAppsAdapterObject);
                        synchronized (favDrawer) {
                            favDrawer.notify();
                        }
                    }else if(count < 10) {
                        favPacs[count] = new FavPac();
                        favPacs[count].icon = favPac.icon;
                        favPacs[count].label = favPac.label;
                        favPacs[count].name = favPac.name;
                        favPacs[count].packageName = favPac.packageName;
                        count++;

                        favAppsAdapterObject.favPacsForAdapter = favPacs;
                        favList.setAdapter(favAppsAdapterObject);
                        synchronized (favDrawer) {
                            favDrawer.notify();
                        }
                    }
                }
            }catch(NullPointerException e) {
            }
        }else {
            Log.i("INFO", "NULL");
        }*/

        favList.setOnItemClickListener(new FavAppClickListener(MainActivity.activity, favPacs));

        favList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                View favView = getLayoutInflater ().inflate (R.layout.popup_on_fav_icon, null);
                final View favAppRemoveView = view;
                final long favAppRemoveId = id;

                final Dialog mBottomSheetDialog = new Dialog (MainActivity.this, R.style.MaterialDialogSheet);
                favView.setBackgroundColor(Color.WHITE);

                LinearLayout favAppRemove = (LinearLayout) favView.findViewById(R.id.fav_app_remove);
                favAppRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Drawable test = favPacs[position+1].icon;
                        }catch(NullPointerException e) {
                            favPacs[position].icon = null;
                            favPacs[position].label = null;
                            favPacs[position].name = null;
                            favPacs[position].packageName = null;
                        }

                        for(int i = position; i<9; i++) {
                            try {
                                favPacs[i].icon = favPacs[i+1].icon;
                                favPacs[i].label = favPacs[i+1].label;
                                favPacs[i].name = favPacs[i+1].name;
                                favPacs[i].packageName = favPacs[i+1].packageName;

                                favPacs[i+1].icon = null;
                                favPacs[i+1].label = null;
                                favPacs[i+1].name = null;
                                favPacs[i+1].packageName = null;
                            }catch(NullPointerException e) {
                                currentFavAppPosition = i-1;
                                /*favPacs[i].icon = null;
                                favPacs[i].label = null;
                                favPacs[i].name = null;
                                favPacs[i].packageName = null;
                                favPacs[i].iconLocation = null;*/
                            }
                        }

                        favAppsAdapterObject = new FavAppsAdapter(activity, favPacs);
                        favList.setAdapter(favAppsAdapterObject);
                        synchronized (favDrawer) {
                            favDrawer.notify();
                        }
                        mBottomSheetDialog.dismiss();
                    }
                });

                LinearLayout cancelDialog = (LinearLayout) favView.findViewById(R.id.cancel_dialog);
                cancelDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                    }
                });

                //LinearLayout mainLayoutPopupHome = (LinearLayout) view.findViewById(R.id.mainLayout_popupHome);

                mBottomSheetDialog.setContentView (favView);
                mBottomSheetDialog.setCancelable (true);
                mBottomSheetDialog.setCanceledOnTouchOutside(true);
                mBottomSheetDialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mBottomSheetDialog.getWindow ().setGravity (Gravity.BOTTOM);

                mBottomSheetDialog.show ();

                return true;
            }
        });

        drawerGrid.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                slidingDrawer.animateClose();
            }
        });

        homeView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                slidingDrawer.animateOpen();
                Animation show = AnimationUtils.loadAnimation(activity, R.anim.drawer_show);
                slidingDrawer.startAnimation(show);
            }
        });



        new LoadApps().execute();
        addAppsToHome();
        pacForUpdateSerialization = pacs;
        //setPacs(true);

        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                appLaunchable = true;
                handler.setBackground(getResources().getDrawable(R.drawable.right_arrow));
                slidingDrawer.bringToFront();
                getSupportActionBar().hide();
                favDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                //makeTransparent(getWindow());
            }
        });

        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                handler.setBackground(getResources().getDrawable(R.drawable.left_arrow));
                getSupportActionBar().show();
                favDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                //restoreTransparent(getWindow());
            }
        });

        homeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View view = getLayoutInflater ().inflate (R.layout.popup_on_home_icon, null);

                final Dialog mBottomSheetDialog = new Dialog (MainActivity.this,
                        R.style.MaterialDialogSheet);
                view.setBackgroundColor(Color.WHITE);

                LinearLayout addWidget = (LinearLayout) view.findViewById(R.id.add_widget);
                addWidget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectWidget();
                        mBottomSheetDialog.dismiss();
                    }
                });

                LinearLayout addShortcut = (LinearLayout) view.findViewById(R.id.add_shortcut);
                addShortcut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectShortcut();
                        mBottomSheetDialog.dismiss();
                    }
                });

                LinearLayout applyIconTheme = (LinearLayout) view.findViewById(R.id.apply_icon_theme);
                applyIconTheme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectTheme();
                        mBottomSheetDialog.dismiss();
                    }
                });

                LinearLayout cancelDialog = (LinearLayout) view.findViewById(R.id.cancel_dialog);
                cancelDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                    }
                });

                //LinearLayout mainLayoutPopupHome = (LinearLayout) view.findViewById(R.id.mainLayout_popupHome);

                mBottomSheetDialog.setContentView (view);
                mBottomSheetDialog.setCancelable (true);
                mBottomSheetDialog.setCanceledOnTouchOutside(true);
                mBottomSheetDialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mBottomSheetDialog.getWindow ().setGravity (Gravity.BOTTOM);

                mBottomSheetDialog.show ();

                return true;
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(new PacReciever(), filter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        favDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        favDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (favDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    /*public void makeTransparent(Window currentWindow) {
        View decorView = currentWindow.getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void restoreTransparent(Window currentWindow) {
        View decorView = currentWindow.getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
    }*/

    void selectTheme() {
        Intent intent = new Intent(Intent.ACTION_PICK_ACTIVITY);

        Intent filter = new Intent(Intent.ACTION_MAIN);
        filter.addCategory("com.anddoes.launcher.THEME");

        intent.putExtra(Intent.EXTRA_INTENT, filter);

        startActivityForResult(intent, 5);
    }

    void selectShortcut() {
        Intent intent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        intent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        startActivityForResult(intent, 3);
    }

    void selectWidget() {
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, 1);
    }

    void addEmptyData(Intent pickIntent) {
        ArrayList customInfo = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList customExtras = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK ) {
            if (requestCode == 1) {
                configureWidget(data);
            }
            else if (requestCode == 2) {
                createWidget(data);
            }
            else if(requestCode == 3) {
                configureShortcut(data);
            }
            else if(requestCode == 4) {
                createShortcut(data);
            }
            else if(requestCode == 5) {
                globalPrefs.edit().putString("theme", data.getComponent().getPackageName()).commit();
                new LoadApps().execute();
                //setPacs(false);
            }
        }
        else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    void configureShortcut(Intent data) {
        startActivityForResult(data, 4);
    }

    public void createShortcut(Intent intent) {
        Intent.ShortcutIconResource iconResource = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
        Bitmap icon = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
        String shortcutLabel = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        Intent shortIntent = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);

        if (icon==null) {
            if (iconResource != null) {
                Resources resources =null;
                try {
                    resources = pm.getResourcesForApplication(iconResource.packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (resources != null) {
                    int id = resources.getIdentifier(iconResource.resourceName, null, null);
                    if(resources.getDrawable(id) instanceof StateListDrawable) {
                        Drawable d = ((StateListDrawable)resources.getDrawable(id)).getCurrent();
                        icon = ((BitmapDrawable)d).getBitmap();
                    }else {
                        icon = ((BitmapDrawable) resources.getDrawable(id)).getBitmap();
                    }
                }
            }
        }

        if (shortcutLabel != null && shortIntent != null && icon != null) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 100;
            lp.topMargin = 220;

            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout ll = (LinearLayout) li.inflate(R.layout.drawer_items, null);

            ((ImageView)ll.findViewById(R.id.icon_image)).setImageBitmap(icon);
            ((TextView)ll.findViewById(R.id.icon_label)).setText(shortcutLabel);

            ll.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.setOnTouchListener(new AppTouchListenerWS());
                    return true;
                }
            });

            ll.setOnClickListener(new ShortcutClickListener(this));
            ll.setTag(shortIntent);
            homeView.addView(ll, lp);
            slidingDrawer.bringToFront();
        }
    }

    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, 2);
        } else {
            createWidget(data);
        }
    }

    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        final LauncherAppWidgetHostView hostView = (LauncherAppWidgetHostView) mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(homeView.getWidth()/2, homeView.getHeight()/3);
        lp.leftMargin = numWidgets * (homeView.getWidth()/2);
        lp.topMargin = 220;

        hostView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setOnTouchListener(new AppTouchListenerWS());
                return true;
            }
        });

        homeView.addView(hostView, lp);
        slidingDrawer.bringToFront();
        numWidgets++;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    public void removeWidget(AppWidgetHostView hostView) {
        mAppWidgetHost.deleteAppWidgetId(hostView.getAppWidgetId());
        homeView.removeView(hostView);
    }

    public class LoadApps extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> pacsList = pm.queryIntentActivities(mainIntent, 0);
            pacs = new Pac[pacsList.size()];
            pacForUpdateSerialization = new Pac[pacsList.size()];

            for(int i=0; i<pacsList.size(); i++) {
                pacs[i] = new Pac();
                pacs[i].icon = pacsList.get(i).loadIcon(pm);
                pacs[i].packageName = pacsList.get(i).activityInfo.packageName;
                pacs[i].name = pacsList.get(i).activityInfo.name;
                pacs[i].label = pacsList.get(i).loadLabel(pm).toString();

                pacForUpdateSerialization[i] = new Pac();
                pacForUpdateSerialization[i].icon = pacsList.get(i).loadIcon(pm);
                pacForUpdateSerialization[i].packageName = pacsList.get(i).activityInfo.packageName;
                pacForUpdateSerialization[i].name = pacsList.get(i).activityInfo.name;
                pacForUpdateSerialization[i].label = pacsList.get(i).loadLabel(pm).toString();
            }

            new SortApps().exchange_sort(pacs);
            new SortApps().exchange_sort(pacForUpdateSerialization);
            themePacs();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(drawerAdapterObject == null) {
                drawerAdapterObject = new DrawerAdapter(activity, pacs);
                drawerGrid.setAdapter(drawerAdapterObject);
                drawerGrid.setOnItemClickListener(new DrawerClickListener(activity, pacs, pm));
                //drawerGrid.setOnItemLongClickListener(new DrawerLongClickListener(activity, slidingDrawer, homeView, pacs));
                drawerGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                        View appView = getLayoutInflater ().inflate (R.layout.popup_on_app_icon, null);
                        final View addToHomeView = view;
                        final long addToHomeId = id;

                        final String packageName = pacs[position].packageName;

                        final Dialog mBottomSheetDialog = new Dialog (MainActivity.this, R.style.MaterialDialogSheet);
                        appView.setBackgroundColor(Color.WHITE);

                        LinearLayout appInfo = (LinearLayout) appView.findViewById(R.id.app_info);
                        appInfo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    //Open the specific App Info page:
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + packageName));
                                    startActivity(intent);

                                } catch ( ActivityNotFoundException e ) {
                                    //e.printStackTrace();

                                    //Open the generic Apps page:
                                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                    startActivity(intent);

                                }
                                mBottomSheetDialog.dismiss();
                            }
                        });

                        LinearLayout uninstall = (LinearLayout) appView.findViewById(R.id.uninstall);
                        uninstall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DELETE);
                                intent.setData(Uri.parse("package:"+packageName));
                                startActivity(intent);
                                mBottomSheetDialog.dismiss();
                            }
                        });

                        LinearLayout addToHome = (LinearLayout) appView.findViewById(R.id.add_to_home);
                        addToHome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new DrawerLongClickListener(activity, slidingDrawer, homeView, pacs, parent, addToHomeView, position, addToHomeId);
                                mBottomSheetDialog.dismiss();
                            }
                        });

                        final LinearLayout addToFav = (LinearLayout) appView.findViewById(R.id.add_to_fav);
                        addToFav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FavoriteApps objectData = SerializationTools.loadFavApps();

                                if(objectData == null) {
                                    objectData = new FavoriteApps();
                                }

                                if(objectData.apps == null) {
                                    objectData.apps = new ArrayList<FavPac>();
                                }

                                if(favPacs[0] != null) {
                                    int currentPosition = 0;
                                    for(FavPac testPac : favPacs) {
                                        if(testPac != null) {
                                            if(testPac.icon == null) {
                                                break;
                                            }else {
                                                currentPosition++;
                                            }
                                        }
                                    }

                                    if(!(currentPosition >= 10)) {
                                        favPacs[currentPosition] = new FavPac();
                                        favPacs[currentPosition].icon = pacs[position].icon;
                                        favPacs[currentPosition].label = pacs[position].label;
                                        favPacs[currentPosition].name = pacs[position].name;
                                        favPacs[currentPosition].packageName = pacs[position].packageName;

                                        favPacs[currentPosition].cacheIcon();
                                        objectData.apps.add(favPacs[currentPosition]);
                                    }

                                    favAppsAdapterObject.favPacsForAdapter = favPacs;
                                    favList.setAdapter(favAppsAdapterObject);
                                    synchronized (favDrawer) {
                                        favDrawer.notify();
                                    }
                                }else {
                                    favPacs[0] = new FavPac();
                                    favPacs[0].icon = pacs[position].icon;
                                    favPacs[0].label = pacs[position].label;
                                    favPacs[0].name = pacs[position].name;
                                    favPacs[0].packageName = pacs[position].packageName;

                                    favAppsAdapterObject = new FavAppsAdapter(activity, favPacs);
                                    favList.setAdapter(favAppsAdapterObject);
                                    favPacs[0].cacheIcon();
                                    objectData.apps.add(favPacs[0]);
                                    synchronized (favDrawer) {
                                        favDrawer.notify();
                                    }
                                }

                                SerializationTools.serializeFavApps(objectData);

                                mBottomSheetDialog.dismiss();
                            }
                        });

                        LinearLayout cancelDialog = (LinearLayout) appView.findViewById(R.id.cancel_dialog);
                        cancelDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mBottomSheetDialog.dismiss();
                            }
                        });

                        //LinearLayout mainLayoutPopupHome = (LinearLayout) view.findViewById(R.id.mainLayout_popupHome);

                        mBottomSheetDialog.setContentView (appView);
                        mBottomSheetDialog.setCancelable (true);
                        mBottomSheetDialog.setCanceledOnTouchOutside(true);
                        mBottomSheetDialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        mBottomSheetDialog.getWindow ().setGravity (Gravity.BOTTOM);

                        mBottomSheetDialog.show ();

                        return true;
                    }
                });
            }else {
                drawerAdapterObject.pacsForAdapter = pacs;
                drawerAdapterObject.notifyDataSetInvalidated();
            }
        }
    }

    public void addAppsToHome() {
        AppSerializableData data = SerializationTools.loadSerializedData();

        if(data != null) {
            for (Pac pacToAddHome : data.apps) {
                pacToAddHome.addToHome(this, homeView);
            }
        }
    }

    /*public void setPacs(boolean init) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> pacsList = pm.queryIntentActivities(mainIntent, 0);
        pacs = new Pac[pacsList.size()];

        for(int i=0; i<pacsList.size(); i++) {
            pacs[i] = new Pac();
            pacs[i].icon = pacsList.get(i).loadIcon(pm);
            pacs[i].packageName = pacsList.get(i).activityInfo.packageName;
            pacs[i].name = pacsList.get(i).activityInfo.name;
            pacs[i].label = pacsList.get(i).loadLabel(pm).toString();
        }

        new SortApps().exchange_sort(pacs);
        themePacs();

        if(init) {
            drawerAdapterObject = new DrawerAdapter(this, pacs);
            drawerGrid.setAdapter(drawerAdapterObject);
            drawerGrid.setOnItemClickListener(new DrawerClickListener(this, pacs, pm));
            drawerGrid.setOnItemLongClickListener(new DrawerLongClickListener(this, slidingDrawer, homeView, pacs));
        }else {
            drawerAdapterObject.pacsForAdapter = pacs;
            drawerAdapterObject.notifyDataSetInvalidated();
        }

    }*/

    public void themePacs() {
        //theming vars-----------------------------------------------
        final int ICONSIZE = Tools.numtodp(65, MainActivity.this);
        Resources themeRes = null;
        String resPacName =globalPrefs.getString("theme", "");
        String iconResource = null;
        int intres=0;
        int intresiconback = 0;
        int intresiconfront = 0;
        int intresiconmask = 0;
        float scaleFactor = 1.0f;

        Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
        p.setAntiAlias(true);

        Paint origP = new Paint(Paint.FILTER_BITMAP_FLAG);
        origP.setAntiAlias(true);

        Paint maskp= new Paint(Paint.FILTER_BITMAP_FLAG);
        maskp.setAntiAlias(true);
        maskp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        if (resPacName.compareTo("")!=0){
            try{themeRes =pm.getResourcesForApplication(resPacName);}catch(Exception e){};
            if (themeRes!=null){
                String[] backAndMaskAndFront =ThemeTools.getIconBackAndMaskResourceName(themeRes,resPacName);
                if (backAndMaskAndFront[0]!=null)
                    intresiconback=themeRes.getIdentifier(backAndMaskAndFront[0],"drawable",resPacName);
                if (backAndMaskAndFront[1]!=null)
                    intresiconmask=themeRes.getIdentifier(backAndMaskAndFront[1],"drawable",resPacName);
                if (backAndMaskAndFront[2]!=null)
                    intresiconfront=   themeRes.getIdentifier(backAndMaskAndFront[2],"drawable",resPacName);
            }
        }

        BitmapFactory.Options uniformOptions = new BitmapFactory.Options();
        uniformOptions.inScaled=false;
        uniformOptions.inDither=false;
        uniformOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Canvas origCanv;
        Canvas canvas;
        scaleFactor=ThemeTools.getScaleFactor(themeRes,resPacName);
        Bitmap back=null;
        Bitmap mask=null;
        Bitmap front=null;
        Bitmap scaledBitmap = null;
        Bitmap scaledOrig = null;
        Bitmap orig = null;

        if (resPacName.compareTo("")!=0 && themeRes!=null){
            try{
                if (intresiconback!=0)
                    back =BitmapFactory.decodeResource(themeRes,intresiconback,uniformOptions);
            }catch(Exception e){}
            try{
                if (intresiconmask!=0)
                    mask = BitmapFactory.decodeResource(themeRes,intresiconmask,uniformOptions);
            }catch(Exception e){}
            try{
                if (intresiconfront!=0)
                    front = BitmapFactory.decodeResource(themeRes,intresiconfront,uniformOptions);
            }catch(Exception e){}
        }
        //theming vars-----------------------------------------------
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        for(int I=0;I<pacs.length;I++) {
            if (themeRes!=null){
                iconResource=null;
                intres=0;
                iconResource=ThemeTools.getResourceName(themeRes, resPacName, "ComponentInfo{"+pacs[I].packageName+"/"+pacs[I].name+"}");
                if (iconResource!=null){
                    intres = themeRes.getIdentifier(iconResource,"drawable",resPacName);
                }

                if (intres!=0){
                    pacs[I].icon = new BitmapDrawable(BitmapFactory.decodeResource(themeRes,intres,uniformOptions));
                }else{
                    orig=Bitmap.createBitmap(pacs[I].icon.getIntrinsicWidth(), pacs[I].icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    pacs[I].icon.setBounds(0, 0, pacs[I].icon.getIntrinsicWidth(), pacs[I].icon.getIntrinsicHeight());
                    pacs[I].icon.draw(new Canvas(orig));

                    scaledOrig =Bitmap.createBitmap(ICONSIZE, ICONSIZE, Bitmap.Config.ARGB_8888);
                    scaledBitmap = Bitmap.createBitmap(ICONSIZE, ICONSIZE, Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(scaledBitmap);
                    if (back!=null){
                        canvas.drawBitmap(back, Tools.getResizedMatrix(back, ICONSIZE, ICONSIZE), p);
                    }

                    origCanv=new Canvas(scaledOrig);
                    orig=Tools.getResizedBitmap(orig, ((int)(ICONSIZE*scaleFactor)), ((int)(ICONSIZE*scaleFactor)));
                    origCanv.drawBitmap(orig, scaledOrig.getWidth()-(orig.getWidth()/2)-scaledOrig.getWidth()/2 ,scaledOrig.getWidth()-(orig.getWidth()/2)-scaledOrig.getWidth()/2, origP);

                    if (mask!=null){
                        origCanv.drawBitmap(mask,Tools.getResizedMatrix(mask, ICONSIZE, ICONSIZE), maskp);
                    }

                    if (back!=null){
                        canvas.drawBitmap(Tools.getResizedBitmap(scaledOrig,ICONSIZE,ICONSIZE), 0, 0,p);
                    }else
                        canvas.drawBitmap(Tools.getResizedBitmap(scaledOrig,ICONSIZE,ICONSIZE), 0, 0,p);

                    if (front!=null)
                        canvas.drawBitmap(front,Tools.getResizedMatrix(front, ICONSIZE, ICONSIZE), p);

                    pacs[I].icon = new BitmapDrawable(scaledBitmap);
                }
            }
        }

        front=null;
        back=null;
        mask=null;
        scaledOrig=null;
        orig=null;
        scaledBitmap=null;
        canvas=null;
        origCanv=null;
        p=null;
        maskp=null;
        resPacName=null;
        iconResource=null;
        intres=0;
    }

    public class PacReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new LoadApps().execute();
            //setPacs(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        new Pac().cacheIcon();
    }
}
