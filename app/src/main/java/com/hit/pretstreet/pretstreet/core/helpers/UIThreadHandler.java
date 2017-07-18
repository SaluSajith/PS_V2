package com.hit.pretstreet.pretstreet.core.helpers;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by User on 7/7/2017.
 */

public class UIThreadHandler extends Handler {
    /**
     * Default constructor associates this handler with the {@link Looper} for the
     * UI thread.
     * <p>
     * If this thread does not have a looper, this handler won't be able to receive messages
     * so an exception is thrown.This constructor make sure that the handler is created on UI Thread.
     * <p>
     */
    public UIThreadHandler() {
        super(Looper.getMainLooper());
    }

}

