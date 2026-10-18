package com.fishguy129.modulartoasts.util;

import net.minecraft.client.gui.GuiGraphics;

public final class ColorUtil {
    private ColorUtil() {}

    public static int withAlpha(int argb, float alpha01) {
        int a = (argb >>> 24) & 0xFF;
        int na = Math.max(0, Math.min(255, (int) (a * alpha01)));
        return (na << 24) | (argb & 0x00FFFFFF);
    }

    public static void fillRounded(GuiGraphics g, int x, int y, int w, int h, int r, int color) {
        g.fill(x + r, y, x + w - r, y + h, color);
        g.fill(x, y + r, x + w, y + h - r, color);

        g.fill(x, y, x + r, y + r, color);
        g.fill(x + w - r, y, x + w, y + r, color);
        g.fill(x, y + h - r, x + r, y + h, color);
        g.fill(x + w - r, y + h - r, x + w, y + h, color);
    }

    public static void strokeRounded(GuiGraphics g, int x, int y, int w, int h, int r, int thickness, int color) {
        g.fill(x, y, x + w, y + thickness, color);
        g.fill(x, y + h - thickness, x + w, y + h, color);
        g.fill(x, y, x + thickness, y + h, color);
        g.fill(x + w - thickness, y, x + w, y + h, color);
    }
}