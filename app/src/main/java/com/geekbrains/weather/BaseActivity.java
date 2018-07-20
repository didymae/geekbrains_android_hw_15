package com.geekbrains.weather;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import es.dmoral.toasty.Toasty;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class BaseActivity extends AppCompatActivity
        implements BaseView.View, BaseFragment.Callback, NavigationView.OnNavigationItemSelectedListener {
    private FloatingActionButton fab;
    private TextView textView;
    private static final String TEXT = "TEXT";
    private static final String DEFAULT_COUNTRY = "Moscow";
    private static final String NAV= "NAV";
    boolean isExistAction;  // Можно ли расположить рядом фрагмент
    boolean isDrawerClose;
    String country;
    String name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        initLayout();
    }

    private void initLayout() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
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
            Log.d(NAV, "nav_settings!!");
            isDrawerClose=true;

        }
        else if (id == R.id.nav_info){
                Toasty.success(this, "About menu!!").show();
                Log.d(NAV, "About!!");
                isDrawerClose=true;

        }
        else if (id == R.id.sub_report){
                Log.d(NAV, "Submit Error!!");
               PopupMenu popupMenu = new PopupMenu(this ,findViewById(R.id.nav_view));
               popupMenu.inflate(R.menu.popup);
               isDrawerClose=false;
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    //handle the inflated menu's buttons here
                    return true;
                }
            });
                popupMenu.show();
            }
        Log.d(NAV, "Close !!");

        if(isDrawerClose){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);}
        return true;
    }


}
