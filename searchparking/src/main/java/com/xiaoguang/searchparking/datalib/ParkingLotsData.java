package com.xiaoguang.searchparking.datalib;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by wuxiaoguang on 12/30/15.
 */
public class ParkingLotsData {

    public static class ParkingLot{
        public final double lat;
        public final double lng;
        public final String name;
        public final JSONObject json;
        public final String address;
        public final double rating;

        public ParkingLot(JSONObject json) throws JSONException {
            this.lat = json.getJSONObject("location").getJSONObject("coordinate").getDouble("latitude");
            this.lng = json.getJSONObject("location").getJSONObject("coordinate").getDouble("longitude");
            this.name = json.getString("name");
            JSONArray displayAddress = json.getJSONObject("location").getJSONArray("display_address");
            if(displayAddress != null && displayAddress.length() >= 2) {
                this.address = displayAddress.getString(0) + ", " + displayAddress.getString(1);
            } else {
                this.address = "";
            }
            this.rating = json.getDouble("rating");
            this.json = json;
        }

        @Override
        public boolean equals(Object o) {
            if(o == null) return false;
            if(!(o instanceof ParkingLot)) return false;
            if(o == this) return true;
            ParkingLot obj = (ParkingLot)o;
            return new EqualsBuilder().
                    append(lat, obj.lat).
                    append(lng, obj.lng).
                    equals(o);
        }

        @Override
        public int hashCode() {
            return latLngToHashCode(lat, lng);
        }
    }

    public ParkingLotsData(List<ParkingLot> list, Map<Integer, ParkingLot> map) {
        this.parkingLots = list;
        this.existingParkingLots = map;
    }


    private List<ParkingLot> parkingLots;
    private final Map<Integer, ParkingLot> existingParkingLots;

    public List<ParkingLot> getParkingLots() {
        return Collections.unmodifiableList(parkingLots);
    }
    public Map<Integer, ParkingLot> getExistingParkingLots(){
        return Collections.unmodifiableMap(existingParkingLots);
    }

    public static int latLngToHashCode(double lat, double lng) {
        return new HashCodeBuilder(17, 31).
                append(lat).
                append(lng).
                toHashCode();
    }
}
