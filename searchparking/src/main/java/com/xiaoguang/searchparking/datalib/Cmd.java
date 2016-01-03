package com.xiaoguang.searchparking.datalib;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by wuxiaoguang on 12/14/15.
 */
public class Cmd {
    public static class SearchCmd{
//        public final Location location;
        public final LatLng latLng;
        public final int radius;
//        public SearchCmd(Location location) {
//            this.location = location;
//            this.latLng = null;
//        }
        public SearchCmd(LatLng latLng, int r) {
            this.latLng = latLng;
            this.radius = r;
//            this.location = null;
        }
    }
}
