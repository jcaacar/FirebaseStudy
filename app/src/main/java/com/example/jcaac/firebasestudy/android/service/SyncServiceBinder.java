package com.example.jcaac.firebasestudy.android.service;

import android.os.Binder;

public class SyncServiceBinder extends Binder {

    private SyncServiceAPI syncServiceAPI;

    public SyncServiceBinder(SyncServiceAPI syncServiceAPI) {
        this.syncServiceAPI = syncServiceAPI;
    }

    public SyncServiceAPI getService() {
        return syncServiceAPI;
    }
}
