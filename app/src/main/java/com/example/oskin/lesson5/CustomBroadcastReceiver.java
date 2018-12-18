package com.example.oskin.lesson5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CustomBroadcastReceiver extends BroadcastReceiver {

    ViewCallback viewCallback;

    CustomBroadcastReceiver(ViewCallback viewCallback){
        this.viewCallback = viewCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO
        viewCallback.collingBack(intent.getStringExtra(MyService.NAME_COLOR),
                intent.getIntExtra(MyService.CURRENT_COLOR,0));
    }


}
