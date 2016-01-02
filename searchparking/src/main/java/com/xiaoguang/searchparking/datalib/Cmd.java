package com.xiaoguang.searchparking.datalib;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by wuxiaoguang on 12/14/15.
 */
public class Cmd {
    public static class SearchCmd{
        public final Location location;
        public final LatLng latLng;
        public SearchCmd(Location location) {
            this.location = location;
            this.latLng = null;
        }
        public SearchCmd(LatLng latLng) {
            this.latLng = latLng;
            this.location = null;
        }
    }
}
