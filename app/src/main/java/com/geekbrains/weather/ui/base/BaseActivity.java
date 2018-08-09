package com.geekbrains.weather.ui.base;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.os.Build;
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

import com.geekbrains.weather.Constants;
import com.geekbrains.weather.data.PrefsData;
import com.geekbrains.weather.data.PrefsHelper;
import com.geekbrains.weather.data.data.DataManager;
import com.geekbrains.weather.data.data.IDataManager;
import com.geekbrains.weather.model.weather.WeatherRequest;
import com.geekbrains.weather.service.MainService;
import com.geekbrains.weather.service.MyService;
import com.geekbrains.weather.ui.city.CreateActionFragment;
import com.geekbrains.weather.R;
import com.geekbrains.weather.ui.geoweb.GeoFragment;
import com.geekbrains.weather.ui.geoweb.OkHttpRequester;
import com.geekbrains.weather.ui.plan.PlanFragment;
import com.geekbrains.weather.ui.temperature.TemperatureFragment;
import com.geekbrains.weather.ui.weather.WeatherFragment;
import com.geekbrains.weather.ui.login.LoginFragment;

import es.dmoral.toasty.Toasty;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static com.geekbrains.weather.Constants.KEYAPI;
import static com.geekbrains.weather.Constants.SMS_CODE;
import static com.geekbrains.weather.Constants.TEMP_K;

