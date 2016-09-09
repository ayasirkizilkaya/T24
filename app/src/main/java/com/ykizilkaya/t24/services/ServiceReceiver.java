package com.ykizilkaya.t24.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ServiceReceiver extends ResultReceiver {

    private Listener listener;

    public ServiceReceiver(Handler handler) {
        super(handler);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(listener != null) listener.onReceiveResult(resultCode, resultData);
    }

    public interface Listener {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

}