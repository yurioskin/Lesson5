package com.example.oskin.lesson5;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Pair;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {

    public static final String ACTION_CHANGE_STATE = "com.example.oskin.lesson4.action.CHANGE_STATE";
    public static final String NEW_STATE = "com.example.oskin.lesson4.key.NEW_STATE";
    public static final String CHANGE_COLOR = "com.example.oskin.lesson4.key.CHANGE_COLOR";
    public static final String CURRENT_COLOR = "com.example.oskin.lesson4.key.CURRENT_COLOR";
    public static final String NAME_COLOR = "com.example.oskin.lesson4.key.NAME_COLOR";

    public static final int MSG_REGISTER_CLIENT = 0x00001;
    public static final int MSG_UNREGISTER_CLIENT = 0x00002;
    public static final int MSG_SERVICE_STOP = 0x00003;
    public static final int MSG_CURRENT_VALUE = 0x00004;
    public static final int MSG_INTERRUPT = 0x00005;

    private static final int MODE = Service.START_NOT_STICKY;

    private List<Messenger> mClients = new ArrayList<>();
    private Messenger mMessenger = new Messenger(new IncomingHandler());
    private static List<Pair<String,Integer>> mColors = new ArrayList<>();

    private boolean isInterrupted = false;

    private Thread linearThread = new Thread(){
        Random mRandom = new Random();
        int lastColor = 0;
        int indexColor;

        @Override
        public void run(){
            try {
                for (int i = 0; ; i++) {
                    if (isInterrupted) {
                        stopSelf();
                        return;
                    }

                    indexColor = mRandom.nextInt(6);
                    while (lastColor == indexColor){
                        indexColor = mRandom.nextInt(6);
                    }
                    lastColor = indexColor;
                    Intent intent = new Intent(CHANGE_COLOR);
                    intent.putExtra(NAME_COLOR, mColors.get(indexColor).first);
                    intent.putExtra(CURRENT_COLOR, mColors.get(indexColor).second);
                    sendBroadcast(intent);
                    TimeUnit.SECONDS.sleep(1);

                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    };

    private Thread timeThread = new Thread(){

        @Override
        public void run() {
            super.run();
            try {
                Message msg;
                Date currentDate;
                for (int i = 0; ; i++) {
                    if (isInterrupted) {
                        stopSelf();
                        return;
                    }
                    currentDate = Calendar.getInstance().getTime();
                    msg  = Message.obtain(null,MSG_CURRENT_VALUE,currentDate);
                    for (Messenger messenger:mClients) {
                        messenger.send(msg);
                    }
                    TimeUnit.SECONDS.sleep(1);

                }
            } catch (InterruptedException | RemoteException e){
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate() {
        //TODO business logic
        super.onCreate();
        initColors();
        linearThread.start();
        timeThread.start();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    public static final Intent newIntent(Context context){
        Intent intent = new Intent(context, MyService.class);
        return intent;
    }

    private class IncomingHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_INTERRUPT:
                    isInterrupted = true;
                    break;
            }
        }
    }
    private void initColors() {
        mColors.add(new Pair<>("Red", Color.RED));
        mColors.add(new Pair<>("Cyan", Color.CYAN));
        mColors.add(new Pair<>("Magenta", Color.MAGENTA));
        mColors.add(new Pair<>("Blue", Color.BLUE));
        mColors.add(new Pair<>("Green", Color.GREEN));
        mColors.add(new Pair<>("Yellow", Color.YELLOW));
    }
}
