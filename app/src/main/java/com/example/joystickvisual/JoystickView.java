package com.example.joystickvisual;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;

final class JoystickView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final long started = SystemClock.elapsedRealtime();
    private int mode;
    private float speed = 1f;

    JoystickView(Context context) { super(context); setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO); }
    void configure(int mode, float speed) { this.mode = mode; this.speed = speed; invalidate(); }

    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        float x = getWidth()/2f, y = getHeight()/2f, r = Math.min(x,y)*0.86f;
        if (r <= 0) return;
        float[] pos = Motion.sample((SystemClock.elapsedRealtime()-started)/1000.0, mode, speed);
        float kx = x + pos[0]*r*0.62f, ky = y + pos[1]*r*0.62f;
        p.setStyle(Paint.Style.FILL); p.setColor(0x22333333); c.drawCircle(x,y,r,p);
        p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(Math.max(2,r*0.032f)); p.setColor(0xE5FFFFFF);
        c.drawCircle(x,y,r,p); p.setColor(0x66FFFFFF); c.drawCircle(x,y,r*0.7f,p);
        p.setStyle(Paint.Style.FILL); p.setColor(0xE5FFFFFF); c.drawCircle(kx,ky,r*0.30f,p);
        p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(Math.max(2,r*0.026f)); p.setColor(Color.DKGRAY);
        c.drawCircle(kx,ky,r*0.30f,p);
        postInvalidateDelayed(33);
    }
}
