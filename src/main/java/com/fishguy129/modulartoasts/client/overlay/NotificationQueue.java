package com.fishguy129.modulartoasts.client.overlay;

import com.fishguy129.modulartoasts.api.Notification;
import com.fishguy129.modulartoasts.client.log.NotificationHistory;

import java.util.*;

public final class NotificationQueue {
    private NotificationQueue() {}

    private static final Deque<Notification> PENDING = new ArrayDeque<>();
    private static final List<ActiveNotification> ACTIVE = new ArrayList<>();

    private static final Map<String, Integer> COOLDOWNS = new HashMap<>();

    public static void enqueue(Notification n) {
        if (n == null) return;

        String key = cooldownKey(n);
        if (isOnCooldown(key)) return;

        NotificationHistory.push(n);

        if (!n.display().stack()) {
            replaceExisting(n);
        }

        if (n.mergeable()) {
            if (mergeIntoExisting(n)) {
                setCooldown(key, cooldownTicksFor(n));
                return;
            }
        }

        insertByPriority(n);

        setCooldown(key, cooldownTicksFor(n));
    }

    public static void clientTick() {
        tickCooldowns();

        for (int i = ACTIVE.size() - 1; i >= 0; i--) {
            ActiveNotification a = ACTIVE.get(i);
            a.tick();
            if (a.isDone()) ACTIVE.remove(i);
        }

        int maxVisible = computeMaxVisible();
        while (ACTIVE.size() < maxVisible) {
            Notification next = pollHighestPriority();
            if (next == null) break;
            ACTIVE.add(new ActiveNotification(next));

            if (next.sound() != null) {
                var mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.player != null) mc.player.playSound(next.sound(), 1.0f, 1.0f);
            }
        }
    }

    public static List<ActiveNotification> getActiveList() {
        return ACTIVE;
    }

    private static void insertByPriority(Notification n) {
        if (PENDING.isEmpty()) {
            PENDING.addLast(n);
            return;
        }

        int pr = n.display().priority();
        List<Notification> temp = new ArrayList<>(PENDING.size() + 1);

        boolean inserted = false;
        for (Notification cur : PENDING) {
            if (!inserted && pr > cur.display().priority()) {
                temp.add(n);
                inserted = true;
            }
            temp.add(cur);
        }
        if (!inserted) temp.add(n);

        PENDING.clear();
        for (Notification x : temp) PENDING.addLast(x);
    }

    private static Notification pollHighestPriority() {
        return PENDING.pollFirst();
    }

    private static int computeMaxVisible() {
        int max = 1;
        for (Notification n : PENDING) max = Math.max(max, n.display().maxVisible());
        for (ActiveNotification a : ACTIVE) max = Math.max(max, a.notification().display().maxVisible());
        return max;
    }

    private static boolean mergeIntoExisting(Notification n) {
        var list = new ArrayList<>(PENDING);
        for (int i = list.size() - 1; i >= 0; i--) {
            Notification p = list.get(i);
            if (canMerge(p, n)) {
                list.set(i, n);
                PENDING.clear();
                for (Notification x : list) PENDING.addLast(x);
                return true;
            }
        }
        for (int i = 0; i < ACTIVE.size(); i++) {
            Notification a = ACTIVE.get(i).notification();
            if (canMerge(a, n)) {
                ACTIVE.set(i, new ActiveNotification(n));
                return true;
            }
        }
        return false;
    }

    private static boolean canMerge(Notification a, Notification b) {
        return a.kind() == b.kind() && a.title().equals(b.title());
    }

    private static void replaceExisting(Notification n) {
        for (int i = 0; i < ACTIVE.size(); i++) {
            Notification a = ACTIVE.get(i).notification();
            if (a.kind() == n.kind() && a.title().equals(n.title())) {
                ACTIVE.set(i, new ActiveNotification(n));
                return;
            }
        }
        var list = new ArrayList<>(PENDING);
        for (int i = list.size() - 1; i >= 0; i--) {
            Notification p = list.get(i);
            if (p.kind() == n.kind() && p.title().equals(n.title())) {
                list.set(i, n);
                PENDING.clear();
                for (Notification x : list) PENDING.addLast(x);
                return;
            }
        }
    }

    private static String cooldownKey(Notification n) {
        return n.kind().name();
    }

    private static int cooldownTicksFor(Notification n) {
        return switch (n.kind()) {
            case SKILL_GAIN -> 6;
            case AUCTION_SOLD -> 2;
            case LOOTCRATE_SPAWN -> 40;
            default -> 4;
        };
    }

    private static boolean isOnCooldown(String key) {
        return COOLDOWNS.getOrDefault(key, 0) > 0;
    }

    private static void setCooldown(String key, int ticks) {
        if (ticks <= 0) return;
        COOLDOWNS.put(key, Math.max(COOLDOWNS.getOrDefault(key, 0), ticks));
    }

    private static void tickCooldowns() {
        if (COOLDOWNS.isEmpty()) return;
        var it = COOLDOWNS.entrySet().iterator();
        while (it.hasNext()) {
            var e = it.next();
            int v = e.getValue() - 1;
            if (v <= 0) it.remove();
            else e.setValue(v);
        }
    }
}