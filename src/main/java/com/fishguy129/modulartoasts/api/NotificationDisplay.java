package com.fishguy129.modulartoasts.api;

public record NotificationDisplay(
        Anchor anchor,
        int maxVisible,
        int priority,
        boolean stack
) {
    public enum Anchor {
        TOP_CENTER,
        TOP_RIGHT
    }

    public static NotificationDisplay defaultTopCenter() {
        return new NotificationDisplay(Anchor.TOP_CENTER, 3, 0, true);
    }

    public static NotificationDisplay important() {
        return new NotificationDisplay(Anchor.TOP_CENTER, 3, 10, true);
    }
}