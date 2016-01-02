package com.xiaoguang.searchparking;

import android.app.Application;
import android.location.Location;
import android.test.ApplicationTestCase;

import com.beust.jcommander.JCommander;
import com.xiaoguang.dispatcherlib.Dispatcher;
import com.xiaoguang.searchparking.component.CmpUtil;
import com.xiaoguang.searchparking.component.Constant;
import com.xiaoguang.searchparking.component.RestComponent;
import com.xiaoguang.searchparking.datalib.Cmd;
import com.xiaoguang.searchparking.yelp.YelpAPI;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void test() {
        CmpUtil.instance(getContext()).init();
        RestComponent restComponent = new RestComponent("RestComponent");
        restComponent.open();
        Dispatcher.instance().post(this, Constant.SEARCH_REQUEST, new Cmd.SearchCmd(null));
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        restComponent.close();
    }
}