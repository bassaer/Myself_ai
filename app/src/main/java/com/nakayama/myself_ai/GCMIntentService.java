package com.nakayama.myself_ai;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;

/**
 * Created by nakayama on 2016/02/10.
 */

public class GCMIntentService extends IntentService {
    private static final String TAG = "GcmIntentService";
    private PushListenerInterface mPushListenerInterface = null;

    public GCMIntentService(){
        super(GCMIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.d(TAG, "messageType: " + messageType + ",body:" + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d(TAG, "messageType: " + messageType + ",body:" + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendMessage(extras);
                //Log.d(TAG,"message!!");
                GCMBroadcastReceiver.completeWakefulIntent(intent);
            }
        }
    }

    public void sendMessage(Bundle messageBundle){
        Iterator<String> iterator = messageBundle.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            //Log.d(TAG,key + messageBundle.getString(key));
        }
        try{
            showMessage(URLDecoder.decode(messageBundle.getString("message"), "UTF-8"));
            //,URLDecoder.decode(messageBundle.getString("detail"),"UTF-8"));
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

    }

    public void showMessage(String message){

    }


}