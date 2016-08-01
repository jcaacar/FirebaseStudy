package com.example.jcaac.firebasestudy.android.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jcaac.firebasestudy.R;
import com.example.jcaac.firebasestudy.android.service.InvalidSyncServiceStateException;
import com.example.jcaac.firebasestudy.android.service.SyncService;
import com.example.jcaac.firebasestudy.android.service.SyncServiceAPI;
import com.example.jcaac.firebasestudy.android.service.SyncServiceBinder;
import com.example.jcaac.firebasestudy.android.util.Util;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity { //implements FirebaseStorageManager.OnCompleteDownload

    //region Attributes

    private Button btnStartService;

    private Button btnStartSync;
    private Button btnStopSync;
    private Button btnCancelSync;
    private Button btnStateSync;

    private SyncServiceAPI service;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SyncServiceBinder binder = (SyncServiceBinder) iBinder;
            service = binder.getService();
            isBounded = true;
            configureLayoutByState(service.getState());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
            isBounded = false;
        }
    };
    private boolean isBounded = false;

    //endregion

    //region LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartService = (Button) findViewById(R.id.btn_start_service);

        btnStartSync = (Button) findViewById(R.id.btn_sync_start);
        btnStopSync = (Button) findViewById(R.id.btn_sync_stop);
        btnCancelSync = (Button) findViewById(R.id.btn_sync_cancel);
        btnStateSync = (Button) findViewById(R.id.btn_sync_state);

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivityPermissionsDispatcher.startSyncServicePermissionWithCheck(MainActivity.this);
            }
        });

        btnStartSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSync();
            }
        });

        btnStopSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSync();
            }
        });

        btnCancelSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelSync();
            }
        });

        btnStateSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncService.State state = service.getState();
                String value = SyncService.State.values()[state.getValue()].name();
                Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Util.isServiceRunning(this, SyncService.class)) {
            if (!isBounded) {
                bindService(new Intent(this, SyncService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBounded) {
            unbindService(serviceConnection);
            isBounded = false;
        }
    }

    //endregion

    //region Permissions

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void startSyncServicePermission() {
        startSyncService();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showDeniedForExternalStorage() {
        Toast.makeText(this, R.string.permissionDenied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //endregion

    //region Core

    private void configureLayoutByState(SyncService.State syncServiceState) {

        switch (syncServiceState) {
            case STOPPED:
                btnStartSync.setEnabled(true);
                btnStateSync.setEnabled(true);

                btnStartService.setEnabled(false);
                btnStopSync.setEnabled(false);
                btnCancelSync.setEnabled(false);
                break;
            case DOWNLOADING:

                btnStopSync.setEnabled(true);
                btnCancelSync.setEnabled(true);
                btnStateSync.setEnabled(true);

                btnStartSync.setEnabled(false);
                btnStartService.setEnabled(false);
                break;
            case COMPLETED:

                btnStateSync.setEnabled(true);

                btnStartService.setEnabled(false);
                btnStartSync.setEnabled(false);
                btnStopSync.setEnabled(false);
                btnCancelSync.setEnabled(false);
                break;
            case CANCELLED:

                btnStateSync.setEnabled(true);

                btnStartService.setEnabled(false);
                btnStartSync.setEnabled(false);
                btnStopSync.setEnabled(false);
                btnCancelSync.setEnabled(false);

                break;
            case ERROR:
                break;
        }
    }

    private void startSyncService() {
        if (!Util.isServiceRunning(this, SyncService.class)) {
            startService(new Intent(this, SyncService.class));
        }
        if (!isBounded) {
            bindService(new Intent(this, SyncService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }

        btnStartSync.setEnabled(true);
        btnStateSync.setEnabled(true);
    }

    private void startSync() {
        try {
            service.startSync();
            configureLayoutByState(SyncService.State.DOWNLOADING);
        } catch (InvalidSyncServiceStateException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopSync() {
        try {
            service.stopSync();
            configureLayoutByState(SyncService.State.STOPPED);
        } catch (InvalidSyncServiceStateException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelSync() {
        try {
            service.cancelSync();
            configureLayoutByState(SyncService.State.CANCELLED);
        } catch (InvalidSyncServiceStateException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //endregion
}
