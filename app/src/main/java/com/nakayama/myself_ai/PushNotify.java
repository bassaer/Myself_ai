package com.nakayama.myself_ai;

/**
 * Created by nakayama on 2016/02/10.
 */
public class PushNotify {

    private PushListenerInterface listener = null;

    public void sendMessage(String message){
        listener.informMessage(message);
    }


    public void setListener(PushListenerInterface listener){
        this.listener = listener;
    }
}
