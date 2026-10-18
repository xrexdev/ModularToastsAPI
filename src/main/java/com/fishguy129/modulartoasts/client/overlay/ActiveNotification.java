package com.fishguy129.modulartoasts.client.overlay;

import com.fishguy129.modulartoasts.api.Notification;

public final class ActiveNotification {
    private final Notification notification;
    private int age = 0;

    private static final int SLIDE_TICKS = 10;
    private static final int OUT_TICKS = 10;

    public ActiveNotification(Notification notification) {
        this.notification = notification;
    }

    public Notification notification() {
        return notification;
    }

    public void tick() {
        age++;
    }

    public boolean isDone() {
        return age >= notification.displayTicks() + OUT_TICKS;
    }

    public float visibleAlpha() {
        int endStart = notification.displayTicks();
        if (age < SLIDE_TICKS) return age / (float) SLIDE_TICKS;
        if (age < endStart) return 1f;
        int outAge = age - endStart;
        return 1f - (outAge / (float) OUT_TICKS);
    }

    public float slideProgress() {
        if (age < SLIDE_TICKS) return Math.max(0f, age / (float) SLIDE_TICKS);
        return 1f;
    }
}