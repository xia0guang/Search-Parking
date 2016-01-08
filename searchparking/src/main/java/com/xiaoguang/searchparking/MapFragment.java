package com.xiaoguang.searchparking;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.transition.TransitionInflater;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xiaoguang.dispatcherlib.Callback;
import com.xiaoguang.dispatcherlib.Dispatcher;
import com.xiaoguang.searchparking.component.Constant;
import com.xiaoguang.searchparking.datalib.Cmd;
import com.xiaoguang.searchparking.datalib.ParkingLotsData;
import com.xiaoguang.searchparking.util.LocationUtil;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapFragment";
    private static final int MAX_RANGE = 40000;
    private static final String PAUSE_STATE = "PauseState";
    private static final String PAUSE_LAT = "Lat";
    private static final String PAUSE_LNG = "Lng";
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Activity mActivity;
    private LocationManager mLocationManager;
    private Location centerLocation;
    private Callback mParkingLotsCallback;
    private LatLng mCenterLatLng;
    private MapFragmentListener mListener;
    private boolean mResumeFromPrev;
    private float mZoomRatio;
    private ParkingLotsData mParkingLotsData;
    private int mSearchRadius = 0;

    public static MapFragment newInstance() {
        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int permissionCheck = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION);
        mResumeFromPrev = false;
        mZoomRatio = 13;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            double pauseLat = savedInstanceState.getDouble(PAUSE_LAT);
            double pauseLng = savedInstanceState.getDouble(PAUSE_LNG);
            mResumeFromPrev = savedInstanceState.getBoolean(PAUSE_STATE);
            mCenterLatLng = new LatLng(pauseLat, pauseLng);
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMapView = (MapView)rootView.findViewById(R.id.google_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        mMapView.setTransitionName(Constant.MAP_VIEW_TRANSITION);

        FloatingActionButton searchButton = (FloatingActionButton) rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if (centerLocation != null) {
                    mCenterLatLng = new LatLng(centerLocation.getLatitude(), centerLocation.getLongitude());
                    sendSearchCurrentRegionRequest();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCenterLatLng, mZoomRatio));
                }
            }
        });

        FloatingActionButton listButton= (FloatingActionButton) rootView.findViewById(R.id.list_button);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location myLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                mListener.onListButtonClicked(myLocation.getLatitude(), myLocation.getLongitude(), mZoomRatio);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mParkingLotsCallback = Dispatcher.instance().register(this, Constant.PARKING_LOT_DATA, new Callback() {
            @Override
            public void onEvent(Object o, String s, Object o1) {
                final ParkingLotsData data = (ParkingLotsData) o1;
                mParkingLotsData = data;
                if (mGoogleMap != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (ParkingLotsData.ParkingLot parkingLot : data.getParkingLots()) {
                                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(parkingLot.lat, parkingLot.lng))
                                        .title(parkingLot.name)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_marker)));
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setIndoorEnabled(true);
        mGoogleMap.setOnCameraChangeListener(this);
        mGoogleMap.setOnMarkerClickListener(this);


        if(mResumeFromPrev || mCenterLatLng != null) {
            Dispatcher.instance().post(this, Constant.ALL_PARKING_DATA_REQUEST, new Cmd.BaseCmd());
        } else {
            centerLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if(centerLocation != null) {
                mCenterLatLng = new LatLng(centerLocation.getLatitude(), centerLocation.getLongitude());
            }
        }
        if(mCenterLatLng != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCenterLatLng, mZoomRatio));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        double tmpLat = marker.getPosition().latitude;
        double tmpLng = marker.getPosition().longitude;
        if(mParkingLotsData != null) {
            ParkingLotsData.ParkingLot parkingLot = mParkingLotsData
                                                    .getExistingParkingLots()
                                                    .get(ParkingLotsData.latLngToHashCode(tmpLat,tmpLng));
            double miles = LocationUtil.distance(parkingLot.lat, mCenterLatLng.latitude, parkingLot.lng, mCenterLatLng.longitude, 0.0, 0.0)/1600;
            String mileStr = String.format("%.1f miles", miles);
            mListener.onMarkerClicked(parkingLot, mZoomRatio, mileStr);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));
            }
        }
        return true;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mCenterLatLng = cameraPosition.target;
        mZoomRatio = cameraPosition.zoom;
        sendSearchCurrentRegionRequest();
    }

    private void sendSearchCurrentRegionRequest() {
        mSearchRadius = rangeOfScreen();
        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;

        if(mSearchRadius >= MAX_RANGE) {
            Toast.makeText(mActivity, "Out of maximum range, please zoom in to search", Toast.LENGTH_LONG).show();
        } else {
            Dispatcher.instance().post(this, Constant.SEARCH_REQUEST, new Cmd.SearchCmd(mCenterLatLng, mSearchRadius, bounds));
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        Dispatcher.instance().unregister(this, Constant.PARKING_LOT_DATA, mParkingLotsCallback);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(PAUSE_STATE, true);
        if(mCenterLatLng != null) {
            outState.putDouble(PAUSE_LAT, mCenterLatLng.latitude);
            outState.putDouble(PAUSE_LNG, mCenterLatLng.longitude);
        }
        super.onSaveInstanceState(outState);
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
        mListener = (MapFragmentListener)mActivity;
    }

    @Override
    public void onDetach() {
        mActivity = null;
        mListener = null;
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

            return (int)LocationUtil.distance(leftUpLL.latitude, rightBottomLL.latitude, leftUpLL.longitude, rightBottomLL.longitude, 0.0, 0.0);
        }
        return 0;
    }

    public interface MapFragmentListener {
        void onListButtonClicked(double lat, double lng, float zoomRatio);
        void onMarkerClicked(ParkingLotsData.ParkingLot parkingLot, float zoomRatio, String mileStr);
    }

}
