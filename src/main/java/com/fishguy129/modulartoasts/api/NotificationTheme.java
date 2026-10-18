package com.fishguy129.modulartoasts.api;

public record NotificationTheme(
        int accentARGB,
        int backgroundARGB,
        int titleARGB,
        int textARGB,
        int outlineARGB
) {
    public static NotificationTheme rpgDefault() {
        return new NotificationTheme(
                0xFFE3B341,
                0xCC0B0F14,
                0xFFFFFFFF,
                0xFFE6E6E6,
                0xFF2B2B2B
        );
    }

    public static NotificationTheme of(int accent, int bg, int title, int text, int outline) {
        return new NotificationTheme(accent, bg, title, text, outline);
    }
}