package com.xiaoguang.searchparking;

import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.xiaoguang.searchparking.datalib.ParkingLotsData;
import com.xiaoguang.searchparking.util.LocationUtil;

import java.util.List;

public class MyParkingLotRecyclerViewAdapter extends RecyclerView.Adapter<MyParkingLotRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "Adapter";
    private final ParkingLotFragment.OnListFragmentListener mListener;
    private final double centerLat;
    private final double centerLng;
    private List<ParkingLotsData.ParkingLot> mParkingLots;
    private final float mZoomRatio;

    public MyParkingLotRecyclerViewAdapter(List<ParkingLotsData.ParkingLot> parkingLots,
                                           ParkingLotFragment.OnListFragmentListener listener,
                                           double lat,
                                           double lng,
                                           float zoomRatio) {
        this.centerLat = lat;
        this.centerLng = lng;
        this.mParkingLots = parkingLots;
        mListener = listener;
        this.mZoomRatio = zoomRatio;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_parkinglot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ParkingLotsData.ParkingLot parkingLot = mParkingLots.get(position);
        holder.nameView.setText(parkingLot.name);
        holder.addressView.setText(parkingLot.address);
        double miles = LocationUtil.distance(parkingLot.lat, centerLat, parkingLot.lng, centerLng, 0.0, 0.0)/1600;
        holder.milesView.setText(String.format("%.1f miles", miles));

        holder.ratingBarView.setMax(5);
        float rating = (float)parkingLot.rating;
        Log.d(TAG, "real: " + parkingLot.rating +", cur: " + rating);
        holder.ratingBarView.setStepSize(0.5f);
        holder.ratingBarView.setRating(rating);
        Log.d(TAG, "after set view: " + holder.ratingBarView.getRating());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.nameView.setTransitionName("name" + position);
            holder.addressView.setTransitionName("address" + position);
            holder.milesView.setTransitionName("miles" + position);
            holder.ratingBarView.setTransitionName("ratingbar" + position);
            holder.cardView.setTransitionName("cardview" + position);
        }

        final int clickPosition = position;
        final String clickMiles = String.format("%.1f miles", miles);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListItemClicked(v, parkingLot, clickPosition, clickMiles, mZoomRatio);
            }
        });
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
        public final View mView;
        private final CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            nameView = (TextView) view.findViewById(R.id.name);
            milesView = (TextView) view.findViewById(R.id.miles);
            addressView = (TextView) view.findViewById(R.id.address);
            ratingBarView = (RatingBar) view.findViewById(R.id.rating_bar);
            cardView = (CardView) view.findViewById(R.id.card_view);

        }
    }

}