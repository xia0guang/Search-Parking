package com.xiaoguang.searchparking;

import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.View;

import com.xiaoguang.searchparking.component.Constant;
import com.xiaoguang.searchparking.datalib.ParkingLotsData;
import com.xiaoguang.searchparking.util.CmpUtil;
import com.xiaoguang.searchparking.component.RestComponent;

public class MainActivity extends AppCompatActivity implements MapFragment.MapFragmentListener, ParkingLotFragment.OnListFragmentListener{

    private FragmentManager fm;
    private RestComponent restComponent;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CmpUtil.instance(getApplicationContext()).init();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if(!isNwConnected(this)) {
            Snackbar.make(findViewById(R.id.main_layout), "Please try again when connect to network", Snackbar.LENGTH_LONG).show();
        }

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
    public void onListButtonClicked(double lat, double lng, float zoomRatio) {
        ParkingLotFragment parkingLotFragment = ParkingLotFragment.newInstance(lat, lng, zoomRatio);
        fm.beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                .addToBackStack(null)
                .replace(R.id.main_container, parkingLotFragment, "ParkingLotFragment")
                .commit();
        setBackArrowVisible(true);

    }

    @Override
    public void onMarkerClicked(ParkingLotsData.ParkingLot parkingLot, float zoomRatio, String mileStr) {
        openSingleLotFragment(null, parkingLot, Constant.NOT_IN_LIST, mileStr, zoomRatio);
    }

    @Override
    public void onListItemClicked(View view, ParkingLotsData.ParkingLot parkingLot, int position, String mileStr, float zoomRatio) {
        openSingleLotFragment(view, parkingLot, position, mileStr, zoomRatio);
    }

    private void openSingleLotFragment(View view, ParkingLotsData.ParkingLot parkingLot, int position, String mileStr, float zoomRatio) {
        if(parkingLot != null) {
            SingleLotFragment singleLotFragment = SingleLotFragment.newInstance(
                    parkingLot.name,
                    mileStr,
                    (float)parkingLot.rating,
                    parkingLot.address,
                    parkingLot.isOpen,
                    parkingLot.displayPhone,
                    parkingLot.phoneNumber,
                    parkingLot.url,
                    parkingLot.lat,
                    parkingLot.lng,
                    zoomRatio,
                    position);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if(position == Constant.NOT_IN_LIST) {
                    View mapView = findViewById(R.id.google_map);
                    singleLotFragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.trans_move));
                    singleLotFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.explode));
                    fm.beginTransaction()
                            .replace(R.id.main_container, singleLotFragment)
                            .addToBackStack(null)
                            .addSharedElement(mapView, mapView.getTransitionName())
                            .commit();
                } else {
                    View nameView = view.findViewById(R.id.name);
                    View milesView = view.findViewById(R.id.miles);
                    View ratingView = view.findViewById(R.id.rating_bar);
                    View addressView = view.findViewById(R.id.address);
                    View cardView = view.findViewById(R.id.card_view);

                    singleLotFragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.trans_move));
                    singleLotFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.explode));


                    fm.beginTransaction()
                            .replace(R.id.main_container, singleLotFragment)
                            .addToBackStack(null)
                            .addSharedElement(nameView, nameView.getTransitionName())
                            .addSharedElement(milesView, milesView.getTransitionName())
                            .addSharedElement(ratingView, ratingView.getTransitionName())
                            .addSharedElement(addressView, addressView.getTransitionName())
                            .addSharedElement(cardView, cardView.getTransitionName())
                            .commit();
                }

            } else {
                fm.beginTransaction()
                        .replace(R.id.main_container, singleLotFragment)
                        .addToBackStack(null)
                        .commit();

            }
            setBackArrowVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        if(fm.getBackStackEntryCount() > 0) {
            if(fm.getBackStackEntryCount() == 1) {
                setBackArrowVisible(false);
            }
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void setBackArrowVisible(Boolean visible){
        if(visible) {
            mToolbar.setNavigationIcon(R.drawable.back_arrow);
            mToolbar.setNavigationContentDescription("Back");
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            mToolbar.setNavigationIcon(null);
            mToolbar.setNavigationContentDescription("Home");
        }
    }

    public static boolean isNwConnected(Context context) {
        if (context == null) {
            return true;
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
        if (nwInfo != null && nwInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
