package com.example.jcaac.firebasestudy.android.service;

public class InvalidSyncServiceStateException extends Exception {

    public InvalidSyncServiceStateException(SyncService.State currentState, SyncService.State toState) {
        super(String.format("Invalid operation by changing the machine %s states for %s",
                SyncService.State.values()[currentState.getValue()].name(),
                SyncService.State.values()[toState.getValue()].name()));
    }
}
