package com.xiaoguang.searchparking.datalib;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

/**
 * Created by wuxiaoguang on 12/30/15.
 */
public class ParkingLotsData {
    public static class ParkingLot{
        public final double lat;
        public final double lng;
        public final String name;
        public final JSONObject json;

        public ParkingLot(JSONObject json) throws JSONException {
            this.lat = json.getJSONObject("location").getJSONObject("coordinate").getDouble("latitude");
            this.lng = json.getJSONObject("location").getJSONObject("coordinate").getDouble("longitude");
            this.name = json.getString("name");
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
            return new HashCodeBuilder(17, 31).
                    append(lat).
                    append(lng).
                    toHashCode();
        }
    }

    public ParkingLotsData(List<ParkingLot> list) {
        this.parkingLots = list;
    }

    private List<ParkingLot> parkingLots;

    public List<ParkingLot> getParkingLots() {
        return Collections.unmodifiableList(parkingLots);
    }
}
