package com.example.joystickvisual;

/** Decorative movement only: the coordinates are never sent to Android input. */
public final class Motion {
    private Motion() {}
    public static float[] sample(double seconds, int mode, float speed) {
        double t = seconds * Math.max(0.1f, speed);
        double angle = t * 1.8;
        double radius = 0.78;
        if (mode == 1) radius *= 0.4 + 0.6 * Math.pow(Math.sin(t * 1.2), 2);
        else if (mode == 2) { angle = 0.85 * Math.sin(t * 1.4); radius *= Math.sin(t * 1.1); }
        return new float[]{(float)(Math.cos(angle)*radius), (float)(Math.sin(angle)*radius)};
    }
}
