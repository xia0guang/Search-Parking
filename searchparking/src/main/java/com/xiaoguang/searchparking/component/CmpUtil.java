package com.xiaoguang.searchparking.component;

import android.content.Context;

/**
 * Created by wuxiaoguang on 12/30/15.
 */
public class CmpUtil {
    private Context ctx;
    private static volatile CmpUtil sInstance;

    public static CmpUtil instance(Context ctx){
        if(sInstance == null) {
            synchronized (CmpUtil.class){
                if(sInstance == null) {
                    sInstance = new CmpUtil(ctx);
                }
            }
        }

        return sInstance;
    }

    private CmpUtil(Context context) {
        this.ctx = context;
    }

    public void init(){

    }

    public Context getCtx(){
        return this.ctx;
    }
}
