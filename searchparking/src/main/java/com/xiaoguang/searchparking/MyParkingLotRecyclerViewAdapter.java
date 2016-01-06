package com.xiaoguang.searchparking;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.xiaoguang.searchparking.datalib.ParkingLotsData;
import com.xiaoguang.searchparking.util.LocationUtil;

import java.util.List;

public class MyParkingLotRecyclerViewAdapter extends RecyclerView.Adapter<MyParkingLotRecyclerViewAdapter.ViewHolder> {

    private final ParkingLotFragment.OnListFragmentListener mListener;
    private final double centerLat;
    private final double centerLng;
    private List<ParkingLotsData.ParkingLot> mParkingLots;

    public MyParkingLotRecyclerViewAdapter(List<ParkingLotsData.ParkingLot> parkingLots,
                                           ParkingLotFragment.OnListFragmentListener listener,
                                           double lat,
                                           double lng) {
        this.centerLat = lat;
        this.centerLng = lng;
        this.mParkingLots = parkingLots;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_parkinglot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ParkingLotsData.ParkingLot parkingLot = mParkingLots.get(position);
        holder.nameView.setText(parkingLot.name);
        holder.addressView.setText(parkingLot.address);
        double miles = LocationUtil.distance(parkingLot.lat, centerLat, parkingLot.lng, centerLng, 0.0, 0.0)/1600;
        holder.milesView.setText(String.format("%.1f miles", miles));
        holder.ratingBarView.setRating((float)parkingLot.rating);
        holder.ratingBarView.setMax(5);
    }

    @Override
    public int getItemCount() {
        return mParkingLots.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nameView;
        public final TextView milesView;
        public final TextView addressView;
        public final RatingBar ratingBarView;

        public ViewHolder(View view) {
            super(view);
            nameView = (TextView)view.findViewById(R.id.name);
            milesView = (TextView)view.findViewById(R.id.miles);
            addressView = (TextView)view.findViewById(R.id.address);
            ratingBarView = (RatingBar)view.findViewById(R.id.rating_bar);
        }

    }
}