public class BaseActivity extends AppCompatActivity
        implements BaseView.View, BaseFragment.Callback, NavigationView.OnNavigationItemSelectedListener,ActivityCompat.OnRequestPermissionsResultCallback {
    private FloatingActionButton fab;
    private static final String TEXT = "TEXT_LOG";
    private static final String DEFAULT_CITY = "Moscow";
    boolean isExistAction;  // Можно ли расположить рядом фрагмент
    boolean isDrawerClose;
    String country;
    NavigationView navigationView;
    private static final int PERMISSION_REQUEST_CODE = 10;
    private static final int PERMISSION_SMS_REQUEST_CODE = 11;
    public final static String BROADCAST_ACTION = "BROADCAST_ACTION";
    public final static String  SENSOR_VAL = "SENSOR_VAL";
    public final static String  SENSOR_TYPE = "SENSOR_TYPE";
    private TextView textTemp;
    private TextView textHumidity;
    private PrefsHelper prefsHelper;
    private TextView textWind;
    private TextView textPressure;
    private TextView textFeelsLike;
    private TextView textVisibility;
    private TextView textPrecipitation;

    private static final String MY_ACTION = "MY_ACTION";
    private static final String MY_EXTRA = "MY_EXTRA";
    private String myData = "myData";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        prefsHelper = new PrefsData(this);

        //инициализируем layout
        initLayout();

        //получаем данные о погоде
        initRetrofitComponent();

        //тестовая служба сбор данных с датчиков.
        startService();

        //broadcast
        startReceiver();


    }

    private void startReceiver() {
        Intent intent = new Intent(MY_ACTION);
        intent.putExtra(MY_EXTRA, myData);
        sendBroadcast(intent);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String testAns = intent.getStringExtra("Test");
            }
        };
        IntentFilter intentFilter = new IntentFilter("com.geekbrains.weather.receiver.SMSReceiver");
        registerReceiver(broadcastReceiver, intentFilter);

    }

    private void startService(){

        Intent intent = new Intent(BaseActivity.this, MyService.class);
        startService(intent);

        IntentFilter intentValue = new IntentFilter(BROADCAST_ACTION);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String value = String.valueOf(intent.getFloatExtra(SENSOR_VAL, 0));
                int type = intent.getIntExtra(SENSOR_TYPE,0);
                if( getCurrentFragment() instanceof WeatherFragment){
                //     setSensorValue(value,type);
                }
            }
        };
        registerReceiver(broadcastReceiver, intentValue);

    }

    private void initRetrofitComponent() {
        IDataManager dataManager = new DataManager(new DataManager.OnResponseCompleted() {
            @Override
            public void onCompleted(WeatherRequest response) {
                if( getCurrentFragment() instanceof WeatherFragment){

                    textHumidity = findViewById(R.id.tv_humidity);
                    textTemp = findViewById(R.id.tv_temperature);
                    textWind = findViewById(R.id.tv_wind);
                    textVisibility = findViewById(R.id.tv_visibility);
                    textPrecipitation = findViewById(R.id.tv_precipitation);
                    textPressure = findViewById(R.id.tv_pressure);
                    textFeelsLike = findViewById(R.id.tv_feels_like);

                    textTemp.setText(Long.toString(Math.round(response.getMain().getTemp()-TEMP_K)));
                    textHumidity.setText(Double.toString(response.getMain().getHumidity()));
                    textPressure.setText(Double.toString(response.getMain().getPressure()));
                    textWind.setText(Double.toString(response.getWind().getSpeed()));
                    textVisibility.setText(Double.toString(response.getVisibility()));
                }
            }
        });
        String city;
        if (!prefsHelper.getSharedPreferences(Constants.CITY).equals("")) {
            city = prefsHelper.getSharedPreferences(Constants.CITY);
        } else city = DEFAULT_CITY;

        dataManager.initRetrofit();
        dataManager.requestRetrofit(city+",ru", KEYAPI);
    }

   /* private void setSensorValue(String value, int type) {

        if(Sensor.TYPE_AMBIENT_TEMPERATURE == type){
            textTemp = findViewById(R.id.tv_temperature);
            textTemp.setText(value);

        } else if (type == Sensor.TYPE_RELATIVE_HUMIDITY){
            textHumidity = findViewById(R.id.tv_humidity);
            textHumidity.setText(value);

        }

    }*/


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
        if(prefsHelper.getSharedPreferences(Constants.SMS_CODE).equals("")){
            addMainFragment(new LoginFragment());
        } else if (getResources().getConfiguration().orientation != ORIENTATION_LANDSCAPE) {
            if (country == null) {
                addMainFragment(WeatherFragment.newInstance(DEFAULT_CITY));}
            else {
                replaceMainFragment(WeatherFragment.newInstance(country));
            }
        }
    }


    @Override
    public void onBackPressed() {
        //закрываем drawer если он был открыт при нажатии на аппаратную клавишу назад
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getCurrentFragment() instanceof CreateActionFragment) {
            addMainFragment(new WeatherFragment());
        } else if (getCurrentFragment() instanceof GeoFragment) {
            addMainFragment(new WeatherFragment());
        } else if (getCurrentFragment() instanceof PlanFragment) {
            addMainFragment(new WeatherFragment());
        } else if (getCurrentFragment() instanceof TemperatureFragment) {
            addMainFragment(new WeatherFragment());
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
            //    .addToBackStack("")
                .commit();
    }

    public void replaceMainFragment(Fragment fragment) {
        //запускаем WeatherFragment и передаем туда country
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame, fragment)
           //     .addToBackStack("")
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

        } else if (id == R.id.nav_info){
                Toasty.success(this, "About menu!!").show();
                isDrawerClose = true;
        } else if (id == R.id.nav_web) {
            replaceMainFragment(new GeoFragment());
        } else if (id == R.id.nav_note) {
            replaceMainFragment(new PlanFragment());
        } else if (id == R.id.sub_report){
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

    public void requestReceiveSMSPermission() {

        if (shouldAskPermission(this,Manifest.permission.RECEIVE_SMS)) {
// Запрашиваем разрешения у пользователя
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.RECEIVE_SMS}, PERMISSION_SMS_REQUEST_CODE);
            Log.d(TEXT, "requestReceiveSMSPermission");
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

    public static boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }
    private static boolean shouldAskPermission(Context context, String permission){
        if (shouldAskPermission()) {
            int permissionResult = ActivityCompat.checkSelfPermission(context, permission);
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

}
