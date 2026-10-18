package com.fishguy129.modulartoasts.api;

import com.fishguy129.modulartoasts.util.MoneyFormat;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class Notifications {
    private Notifications() {}

    // ---------------- CUSTOM ----------------

    /** Deprecated kinda */
    public static Notification custom(
            String title,
            String message,
            @Nullable String valueLine,
            @Nullable ItemStack icon,
            int displayTicks,
            @Nullable NotificationTheme theme,
            @Nullable SoundEvent sound,
            boolean mergeable
    ) {
        return custom(
                title, message, valueLine, icon, displayTicks,
                theme, sound, mergeable,
                NotificationDisplay.defaultTopCenter()
        );
    }

    /* Custom with anchor/maxVisible/priority/stack display */
    public static Notification custom(
            String title,
            String message,
            @Nullable String valueLine,
            @Nullable ItemStack icon,
            int displayTicks,
            @Nullable NotificationTheme theme,
            @Nullable SoundEvent sound,
            boolean mergeable,
            NotificationDisplay display
    ) {
        return new Notification(
                NotificationKind.CUSTOM,
                title, message, valueLine,
                (icon == null) ? ItemStack.EMPTY : icon.copy(),
                displayTicks,
                (theme == null) ? NotificationTheme.rpgDefault() : theme,
                sound,
                mergeable,
                (display == null) ? NotificationDisplay.defaultTopCenter() : display
        );
    }

    /* Custom without creating a NotificationTheme object manually */
    public static Notification customColors(
            String title,
            String message,
            @Nullable String valueLine,
            @Nullable ItemStack icon,
            int displayTicks,
            int accentARGB,
            int backgroundARGB,
            int titleARGB,
            int textARGB,
            int outlineARGB,
            @Nullable SoundEvent sound,
            boolean mergeable,
            NotificationDisplay display
    ) {
        return custom(
                title, message, valueLine, icon, displayTicks,
                new NotificationTheme(accentARGB, backgroundARGB, titleARGB, textARGB, outlineARGB),
                sound,
                mergeable,
                display
        );
    }

    /* Max customization. */
    public static CustomBuilder customBuilder() {
        return new CustomBuilder();
    }

    public static final class CustomBuilder {
        private String title = "Notification";
        private String message = "";
        private String valueLine = null;
        private ItemStack icon = ItemStack.EMPTY;
        private int displayTicks = 80;
        private NotificationTheme theme = NotificationTheme.rpgDefault();
        private SoundEvent sound = null;
        private boolean mergeable = false;
        private NotificationDisplay display = NotificationDisplay.defaultTopCenter();

        private CustomBuilder() {}

        public CustomBuilder title(String v) { this.title = (v == null) ? "" : v; return this; }
        public CustomBuilder message(String v) { this.message = (v == null) ? "" : v; return this; }
        public CustomBuilder value(@Nullable String v) { this.valueLine = (v == null || v.isBlank()) ? null : v; return this; }
        public CustomBuilder icon(@Nullable ItemStack v) { this.icon = (v == null) ? ItemStack.EMPTY : v.copy(); return this; }
        public CustomBuilder ticks(int v) { this.displayTicks = Math.max(1, v); return this; }

        public CustomBuilder theme(@Nullable NotificationTheme t) { this.theme = (t == null) ? NotificationTheme.rpgDefault() : t; return this; }
        public CustomBuilder colors(int accent, int bg, int title, int text, int outline) {
            this.theme = new NotificationTheme(accent, bg, title, text, outline);
            return this;
        }

        public CustomBuilder sound(@Nullable SoundEvent s) { this.sound = s; return this; }
        public CustomBuilder mergeable(boolean v) { this.mergeable = v; return this; }

        public CustomBuilder display(NotificationDisplay.Anchor anchor, int maxVisible, int priority, boolean stack) {
            this.display = new NotificationDisplay(anchor, maxVisible, priority, stack);
            return this;
        }

        public CustomBuilder display(@Nullable NotificationDisplay d) {
            this.display = (d == null) ? NotificationDisplay.defaultTopCenter() : d;
            return this;
        }

        public Notification build() {
            return Notifications.custom(
                    title, message, valueLine,
                    icon, displayTicks,
                    theme, sound, mergeable,
                    display
            );
        }
    }

    // ---------------- PRESETS ----------------

    public static Notification auctionSold(ItemStack soldItem, long price, String currencySymbol, NotificationTheme themeIgnored) {
        String msg = soldItem.isEmpty() ? "Item sold" : ("Sold: " + soldItem.getHoverName().getString());
        String value = MoneyFormat.format(price, currencySymbol);
        return tiered(
                NotificationKind.AUCTION_SOLD,
                ToastTier.RARE,
                "Auction House",
                msg,
                value,
                soldItem.copy(),
                null,
                false
        );
    }

    public static Notification lootCrateSpawn(String crateName, NotificationTheme themeIgnored) {
        return tiered(
                NotificationKind.LOOTCRATE_SPAWN,
                ToastTier.EPIC,
                "World Event",
                "A " + crateName + " has spawned!",
                null,
                ItemStack.EMPTY,
                null,
                false
        );
    }

    public static Notification skillGain(String skillName, int newLevel, NotificationTheme themeIgnored) {
        return tiered(
                NotificationKind.SKILL_GAIN,
                ToastTier.COMMON,
                "Skill Up!",
                skillName,
                "Level " + newLevel,
                ItemStack.EMPTY,
                50,
                true
        );
    }

    public static Notification tiered(
            NotificationKind kind,
            ToastTier tier,
            String title,
            String message,
            @Nullable String valueLine,
            @Nullable ItemStack icon,
            @Nullable Integer displayTicksOverride,
            boolean mergeable
    ) {
        int ticks = (displayTicksOverride != null) ? displayTicksOverride : tier.defaultTicks;

        return new Notification(
                kind,
                title,
                message,
                valueLine,
                (icon == null) ? ItemStack.EMPTY : icon.copy(),
                ticks,
                tier.theme,
                tier.sound,
                mergeable,
                tier.display
        );
    }
}