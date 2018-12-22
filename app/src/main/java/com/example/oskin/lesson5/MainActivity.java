package com.example.oskin.lesson5;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class   MainActivity extends AppCompatActivity {

    private CustomBroadcastReceiver mCustomBroadcastReceiver;
    private CustomBroadcastReceiver mStopServiceBroadcastReceiver;
    private IntentFilter mIntentFilter;
    private IntentFilter mStopServiceFilter;


    private Messenger mService;
    private Messenger mMessenger = new Messenger(new IncomingHandler());

    private TextView mNameColorTextView;
    private TextView mTimeTextView;
    private TextView mDateTextView;
    private TextView mDayTextView;
    private TextView mSecondsTextView;
    private TextView mHoursAndMinutes;

    private Button mStarService;
    private Button mBindService;
    private Button mUnBindService;
    private Button mStopService;

    private Button mRedBtn;
    private Button mBlueBtn;
    private Button mCyanBtn;
    private Button mYellowBtn;
    private Button mGreenBtn;
    private Button mMagentaBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initListeners();
        initBroadcastReceivers();
    }

    private void initViews() {
        mNameColorTextView = findViewById(R.id.linear_name_color_tw);
        mTimeTextView = findViewById(R.id.relative_time_tw);
        mDateTextView = findViewById(R.id.relative_date_tw);
        mDayTextView = findViewById(R.id.relative_day_tw);
        mSecondsTextView = findViewById(R.id.seconds_tw);
        mHoursAndMinutes = findViewById(R.id.hours_and_minutes_tw);

        mStarService = findViewById(R.id.linear_start_btn);
        mBindService = findViewById(R.id.bind_service_btn);
        mUnBindService = findViewById(R.id.linear_unbind_btn);
        mStopService = findViewById(R.id.linear_stop_service_btn);

        mRedBtn = findViewById(R.id.red_btn);
        mBlueBtn = findViewById(R.id.blue_btn);
        mGreenBtn = findViewById(R.id.green_btn);
        mCyanBtn = findViewById(R.id.cyan_btn);
        mMagentaBtn = findViewById(R.id.magenta_btn);
        mYellowBtn = findViewById(R.id.yellow_btn);
    }

    private void initListeners() {
        mStarService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startService(MyService.newIntent(MainActivity.this));
            }
        });

        mBindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService();
            }
        });

        mUnBindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService();
            }
        });
        mStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService();
                stopService(MyService.newIntent(MainActivity.this));
            }
        });
    }


    private void initBroadcastReceivers() {
        mCustomBroadcastReceiver = new CustomBroadcastReceiver(new ViewColorsCallback());
        mIntentFilter = new IntentFilter(MyService.CHANGE_COLOR);
        mStopServiceBroadcastReceiver = new CustomBroadcastReceiver(new StopServiceCallback());
        mStopServiceFilter = new IntentFilter(MyService.ACTION_STOP_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mCustomBroadcastReceiver, mIntentFilter,null, null);
        registerReceiver(mStopServiceBroadcastReceiver, mStopServiceFilter,null, null);
    }

    public void bindService(){
        bindService(MyService.newIntent(MainActivity.this), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService(){
        if (mService == null)
            return;
        Message msg = Message.obtain(null, MyService.MSG_UNREGISTER_CLIENT);
        msg.replyTo = mMessenger;
        try {
            mService.send(msg);
        }
        catch (RemoteException exc){
            exc.printStackTrace();
        }
        unbindService(mServiceConnection);
        mService = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mCustomBroadcastReceiver);
        unregisterReceiver(mStopServiceBroadcastReceiver);
        unbindService();
        stopService(MyService.newIntent(MainActivity.this));

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Message msg = Message.obtain(null,MyService.MSG_REGISTER_CLIENT);
            msg.replyTo = mMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private class IncomingHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case MyService.MSG_CURRENT_VALUE:
                    /*Gotten string has format "Mon Dec 17 16:42:19 GMT 2018"*/
                    String[] date = msg.obj.toString().split(" ");
                    String[] time = date[3].split(":");
                    updateDate(date);
                    updateClock(time);
                    break;
                case MyService.MSG_SERVICE_STOP:

                    break;
            }
        }
    }

    private class ViewColorsCallback implements BroadcastCallback {

        @Override
        public void collingBack(Intent intent) {
            String nameColor = intent.getStringExtra(MyService.NAME_COLOR);
            int currentColor = intent.getIntExtra(MyService.CURRENT_COLOR,0);
            changeTextAndBtnColor(nameColor, currentColor);
        }
    }

    private class StopServiceCallback implements BroadcastCallback {

        @Override
        public void collingBack(Intent intent) {
            String stopService = getString(R.string.service_is_stopped);
            changeTextAndBtnColor(stopService, 0);
        }
    }

    private void changeTextAndBtnColor(String nameColor, int CurrentColor){
        mNameColorTextView.setText(nameColor);

        mRedBtn.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        mBlueBtn.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        mGreenBtn.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        mCyanBtn.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        mYellowBtn.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        mMagentaBtn.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));

        switch (CurrentColor){
            case Color.RED:
                mRedBtn.setBackgroundTintList(ColorStateList.valueOf(CurrentColor));
                break;
            case Color.BLUE:
                mBlueBtn.setBackgroundTintList(ColorStateList.valueOf(CurrentColor));
                break;
            case Color.CYAN:
                mCyanBtn.setBackgroundTintList(ColorStateList.valueOf(CurrentColor));
                break;
            case Color.MAGENTA:
                mMagentaBtn.setBackgroundTintList(ColorStateList.valueOf(CurrentColor));
                break;
            case Color.YELLOW:
                mYellowBtn.setBackgroundTintList(ColorStateList.valueOf(CurrentColor));
                break;
            case Color.GREEN:
                mGreenBtn.setBackgroundTintList(ColorStateList.valueOf(CurrentColor));
                break;
            default:
                break;
        }
    }

    private void updateDate(String[] date){
        mTimeTextView.setText(date[3]);
        mDayTextView.setText(date[0]);
        String dateStr = date[2] + " " + date[1] + " " + date[5];
        mDateTextView.setText(dateStr);
    }

    private void updateClock(String[] time){
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mSecondsTextView.getLayoutParams();
        layoutParams.circleAngle = Integer.parseInt(time[2]) * 6;
        mSecondsTextView.setLayoutParams(layoutParams);
        mSecondsTextView.setText(time[2]);
        String hours = time[0] + ":" + time[1];
        mHoursAndMinutes.setText(hours);
    }
}
