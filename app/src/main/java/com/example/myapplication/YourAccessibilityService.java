package com.example.myapplication;



import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.TokenWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class YourAccessibilityService extends AccessibilityService  {

    public static final String mBroadcastStringAction = "com.example.myapplication.string";
    public static final String mBroadcastIntegerAction = "com.example.myapplication.integer";
    public static final String mBroadcastArrayListAction = "com.example.myapplication.arraylist";
    private IntentFilter mIntentFilter;
    private IntentFilter mIntentFilterUnLock;

    PowerManager.WakeLock partialWakeLock;

    BroadcastReceiver displayStateReciewer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

            if (action.equals(Intent.ACTION_SCREEN_OFF)){
                Log.d("--a---a---a--a--a--a--", "ScreenOff");
            } else if (action.equals(Intent.ACTION_SCREEN_ON)){
                Log.d("--a---a---a--a--a--a--", "ScreenOn");
            }
        }
    };

    private CommandReceiver commandReceiver = new CommandReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(mBroadcastIntegerAction)) { // Жесты
                Log.d("--a---a---a--a--a--a--", "onRecieve:");
                pressLocation(new Point(400,500),intent.getIntExtra("Data",0));

            }
        }
    };


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("--a---a---a--a--a--a--", "onServiceConnected:");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(commandReceiver);
        unregisterReceiver(displayStateReciewer);
        Toast.makeText(this, "OFF OFF OFF OFF OFF", Toast.LENGTH_SHORT).show();
        //partialWakeLock.acquire(); Приложение падает при его вызове, пока хз почему
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
        mIntentFilter.addAction(mBroadcastIntegerAction);
        mIntentFilter.addAction(mBroadcastArrayListAction);
        registerReceiver(commandReceiver, mIntentFilter);

        mIntentFilterUnLock = new IntentFilter();
        mIntentFilterUnLock.addAction(Intent.ACTION_SCREEN_ON);
        mIntentFilterUnLock.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(displayStateReciewer,mIntentFilterUnLock);

        Toast.makeText(this, "ON ON ON ON ON", Toast.LENGTH_SHORT).show();


    }



    private void pressLocation(Point position, int route){
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();


        switch (route){
            case 0://UP
                p.moveTo(position.x, position.y);
                p.lineTo(position.x, position.y+500);
                builder.addStroke(new GestureDescription.StrokeDescription(p, 10L, 200L));
                break;

            case 1://DOWN
                p.moveTo(position.x, position.y);
                p.lineTo(position.x, position.y-500);
                builder.addStroke(new GestureDescription.StrokeDescription(p, 10L, 200L));
                break;

            case 2://RIGHT
                p.moveTo(position.x, position.y);
                p.lineTo(position.x+399, position.y);
                builder.addStroke(new GestureDescription.StrokeDescription(p, 10L, 200L));
                break;

            case 3://LEFT
                p.moveTo(position.x, position.y);
                p.lineTo(position.x-399, position.y);
                builder.addStroke(new GestureDescription.StrokeDescription(p, 10L, 200L));
                break;

            case 4://TAP

                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                PowerManager.WakeLock  wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "appname::WakeLock");

                //acquire will turn on the display
                wakeLock.acquire();
                wakeLock.release();//release will release the lock from CPU, in case of that, screen will go back to sleep mode in defined time bt device settings

                for (int i = 0; i < 190999900; i++) {// Сделай нормальную задержку!!!
                    
                }

                p.moveTo(position.x, position.y);
                p.lineTo(position.x, position.y-500);
                builder.addStroke(new GestureDescription.StrokeDescription(p, 10L, 200L));


                break;

            default:

                break;
        }

        GestureDescription gesture = builder.build();
        boolean isDispatched = dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.d("TAG", "gesture completed");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.d("TAG", "gesture completed");
            }
        }, null);

    }


}