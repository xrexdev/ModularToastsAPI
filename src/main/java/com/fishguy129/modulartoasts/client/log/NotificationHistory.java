package com.fishguy129.modulartoasts.client.log;

import com.fishguy129.modulartoasts.api.Notification;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class NotificationHistory {
    private NotificationHistory() {}

    private static final int MAX = 200;
    private static final List<Notification> HISTORY = new ArrayList<>();

    private static final int DEDUPE_WINDOW_TICKS = 2;

    private static long lastTick = -1;
    private static int lastSig = 0;

    public static void push(Notification n) {
        if (n == null) return;

        long tick = clientTick();
        int sig = signature(n);

        if (tick == lastTick && sig == lastSig) return;
        if (lastTick >= 0 && (tick - lastTick) <= DEDUPE_WINDOW_TICKS && sig == lastSig) return;

        lastTick = tick;
        lastSig = sig;

        HISTORY.add(0, n);
        if (HISTORY.size() > MAX) {
            HISTORY.remove(HISTORY.size() - 1);
        }
    }

    public static List<Notification> list() {
        return Collections.unmodifiableList(HISTORY);
    }

    public static void clear() {
        HISTORY.clear();
        lastTick = -1;
        lastSig = 0;
    }

    public static int size() {
        return HISTORY.size();
    }

    private static long clientTick() {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return 0L;
        return mc.level.getGameTime();
    }

    private static int signature(Notification n) {
        int h = 1;
        h = 31 * h + n.kind().hashCode();
        h = 31 * h + safeHash(n.title());
        h = 31 * h + safeHash(n.message());
        h = 31 * h + safeHash(n.valueLine());
        h = 31 * h + n.displayTicks();
        h = 31 * h + (n.mergeable() ? 1 : 0);

        ItemStack icon = n.icon();
        if (icon != null && !icon.isEmpty()) {
            h = 31 * h + icon.getItem().hashCode();
            h = 31 * h + icon.getCount();
        }

        h = 31 * h + n.display().anchor().hashCode();
        h = 31 * h + n.display().priority();
        h = 31 * h + n.display().maxVisible();
        h = 31 * h + (n.display().stack() ? 1 : 0);

        var t = n.theme();
        h = 31 * h + t.accentARGB();
        h = 31 * h + t.backgroundARGB();
        h = 31 * h + t.titleARGB();
        h = 31 * h + t.textARGB();
        h = 31 * h + t.outlineARGB();

        return h;
    }

    private static int safeHash(String s) {
        return (s == null) ? 0 : s.hashCode();
    }
}