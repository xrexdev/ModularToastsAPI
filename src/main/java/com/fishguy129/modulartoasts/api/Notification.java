package com.fishguy129.modulartoasts.api;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record Notification(
        NotificationKind kind,
        String title,
        String message,
        @Nullable String valueLine,
        @Nullable ItemStack icon,
        int displayTicks,
        NotificationTheme theme,
        @Nullable SoundEvent sound,
        boolean mergeable,
        NotificationDisplay display
) {}