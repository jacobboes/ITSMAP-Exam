package com.grp16.itsmap.smapexam.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    public List<PoiListener> listeners;
    private ServiceWrapper service;

    public NotificationReceiver(ServiceWrapper service) {
        listeners = new ArrayList<>();
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppUtil.BROADCAST_LOCATION_CHANGED)) {
            for (PoiListener listener : listeners) {
                listener.dataReady(service.getPoiList());
            }
        }
    }

    public void addListener(PoiListener listener) {
        listeners.add(listener);
    }

    public void removeListener (PoiListener listener) {
        listeners.remove(listener);
    }
}
