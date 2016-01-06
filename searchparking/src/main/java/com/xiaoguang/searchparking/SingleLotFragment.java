package com.xiaoguang.searchparking;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xiaoguang.searchparking.component.Constant;


public class SingleLotFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "SingleLotFragment";
    private TextView nameView;
    private RatingBar ratingBarView;
    private TextView hoursView;
    private TextView callNumberView;
    private TextView moreInfoView;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private TextView addressView;
    private TextView milesView;

    public static SingleLotFragment newInstance(String name,
                                                String mileStr,
                                                float rating,
                                                String address,
                                                String hours,
                                                String callNumber,
                                                String yelpUrl,
                                                double lat,
                                                double lng,
                                                float zoomRatio) {
        SingleLotFragment fragment = new SingleLotFragment();
        Bundle args = new Bundle();
        args.putString(Constant.SINGLE_NAME, name);
        args.putString(Constant.SINGLE_MILE_STRING, mileStr);
        args.putFloat(Constant.SINGLE_RATING, rating);
        args.putString(Constant.SINGLE_ADDRESS,address);
        args.putString(Constant.SINGLE_HOURS, hours);
        args.putString(Constant.SINGLE_CALL_NUMBER, callNumber);
        args.putString(Constant.SINGLE_URL, yelpUrl);
        args.putDouble(Constant.SINGLE_LAT, lat);
        args.putDouble(Constant.SINGLE_LNG, lng);
        args.putFloat(Constant.SINGLE_ZOOM_RATIO, zoomRatio);
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
        View rootView = inflater.inflate(R.layout.fragment_single_lot, container, false);
        Bundle args = getArguments();

        nameView = (TextView)rootView.findViewById(R.id.name);
        nameView.setText(args.getString(Constant.SINGLE_NAME));
        milesView = (TextView)rootView.findViewById(R.id.miles);
        milesView.setText(args.getString(Constant.SINGLE_MILE_STRING));
        ratingBarView = (RatingBar)rootView.findViewById(R.id.rating_bar);
        addressView = (TextView)rootView.findViewById(R.id.address);
        addressView.setText(args.getString(Constant.SINGLE_ADDRESS));
        hoursView = (TextView)rootView.findViewById(R.id.hours);
        hoursView.setText(args.getString(Constant.SINGLE_HOURS));
        callNumberView = (TextView)rootView.findViewById(R.id.call_number);
        callNumberView.setText(args.getString(Constant.SINGLE_CALL_NUMBER));
        moreInfoView = (TextView)rootView.findViewById(R.id.more_info);
        moreInfoView.setText("More Info...");

        mapView = (MapView)rootView.findViewById(R.id.google_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setIndoorEnabled(true);

        Bundle args = getArguments();
        double lat = args.getDouble(Constant.SINGLE_LAT);
        double lng = args.getDouble(Constant.SINGLE_LNG );
        String name = args.getString(Constant.SINGLE_NAME);
        float zoomRatio = args.getFloat(Constant.SINGLE_ZOOM_RATIO);
        LatLng latLng = new LatLng(lat, lng);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomRatio));

        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title(name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_marker)));
        marker.showInfoWindow();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
}
