package com.example.jcaac.firebasestudy.android.service;

public interface SyncServiceAPI {

    SyncService.State getState();

    void startSync() throws InvalidSyncServiceStateException;
    void stopSync() throws InvalidSyncServiceStateException;
    void cancelSync() throws InvalidSyncServiceStateException;

    Exception getError();
}
