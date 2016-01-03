package com.xiaoguang.searchparking;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xiaoguang.dispatcherlib.Callback;
import com.xiaoguang.dispatcherlib.Dispatcher;
import com.xiaoguang.searchparking.component.Constant;
import com.xiaoguang.searchparking.datalib.Cmd;
import com.xiaoguang.searchparking.datalib.ParkingLotsData;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    private static final String TAG = "MapFragment";
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Activity mActivity;
    private LocationManager mLocationManager;
    private Location centerLocation;
    private Callback mParkingLotsCallback;
    private LatLng centerLatLng;

    public static MapFragment newInstance() {
        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMapView = (MapView)rootView.findViewById(R.id.google_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        FloatingActionButton searchButton = (FloatingActionButton) rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if(centerLocation != null) {
                    centerLatLng= new LatLng(centerLocation.getLatitude(), centerLocation.getLongitude());
                    Dispatcher.instance().post(this, Constant.SEARCH_REQUEST, new Cmd.SearchCmd(centerLatLng, rangeOfScreen()));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 13));
                }
            }
        });

        FloatingActionButton listButton= (FloatingActionButton) rootView.findViewById(R.id.list_button);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mParkingLotsCallback = Dispatcher.instance().register(this, Constant.PARKINGLOT_DATA, new Callback() {
            @Override
            public void onEvent(Object o, String s, Object o1) {
                final ParkingLotsData data = (ParkingLotsData) o1;
                if (mGoogleMap != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (ParkingLotsData.ParkingLot parkingLot : data.getParkingLots()) {
                                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(parkingLot.lat, parkingLot.lng))
                                        .title(parkingLot.name));
                            }
                        }
                    });
                }
            }
        });

//        if(centerLocation != null) {
//            Dispatcher.instance().post(this, Constant.SEARCH_REQUEST, new Cmd.SearchCmd(centerLocation));
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setIndoorEnabled(true);
        mGoogleMap.setOnCameraChangeListener(this);

        centerLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if(centerLocation != null) {
            centerLatLng= new LatLng(centerLocation.getLatitude(), centerLocation.getLongitude());
            int searchRadius = rangeOfScreen();
            Dispatcher.instance().post(this, Constant.SEARCH_REQUEST, new Cmd.SearchCmd(centerLatLng, searchRadius));
            Log.d(TAG, "PostSearchRsqOnReady: " + searchRadius);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 13));
        }

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        centerLatLng = cameraPosition.target;
        int searchRadius = rangeOfScreen();
        Dispatcher.instance().post(this, Constant.SEARCH_REQUEST, new Cmd.SearchCmd(centerLatLng, searchRadius));
        Log.d(TAG, "PostSearchRsqOnChange: " + searchRadius);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        Dispatcher.instance().unregister(this, Constant.PARKINGLOT_DATA, mParkingLotsCallback);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = getActivity();
        mLocationManager = (LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    private int rangeOfScreen(){
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point rightBottom = new Point();
        display.getSize(rightBottom);
        Point leftUp = new Point(0,0);
        if(mGoogleMap != null) {
            Projection curProjection = mGoogleMap.getProjection();
            LatLng leftUpLL = curProjection.fromScreenLocation(leftUp);
            LatLng rightBottomLL = curProjection.fromScreenLocation(rightBottom);
            return (int)distance(leftUpLL.latitude, rightBottomLL.latitude, leftUpLL.longitude, rightBottomLL.longitude, 0.0, 0.0);
        }
        return 0;
    }

    private double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

}
