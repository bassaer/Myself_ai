package com.nakayama.myself_ai;

import java.util.EventListener;

/**
 * Created by nakayama on 2016/02/10.
 */
public interface PushListenerInterface extends EventListener {

    public void informMessage(String message);

}
