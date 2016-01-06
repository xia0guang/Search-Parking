package com.xiaoguang.searchparking.datalib;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by wuxiaoguang on 12/14/15.
 */
public class Cmd {
    public static class BaseCmd{}

    public static class SearchCmd extends BaseCmd{
        public final LatLng latLng;
        public final int radius;
        public final LatLngBounds bounds;
        public SearchCmd(LatLng latLng, int r, LatLngBounds bounds) {
            this.latLng = latLng;
            this.radius = r;
            this.bounds = bounds;
        }
    }

    public static class CurrentCmd extends BaseCmd{
    }
}
