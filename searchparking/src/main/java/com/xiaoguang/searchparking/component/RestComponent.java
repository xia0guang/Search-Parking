package com.xiaoguang.searchparking.component;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.xiaoguang.dispatcherlib.Callback;
import com.xiaoguang.dispatcherlib.Component;
import com.xiaoguang.dispatcherlib.Executer;
import com.xiaoguang.searchparking.util.CmpUtil;
import com.xiaoguang.searchparking.datalib.Cmd;
import com.xiaoguang.searchparking.datalib.ParkingLotsData;
import com.xiaoguang.searchparking.yelp.YelpAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuxiaoguang on 12/14/15.
 */
public class RestComponent extends Component {
    public static final String TAG = "RestComponent";
    private final Port parkingLotsDataOutput;
    private final Port currentParkingDataOutput;
    private Map<Integer, ParkingLotsData.ParkingLot> existingParkingLots;
    private ParkingLotsData currentParkingLotsData;

    public RestComponent(String name){
        setName(name);
        existingParkingLots = new HashMap<>();
        addInput(Constant.CURRENT_PARKING_DATA_REQUEST, Cmd.CurrentCmd.class, new Callback() {
            @Override
            public void onEvent(Object o, String s, Object o1) {
                 if(currentParkingLotsData != null) {
                     currentParkingDataOutput.post(currentParkingLotsData);
                 }
            }
        });
        currentParkingDataOutput = addOutput(Constant.CURRENT_PARKING_DATA, ParkingLotsData.class);


        addInput(Constant.ALL_PARKING_DATA_REQUEST, Cmd.BaseCmd.class, new Callback() {
            @Override
            public void onEvent(Object o, String s, Object o1) {
                List<ParkingLotsData.ParkingLot> allParkingLots = new ArrayList<>();
                for(Map.Entry<Integer, ParkingLotsData.ParkingLot> entry : existingParkingLots.entrySet()) {
                    allParkingLots.add(entry.getValue());
                }
                ParkingLotsData data = new ParkingLotsData(allParkingLots, existingParkingLots);
                parkingLotsDataOutput.post(data);
            }
        });

        addInput(Constant.SEARCH_REQUEST, Cmd.SearchCmd.class, new Callback() {
            @Override
            public void onEvent(Object o, String s, Object o1) {
                final LatLng latLng = ((Cmd.SearchCmd) o1).latLng;
                final int radius = ((Cmd.SearchCmd) o1).radius;
                final LatLngBounds bounds = ((Cmd.SearchCmd)o1).bounds;
                Executer.instance().execute(new Runnable() {
                    @Override
                    public void run() {
                        Geocoder geocoder = new Geocoder(CmpUtil.instance(null).getCtx());
                        try {
                            double lat, lng;
                            if (latLng != null) {
                                lat = latLng.latitude;
                                lng = latLng.longitude;
                            } else {
                                lat = 37.7803401;
                                lng = -122.4809772;
                            }
                            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                            if (addresses.size() == 0) {
                                Log.d(TAG, "Failed to get location when using GeoCoder");
                                return;
                            }
                            Address curAddress = addresses.get(0);
                            YelpAPI yelpAPI = new YelpAPI();
                            JSONArray queryRst = yelpAPI.queryAPI(new LatLng(lat, lng), curAddress.getLocality(), radius);
                            if (queryRst == null) {
                                Log.e(TAG + ".Query", "no business found");
                                return;
                            }

                            //Filter incremental parking lot and add to all Existing set
                            List<ParkingLotsData.ParkingLot> incrementalParkingLots = new ArrayList<>();
                            for (int i = 0; i < queryRst.length(); i++) {
                                JSONObject obj = queryRst.getJSONObject(i);
                                ParkingLotsData.ParkingLot lot = new ParkingLotsData.ParkingLot(obj);
                                if (!existingParkingLots.containsKey(lot.hashCode())) {
                                    incrementalParkingLots.add(lot);
                                    existingParkingLots.put(lot.hashCode(), lot);
                                    Log.d(TAG + ".Parking", lot.name + ": " + lot.lat + ", " + lot.lng + ", hashcode: " + lot.hashCode());
                                }
                            }
                            Log.d(TAG + ".Parking", "parking lots sie:" + incrementalParkingLots.size());
                            ParkingLotsData data = new ParkingLotsData(incrementalParkingLots, existingParkingLots);
                            parkingLotsDataOutput.post(data);

                            //Update parking lots inside current visible region
                            List<ParkingLotsData.ParkingLot> currentParkingLots = new ArrayList<>();
                            for (Map.Entry<Integer, ParkingLotsData.ParkingLot> entry : existingParkingLots.entrySet()) {
                                ParkingLotsData.ParkingLot tmpParkingLot = entry.getValue();
                                LatLng parkingLotLatLng = new LatLng(tmpParkingLot.lat, tmpParkingLot.lng);
                                if (bounds.contains(parkingLotLatLng)) {
                                    currentParkingLots.add(tmpParkingLot);
                                }
                            }
                            currentParkingLotsData = new ParkingLotsData(currentParkingLots, existingParkingLots);
                            currentParkingDataOutput.post(currentParkingLotsData);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        parkingLotsDataOutput = addOutput(Constant.PARKING_LOT_DATA, ParkingLotsData.class);
    }
}
