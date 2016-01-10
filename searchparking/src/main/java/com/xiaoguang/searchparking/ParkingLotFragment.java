package com.xiaoguang.searchparking;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoguang.dispatcherlib.Callback;
import com.xiaoguang.dispatcherlib.Dispatcher;
import com.xiaoguang.searchparking.component.Constant;
import com.xiaoguang.searchparking.datalib.Cmd;
import com.xiaoguang.searchparking.datalib.ParkingLotsData;

public class ParkingLotFragment extends Fragment{

    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String ZOOM_RATIO = "ZoomRatio";
    private OnListFragmentListener mListener;
    private Activity mActivity;
    private double mCenterLat;
    private double mCenterLng;
    private Callback mCurrentParkingCallback;
    private RecyclerView mRecyclerView;
    private ParkingLotsData data;
    private float mZoomRatio;

    public ParkingLotFragment() {
    }

    public static ParkingLotFragment newInstance(double lat, double lng, float zoomRatio) {
        ParkingLotFragment fragment = new ParkingLotFragment();
        Bundle args = new Bundle();
        args.putDouble(LAT,lat);
        args.putDouble(LNG, lng);
        args.putFloat(ZOOM_RATIO, zoomRatio);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCenterLat = getArguments().getDouble(LAT);
            mCenterLng = getArguments().getDouble(LNG);
            mZoomRatio = getArguments().getFloat(ZOOM_RATIO);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parkinglot_list, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);

        FloatingActionButton mapButton = (FloatingActionButton)view.findViewById(R.id.map_view_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.trans_move));
            setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentParkingCallback = Dispatcher.instance().register(this, Constant.CURRENT_PARKING_DATA, new Callback() {
            @Override
            public void onEvent(Object o, String s, Object o1) {
                data = (ParkingLotsData)o1;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyParkingLotRecyclerViewAdapter adapter = new MyParkingLotRecyclerViewAdapter(
                                data.getParkingLots(),
                                mListener,
                                mCenterLat,
                                mCenterLng,
                                mZoomRatio);
                        mRecyclerView.setAdapter(adapter);
                    }
                });
            }
        });

        Dispatcher.instance().post(this, Constant.CURRENT_PARKING_DATA_REQUEST, new Cmd.CurrentCmd());
    }

    @Override
    public void onStop() {
        Dispatcher.instance().unregister(this, Constant.CURRENT_PARKING_DATA, mCurrentParkingCallback);
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mListener = (OnListFragmentListener)mActivity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        mListener = null;
    }

    public interface OnListFragmentListener {
        void onListItemClicked(View view, ParkingLotsData.ParkingLot parkingLot, int position, String mileStr, float zoomRatio);
    }
}
