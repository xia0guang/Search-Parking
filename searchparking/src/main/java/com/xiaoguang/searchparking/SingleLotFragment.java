package com.xiaoguang.searchparking;


import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
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
    private static final String POSITION = "Position";
    private static final String NAME = "Name";
    private static final String MILE_STRING = "MileString";
    private static final String RATING = "Rating";
    private static final String ADDRESS = "Address";
    private static final String HOURS = "Hours";
    private static final String CALL_NUMBER = "CallNumber";
    private static final String URL = "URL";
    private static final String LAT = "Lat";
    private static final String LNG = "Lng";
    private static final String ZOOM_RATIO = "ZoomRatio";
    private TextView nameView;
    private RatingBar ratingBarView;
    private TextView hoursView;
    private TextView callNumberView;
    private TextView moreInfoView;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private TextView addressView;
    private TextView milesView;
    private CardView cardView;

    public static SingleLotFragment newInstance(String name,
                                                String mileStr,
                                                float rating,
                                                String address,
                                                String hours,
                                                String callNumber,
                                                String yelpUrl,
                                                double lat,
                                                double lng,
                                                float zoomRatio,
                                                int position) {
        SingleLotFragment fragment = new SingleLotFragment();

        Bundle args = new Bundle();
        args.putString(NAME, name);
        args.putString(MILE_STRING, mileStr);
        args.putFloat(RATING, rating);
        args.putString(ADDRESS, address);
        args.putString(HOURS, hours);
        args.putString(CALL_NUMBER, callNumber);
        args.putString(URL, yelpUrl);
        args.putDouble(LAT, lat);
        args.putDouble(LNG, lng);
        args.putFloat(ZOOM_RATIO, zoomRatio);
        args.putInt(POSITION, position);

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

        nameView = (TextView)rootView.findViewById(R.id.name);
        milesView = (TextView)rootView.findViewById(R.id.miles);
        ratingBarView = (RatingBar)rootView.findViewById(R.id.rating_bar);
        addressView = (TextView)rootView.findViewById(R.id.address);
        hoursView = (TextView)rootView.findViewById(R.id.hours);
        callNumberView = (TextView)rootView.findViewById(R.id.call_number);
        moreInfoView = (TextView)rootView.findViewById(R.id.more_info);
        moreInfoView.setText("More Info...");
        cardView = (CardView)rootView.findViewById(R.id.card_view);

        initView(getArguments());

        mapView = (MapView)rootView.findViewById(R.id.google_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.setTransitionName(Constant.MAP_VIEW_TRANSITION);


        return rootView;
    }

    private void initView(Bundle args){
        nameView.setText(args.getString(NAME));
        milesView.setText(args.getString(MILE_STRING));

        Log.d(TAG, "cur: " + args.getFloat(RATING));
        ratingBarView.setStepSize(0.5f);
        ratingBarView.setRating(args.getFloat(RATING));
        Log.d(TAG, "after set view: " + ratingBarView.getRating());

        addressView.setText(args.getString(ADDRESS));
        hoursView.setText(args.getString(HOURS));
        callNumberView.setText(args.getString(CALL_NUMBER));
        int tmpPosition = args.getInt(POSITION);

        if(tmpPosition != Constant.NOT_IN_LIST && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTransitionName(tmpPosition);
        }
    }

    @SuppressWarnings("NewApi")
    public void setTransitionName(int position) {
        nameView.setTransitionName("name" + position);
        milesView.setTransitionName("miles" + position);
        ratingBarView.setTransitionName("ratingbar" + position);
        addressView.setTransitionName("address" + position);
        cardView.setTransitionName("cardview"+position);
    }

    private void initGoogleMap() {
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setIndoorEnabled(true);

        Bundle args = getArguments();
        double lat = args.getDouble(LAT);
        double lng = args.getDouble(LNG );
        String name = args.getString(NAME);
        float zoomRatio = args.getFloat(ZOOM_RATIO);
        LatLng latLng = new LatLng(lat, lng);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomRatio));

        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_marker)));
        marker.showInfoWindow();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        initGoogleMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
