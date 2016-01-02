package com.xiaoguang.searchparking;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.xiaoguang.searchparking.component.CmpUtil;
import com.xiaoguang.searchparking.component.RestComponent;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fm;
    private RestComponent restComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CmpUtil.instance(getApplicationContext()).init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MapFragment mapFragment = MapFragment.newInstance();
        fm = getFragmentManager();
        fm.beginTransaction()
            .replace(R.id.main_container, mapFragment, "MapFragment")
            .commit();

        restComponent = new RestComponent("RestComponent");
        restComponent.open();
    }

    @Override
    protected void onDestroy() {
        restComponent.close();
        super.onDestroy();
    }
}
