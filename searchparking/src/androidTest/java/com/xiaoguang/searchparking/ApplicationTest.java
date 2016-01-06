package com.xiaoguang.searchparking;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.xiaoguang.dispatcherlib.Dispatcher;
import com.xiaoguang.searchparking.util.CmpUtil;
import com.xiaoguang.searchparking.component.Constant;
import com.xiaoguang.searchparking.component.RestComponent;
import com.xiaoguang.searchparking.datalib.Cmd;

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