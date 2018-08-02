package com.geekbrains.weather.ui.base;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.geekbrains.weather.service.MainService;
import com.geekbrains.weather.service.MyService;
import com.geekbrains.weather.ui.city.CreateActionFragment;
import com.geekbrains.weather.R;
import com.geekbrains.weather.ui.geoweb.GeoFragment;
import com.geekbrains.weather.ui.weather.WeatherFragment;

import es.dmoral.toasty.Toasty;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class BaseActivity extends AppCompatActivity
        implements BaseView.View, BaseFragment.Callback, NavigationView.OnNavigationItemSelectedListener,ActivityCompat.OnRequestPermissionsResultCallback {
    private FloatingActionButton fab;
    private TextView textView;
    private static final String TEXT = "TEXT_LOG";
    private static final String DEFAULT_COUNTRY = "Moscow";
    private static final String NAV= "NAV";
    boolean isExistAction;  // Можно ли расположить рядом фрагмент
    boolean isDrawerClose;
    String country;
    String name;
    NavigationView navigationView;
    private static final int PERMISSION_REQUEST_CODE = 10;
    public final static String BROADCAST_ACTION = "BROADCAST_ACTION";
    public final static String  SENSOR_VAL = "SENSOR_VAL";
    public final static String  SENSOR_TYPE = "SENSOR_TYPE";
    private BroadcastReceiver broadcastReceiver;
    private TextView textTemp;
    private TextView textHumidity;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        initLayout();
        Intent intent = new Intent(BaseActivity.this, MyService.class);
        startService(intent);

        IntentFilter intentValue = new IntentFilter(BROADCAST_ACTION);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String value = String.valueOf(intent.getFloatExtra(SENSOR_VAL, 0));
                int type = intent.getIntExtra(SENSOR_TYPE,0);
                if( getCurrentFragment() instanceof WeatherFragment){
                setSensorValue(value,type);
                Log.d(SENSOR_VAL, value);}

            }


        };
        registerReceiver(broadcastReceiver, intentValue);

    }
    private void setSensorValue(String value, int type) {

        if(Sensor.TYPE_AMBIENT_TEMPERATURE == type){
            textTemp = findViewById(R.id.tv_temperature);
            textTemp.setText(value);

        } else if (type == Sensor.TYPE_RELATIVE_HUMIDITY){
            textHumidity = findViewById(R.id.tv_humidity);
            textHumidity.setText(value);

        }

    }


    private void initLayout() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceMainFragment(new CreateActionFragment());
            }
        });

        View detailsFrame = findViewById(R.id.fl_cont);
        isExistAction = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
        if (isExistAction) {
            // Проверим, что фрагмент существует в активити
            CreateActionFragment detail = (CreateActionFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fl_cont);
            // выводим фрагмент
            if (detail == null) {
                startActionFragment();
            }
        }

        if (getResources().getConfiguration().orientation != ORIENTATION_LANDSCAPE) {
            if (country == null) {
                addMainFragment(WeatherFragment.newInstance(DEFAULT_COUNTRY));}

            else {
                replaceMainFragment(WeatherFragment.newInstance(country));
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private Fragment getCurrentFragment() {
        //получаем наименование фрагмента находящегося в контейнере в данных момент
        return getSupportFragmentManager().findFragmentById(R.id.main_frame);
    }

    @Override
    public Boolean inNetworkAvailable() {
        return true;
    }

    @Override
    public void initDrawer(String username, Bitmap profileImage) {

    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    public void startActionFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_cont, new CreateActionFragment())
                .commit();
    }

    public void addMainFragment(Fragment fragment) {
        //запускаем WeatherFragment и передаем туда country
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_frame, fragment)
                .addToBackStack("")
                .commit();
    }

    public void replaceMainFragment(Fragment fragment) {
        //запускаем WeatherFragment и передаем туда country
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame, fragment)
                .addToBackStack("")
                .commit();
    }



    public Fragment getAnotherFragment() {
        if (getResources().getConfiguration().orientation ==
                ORIENTATION_LANDSCAPE) {
            return getSupportFragmentManager().findFragmentById(R.id.cities);
        } else return getSupportFragmentManager().findFragmentById(R.id.main_frame);

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
// Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_settings) {
            Toasty.success(BaseActivity.this, "Settings menu!!").show();
            isDrawerClose=true;

        }
        else if (id == R.id.nav_info){
                Toasty.success(this, "About menu!!").show();
                isDrawerClose = true;

        } else if (id == R.id.nav_web) {
            replaceMainFragment(new GeoFragment());}

        else if (id == R.id.sub_report){
            PopupMenu popupMenu = new PopupMenu(this, item.getActionView());
            popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
            isDrawerClose = false;
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            });
            popupMenu.show();

        }

        if(isDrawerClose){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);}
        return true;
    }

    public void requestLocationPermission() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
// Запрашиваем разрешения у пользователя
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            Log.d(TEXT, "requestLocationPermission");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        Log.d(TEXT, "onRequestPermissionsResult");

//  Проверяем код чтоб убедиться что это то самое разрешение?
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
//разрешение получено, покажем сообщение

                Log.d(TEXT, "Permission granted");


            } else
                Log.d(TEXT, "Permission not granted");
        }
    }



}
