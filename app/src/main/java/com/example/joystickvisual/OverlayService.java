package com.example.joystickvisual;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.*;

public final class OverlayService extends Service {
    private static final String CHANNEL="visual_overlay", STOP="com.example.joystickvisual.STOP";
    private WindowManager wm;
    private JoystickView view;

    @Override public void onCreate() {
        super.onCreate(); wm=(WindowManager)getSystemService(WINDOW_SERVICE);
        NotificationManager nm=getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel(CHANNEL,"Joystick overlay",NotificationManager.IMPORTANCE_LOW));
    }
    @Override public int onStartCommand(Intent intent,int flags,int startId) {
        if(intent!=null && STOP.equals(intent.getAction())) { stopSelf(); return START_NOT_STICKY; }
        PendingIntent open=PendingIntent.getActivity(this,1,new Intent(this,MainActivity.class),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent stop=PendingIntent.getService(this,2,new Intent(this,OverlayService.class).setAction(STOP),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        startForeground(1,new Notification.Builder(this,CHANNEL).setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("Joystick visual overlay").setContentText("Decorative animation only").setOngoing(true).setContentIntent(open).addAction(new Notification.Action.Builder(null,"Stop",stop).build()).build());
        if(!Settings.canDrawOverlays(this)) { stopSelf(); return START_NOT_STICKY; }
        try { applyOverlay(); } catch(RuntimeException e) { stopSelf(); }
        return START_NOT_STICKY;
    }
    private void applyOverlay() {
        SharedPreferences p=getSharedPreferences(MainActivity.PREFS,MODE_PRIVATE);
        DisplayMetrics m=new DisplayMetrics(); wm.getDefaultDisplay().getMetrics(m);
        int d=Math.min(Math.min(m.widthPixels,m.heightPixels),Math.round(Math.max(70,Math.min(260,p.getInt("size",150)))*m.density));
        WindowManager.LayoutParams lp=new WindowManager.LayoutParams(d,d,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        lp.gravity=Gravity.TOP|Gravity.LEFT; lp.x=Math.round(Math.max(0,m.widthPixels-d)*clamp(p.getInt("x",12)/100f,0,1));
        lp.y=Math.round(Math.max(0,m.heightPixels-d)*clamp(p.getInt("y",75)/100f,0,1));
        // The actual window alpha is capped below Android's 0.8 touch-through threshold.
        lp.alpha=clamp(p.getInt("alpha",55)/100f,0.15f,0.70f);
        if(view==null){view=new JoystickView(this); view.configure(p.getInt("mode",0),p.getInt("speed",100)/100f); wm.addView(view,lp);}
        else {view.configure(p.getInt("mode",0),p.getInt("speed",100)/100f); wm.updateViewLayout(view,lp);}
    }
    private static float clamp(float v,float min,float max){return Math.max(min,Math.min(max,v));}
    @Override public void onConfigurationChanged(Configuration c){ super.onConfigurationChanged(c); if(view!=null)try{applyOverlay();}catch(RuntimeException e){stopSelf();} }
    @Override public void onDestroy(){ if(view!=null){ try{wm.removeView(view);}catch(RuntimeException ignored){} view=null;} stopForeground(STOP_FOREGROUND_REMOVE); super.onDestroy(); }
    @Override public IBinder onBind(Intent i){return null;}
}
