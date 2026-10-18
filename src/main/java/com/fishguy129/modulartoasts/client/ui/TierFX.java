package com.fishguy129.modulartoasts.client.ui;

import com.fishguy129.modulartoasts.api.Notification;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public final class TierFX {
    private TierFX() {}

    private static float time(float partialTick) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return 0f;
        return (mc.level.getGameTime() + partialTick);
    }

    public static float pulse(Notification n, float partialTick) {
        int p = n.display().priority();
        if (p <= 0) return 0f;

        float t = time(partialTick);
        float speed = (p >= 10) ? 0.18f : (p >= 5 ? 0.14f : 0.11f);
        float raw = (Mth.sin(t * speed) + 1f) * 0.5f;

        float eased = raw * raw * (3f - 2f * raw);
        float strength = Mth.clamp(0.35f + (p * 0.05f), 0.45f, 1.00f);

        return Mth.clamp(eased * strength, 0f, 1f);
    }

    public static float bob(Notification n, float partialTick) {
        int p = n.display().priority();
        if (p <= 0) return 0f;

        float t = time(partialTick);
        float speed = (p >= 10) ? 0.10f : 0.085f;
        float amp = (p >= 10) ? 0.75f : (p >= 5 ? 0.55f : 0.35f);
        return Mth.sin(t * speed) * amp;
    }

    public static float sweepPhase(Notification n, float partialTick) {
        int p = n.display().priority();
        if (p < 10) return -1f;

        float t = time(partialTick);
        float period = 72f;
        return (t % period) / period;
    }

    public static float sparkle(Notification n, float partialTick) {
        int p = n.display().priority();
        if (p < 5) return 0f;

        float t = time(partialTick);
        float a = (Mth.sin(t * 0.22f) + 1f) * 0.5f;
        float b = (Mth.sin(t * 0.13f + 1.7f) + 1f) * 0.5f;
        float s = (a * 0.65f + b * 0.35f);

        return s * (p >= 10 ? 1.0f : 0.7f);
    }
}