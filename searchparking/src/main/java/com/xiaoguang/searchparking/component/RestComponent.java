package com.xiaoguang.searchparking.component;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.xiaoguang.dispatcherlib.Callback;
import com.xiaoguang.dispatcherlib.Component;
import com.xiaoguang.dispatcherlib.Executer;
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
    private Map<Integer, ParkingLotsData.ParkingLot> existingParkingLots;

    public RestComponent(String name){
        setName(name);
        existingParkingLots = new HashMap<>();
        addInput(Constant.SEARCH_REQUEST, Cmd.SearchCmd.class, new Callback() {
            @Override
            public void onEvent(Object o, String s, Object o1) {
                final LatLng latLng = ((Cmd.SearchCmd) o1).latLng;
                final int radius = ((Cmd.SearchCmd) o1).radius;
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
                            Address curAdd = geocoder.getFromLocation(lat, lng, 1).get(0);
                            YelpAPI yelpAPI = new YelpAPI();
                            JSONArray queryRst = yelpAPI.queryAPI(new LatLng(lat, lng), curAdd.getLocality(), radius);
                            List<ParkingLotsData.ParkingLot> parkingLots = new ArrayList<>();
                            if(queryRst == null) {
                                Log.e("QueryForParkingLot", "no business found");
                                return;
                            }
                            for (int i = 0; i < queryRst.length(); i++) {
                                JSONObject obj = queryRst.getJSONObject(i);
                                ParkingLotsData.ParkingLot lot = new ParkingLotsData.ParkingLot(obj);
                                if(!existingParkingLots.containsKey(lot.hashCode())) {
                                    parkingLots.add(lot);
                                    existingParkingLots.put(lot.hashCode(), lot);
                                    Log.d("Parking", lot.name + ": " + lot.lat + ", " + lot.lng + ", hashcode: " + lot.hashCode());
                                }
                            }
                            Log.d("Parking", "parking lots sie:" + parkingLots.size());
                            ParkingLotsData data = new ParkingLotsData(parkingLots);
                            parkingLotsDataOutput.post(data);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        parkingLotsDataOutput = addOutput(Constant.PARKINGLOT_DATA, ParkingLotsData.class);
    }
}
