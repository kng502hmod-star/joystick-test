package com.example.joystickvisual;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.*;

public final class MainActivity extends Activity {
    static final String PREFS="visual";
    private SharedPreferences prefs;
    private LinearLayout layout;
    private TextView status;
    private JoystickView preview;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs=getSharedPreferences(PREFS, MODE_PRIVATE);
        ScrollView scroll=new ScrollView(this);
        layout=new LinearLayout(this); layout.setOrientation(LinearLayout.VERTICAL); layout.setPadding(dp(20),dp(16),dp(20),dp(24));
        scroll.addView(layout); setContentView(scroll);
        TextView title=new TextView(this); title.setText("Joystick Visual"); title.setTextSize(28); layout.addView(title);
        label("Decorative animation. Does not read, send or modify touches or other apps.\nSet the position relative to available screen space; settings apply when you tap Start / apply.");
        status=label("");
        preview=new JoystickView(this);
        LinearLayout.LayoutParams pv=new LinearLayout.LayoutParams(dp(160),dp(160)); pv.gravity=Gravity.CENTER;
        layout.addView(preview,pv);
        slider("Position from left", "x", 0,100,12,"%");
        slider("Position from top", "y", 0,100,75,"%");
        slider("Diameter", "size", 70,260,150," dp");
        slider("Opacity", "alpha", 15,70,55,"%");
        slider("Speed", "speed", 25,250,100,"%");
        label("Animation");
        Spinner modes=new Spinner(this);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"Orbit","Pulse orbit","Side sweep"});
        modes.setAdapter(adapter); modes.setSelection(prefs.getInt("mode",0)); layout.addView(modes);
        modes.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            public void onItemSelected(android.widget.AdapterView<?> parent,android.view.View v,int position,long id) { prefs.edit().putInt("mode",position).apply(); refreshPreview(); }
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        button("Grant overlay permission",()->startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()))));
        button("Start / apply",()->{
            if (!Settings.canDrawOverlays(this)) { Toast.makeText(this,"Grant overlay permission first",Toast.LENGTH_LONG).show(); return; }
            if (Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},11);
            startForegroundService(new Intent(this,OverlayService.class));
            Toast.makeText(this,"Overlay started. Use Stop to remove it.",Toast.LENGTH_SHORT).show();
        });
        button("Stop overlay",()->stopService(new Intent(this,OverlayService.class)));
        refreshPreview();
    }
    @Override public void onResume() { super.onResume(); if(status!=null) status.setText(Settings.canDrawOverlays(this)?"Overlay permission: granted":"Overlay permission: needed"); }
    private int dp(int v) { return Math.round(v*getResources().getDisplayMetrics().density); }
    private TextView label(String str) { TextView t=new TextView(this); t.setText(str); t.setPadding(0,dp(8),0,dp(4)); layout.addView(t); return t; }
    private void button(String s,Runnable action){ Button b=new Button(this); b.setText(s); b.setOnClickListener(v->action.run()); layout.addView(b); }
    private void slider(String name,String key,int min,int max,int defaultValue,String unit) {
        int current=prefs.getInt(key,defaultValue); TextView title=label(name+": "+current+unit);
        SeekBar bar=new SeekBar(this); bar.setMax(max-min); bar.setProgress(current-min); layout.addView(bar);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar s,int progress,boolean user){int value=min+progress; title.setText(name+": "+value+unit); prefs.edit().putInt(key,value).apply(); refreshPreview();}
            public void onStartTrackingTouch(SeekBar s){} public void onStopTrackingTouch(SeekBar s){}
        });
    }
    private void refreshPreview(){ if(preview!=null) { preview.configure(prefs.getInt("mode",0),prefs.getInt("speed",100)/100f); preview.setAlpha(prefs.getInt("alpha",55)/100f); } }
}
