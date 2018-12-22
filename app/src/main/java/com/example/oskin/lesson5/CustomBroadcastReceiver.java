package com.example.oskin.lesson5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CustomBroadcastReceiver extends BroadcastReceiver {

    BroadcastCallback mBroadcastCallback;

    CustomBroadcastReceiver(BroadcastCallback broadcastCallback){
        this.mBroadcastCallback = broadcastCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO

        mBroadcastCallback.collingBack(intent);
    }


}
