package com.example.jcaac.firebasestudy.android.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.jcaac.firebasestudy.R;
import com.example.jcaac.firebasestudy.android.util.Util;

public class SyncService extends Service implements SyncServiceAPI {

    private final String TAG = SyncService.class.getSimpleName();
    private final int NOTIFICATION_ID = 1910;

    public enum State {
        STOPPED(0),
        DOWNLOADING(1),
        COMPLETED(2),
        CANCELLED(3),
        ERROR(4);

        private int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    //region Attributes

    private Exception exception;
    private State currentState = State.STOPPED;

    //endregion

    //region LifeCycle

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return new SyncServiceBinder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    //endregion

    //region SyncServiceAPI

    @Override
    public State getState() {
        return currentState;
    }

    @Override
    public void startSync() throws InvalidSyncServiceStateException {
        changeTo(State.DOWNLOADING);
    }

    @Override
    public void stopSync() throws InvalidSyncServiceStateException {
        changeTo(State.STOPPED);
    }

    @Override
    public void cancelSync() throws InvalidSyncServiceStateException {
        changeTo(State.CANCELLED);
    }

    @Override
    public Exception getError() {
        return exception;
    }

    //endregion

    //region Core

    private synchronized void changeTo(State toState) throws InvalidSyncServiceStateException {

        if (toState == State.DOWNLOADING && currentState == State.STOPPED) {

            showNotification(R.string.notification_service_sync_started);
            currentState = toState;

        } else if (toState == State.COMPLETED && currentState == State.DOWNLOADING) {

            showNotification(R.string.notification_service_sync_completed);
            currentState = toState;

        } else if (toState == State.STOPPED && currentState == State.DOWNLOADING) {

            showNotification(R.string.notification_service_sync_stopped);
            currentState = toState;

        } else if (toState == State.CANCELLED && currentState == State.DOWNLOADING) {

            showNotification(R.string.notification_service_sync_cancelled);
            currentState = toState;

        } else if (toState == State.ERROR && currentState == State.DOWNLOADING) {

            showNotification(R.string.notification_service_sync_error);
            currentState = toState;

        } else {
            throw new InvalidSyncServiceStateException(currentState, toState);
        }
    }

    private void showNotification(int contentRes) {
        Notification notification = Util.createNotification(this, R.string.notification_service_sync_title, contentRes);
        startForeground(NOTIFICATION_ID, notification);
    }

    //endregion
}
