package com.fishguy129.modulartoasts.api;

import net.minecraft.sounds.SoundEvents;

public enum ToastTier {
    COMMON(
            NotificationTheme.of(0xFFB0B0B0, 0xCC0B0F14, 0xFFFFFFFF, 0xFFE6E6E6, 0xFF2B2B2B),
            NotificationDisplay.defaultTopCenter(),
            60,
            null
    ),
    RARE(
            NotificationTheme.of(0xFF4D96FF, 0xCC0B0F14, 0xFFFFFFFF, 0xFFE6E6E6, 0xFF2B2B2B),
            new NotificationDisplay(NotificationDisplay.Anchor.TOP_CENTER, 3, 2, true),
            70,
            SoundEvents.PLAYER_LEVELUP
    ),
    EPIC(
            NotificationTheme.of(0xFFB96CFF, 0xCC0B0F14, 0xFFFFFFFF, 0xFFE6E6E6, 0xFF2B2B2B),
            new NotificationDisplay(NotificationDisplay.Anchor.TOP_CENTER, 3, 6, true),
            90,
            SoundEvents.ENDER_DRAGON_GROWL
    ),
    LEGENDARY(
            NotificationTheme.of(0xFFFFC857, 0xCC0B0F14, 0xFFFFFFFF, 0xFFE6E6E6, 0xFF2B2B2B),
            new NotificationDisplay(NotificationDisplay.Anchor.TOP_CENTER, 3, 10, true),
            110,
            SoundEvents.TOTEM_USE
    );

    public final NotificationTheme theme;
    public final NotificationDisplay display;
    public final int defaultTicks;
    public final net.minecraft.sounds.SoundEvent sound;

    ToastTier(NotificationTheme theme, NotificationDisplay display, int defaultTicks, net.minecraft.sounds.SoundEvent sound) {
        this.theme = theme;
        this.display = display;
        this.defaultTicks = defaultTicks;
        this.sound = sound;
    }
}