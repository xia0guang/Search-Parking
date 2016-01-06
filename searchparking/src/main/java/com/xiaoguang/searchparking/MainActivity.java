package com.xiaoguang.searchparking;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;

import com.xiaoguang.searchparking.datalib.ParkingLotsData;
import com.xiaoguang.searchparking.util.CmpUtil;
import com.xiaoguang.searchparking.component.RestComponent;

public class MainActivity extends AppCompatActivity implements MapFragment.MapfragmentListener {

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

    @Override
    public void onListButtonClicked(double lat, double lng) {
        ParkingLotFragment parkingLotFragment = ParkingLotFragment.newInstance(lat, lng);
        fm.beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                .addToBackStack(null)
                .replace(R.id.main_container, parkingLotFragment, "ParkingLotFragment")
                .commit();

    }

    @Override
    public void onMarkerClicked(ParkingLotsData.ParkingLot parkingLot, float zoomRatio, String mileStr) {
        if(parkingLot != null) {
            SingleLotFragment singleLotFragment = SingleLotFragment.newInstance(
                    parkingLot.name,
                    mileStr,
                    (float)parkingLot.rating,
                    parkingLot.address,
                    "Open: Mon-Sun, 00am-23pm",
                    "555-555-5555",
                    "www.google.com",
                    parkingLot.lat,
                    parkingLot.lng,
                    zoomRatio);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                singleLotFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.explode));
            }

            FragmentTransaction ft = getFragmentManager().beginTransaction()
                    .replace(R.id.main_container, singleLotFragment)
                    .addToBackStack("transaction");
            ft.commit();

        }
    }

    @Override
    public void onBackPressed() {
        if(fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
