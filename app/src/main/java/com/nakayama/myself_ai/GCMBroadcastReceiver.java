package com.nakayama.myself_ai;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by nakayama on 2016/02/10.
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    public GCMBroadcastReceiver(){
        super();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName mComponetName = new ComponentName(context.getPackageName(),GCMIntentService.class.getName());
        startWakefulService(context,(intent.setComponent(mComponetName)));
        setResultCode(Activity.RESULT_OK);
    }
}
