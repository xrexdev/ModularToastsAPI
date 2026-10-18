package com.fishguy129.modulartoasts.client.ui;

import com.fishguy129.modulartoasts.util.ColorUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public final class ToastDraw {
    private ToastDraw() {}

    public static void drawRpgPanel(
            GuiGraphics g,
            int x, int y, int w, int h,
            int radius,
            int bgARGB,
            int outlineARGB,
            int accentARGB,
            float alpha,
            float tierPulse01,
            float sparkle01
    ) {
        int bg = ColorUtil.withAlpha(bgARGB, alpha);
        int outline = ColorUtil.withAlpha(outlineARGB, alpha * 0.88f);

        int shadow1 = ColorUtil.withAlpha(0xFF000000, alpha * 0.38f);
        int shadow2 = ColorUtil.withAlpha(0xFF000000, alpha * 0.18f);

        int accentSoft = ColorUtil.withAlpha(darken(accentARGB, 0.06f), alpha * (0.65f + 0.15f * tierPulse01));
        int accentBright = ColorUtil.withAlpha(brighten(accentARGB, 0.26f), alpha * (0.18f + 0.18f * tierPulse01));

        fillRounded(g, x + 1, y + 2, w, h, radius, shadow1);
        fillRounded(g, x + 2, y + 4, w, h, radius, shadow2);

        fillRounded(g, x, y, w, h, radius, bg);

        strokeRounded(g, x, y, w, h, radius, 1, outline);

        int bevelHi = ColorUtil.withAlpha(0xFFFFFFFF, alpha * 0.10f);
        int bevelLo = ColorUtil.withAlpha(0xFF000000, alpha * 0.16f);
        strokeRounded(g, x + 1, y + 1, w - 2, h - 2, Math.max(0, radius - 1), 1, bevelHi);
        strokeRounded(g, x + 2, y + 2, w - 4, h - 4, Math.max(0, radius - 2), 1, bevelLo);

        int capH = Math.min(15, Math.max(12, h / 3));
        gradientVertical(g, x + 3, y + 3, w - 6, capH,
                ColorUtil.withAlpha(0x40FFF2D6, alpha),
                ColorUtil.withAlpha(0x00FFF2D6, alpha),
                1f);

        g.fill(x + 10, y + 3, x + w - 10, y + 4, ColorUtil.withAlpha(0xFFFFFFFF, alpha * 0.06f));

        gradientVertical(g, x + 3, y + 3 + capH, w - 6, h - (capH + 6),
                ColorUtil.withAlpha(0x00000000, alpha),
                ColorUtil.withAlpha(0x34000000, alpha),
                1f);

        drawAccentStrip(g, x + 3, y + 3, h - 6, accentARGB, alpha, tierPulse01);

        int stud = ColorUtil.withAlpha(brighten(accentARGB, 0.55f), alpha * (0.10f + 0.18f * sparkle01));
        int s = 2;
        g.fill(x + 10, y + 9, x + 10 + s, y + 9 + s, stud);
        g.fill(x + w - 12, y + 9, x + w - 12 + s, y + 9 + s, stud);
        g.fill(x + 10, y + h - 11, x + 10 + s, y + h - 11 + s, stud);
        g.fill(x + w - 12, y + h - 11, x + w - 12 + s, y + h - 11 + s, stud);

        strokeRounded(g, x + 1, y + 1, w - 2, h - 2, Math.max(0, radius - 1), 1, accentSoft);
        strokeRounded(g, x + 2, y + 2, w - 4, h - 4, Math.max(0, radius - 2), 1, accentBright);
    }

    public static void drawAccentStrip(GuiGraphics g, int x, int y, int h, int accentARGB, float alpha, float pulse01) {
        int w = 6;

        float glow = 0.06f + 0.22f * pulse01;

        int top = brighten(accentARGB, 0.20f);
        int mid = brighten(accentARGB, 0.45f);
        int bot = darken(accentARGB, 0.25f);

        gradientVertical(g, x, y, w, h, top, bot, alpha * (0.92f + glow));

        int line = ColorUtil.withAlpha(mid, alpha * (0.30f + glow));
        g.fill(x + 1, y + 2, x + 2, y + h - 2, line);

        int gem = ColorUtil.withAlpha(mid, alpha * (0.18f + 0.20f * pulse01));
        g.fill(x + 1, y + 9, x + w - 1, y + 10, gem);
        g.fill(x + 1, y + h - 10, x + w - 1, y + h - 9, gem);
    }

    public static void drawIconSocket(GuiGraphics g, int x, int y, int size, int accentARGB, float alpha, float pulse01) {
        int plate = ColorUtil.withAlpha(0xFF0A0C10, alpha * 0.55f);
        int rimOuter = ColorUtil.withAlpha(darken(accentARGB, 0.10f), alpha * (0.55f + 0.18f * pulse01));
        int rimInner = ColorUtil.withAlpha(brighten(accentARGB, 0.40f), alpha * (0.18f + 0.12f * pulse01));

        fillRounded(g, x, y, size, size, 9, plate);
        strokeRounded(g, x, y, size, size, 9, 1, rimOuter);
        strokeRounded(g, x + 1, y + 1, size - 2, size - 2, 8, 1, rimInner);

        gradientVertical(g, x + 2, y + 2, size - 4, 9, 0x30FFF2D6, 0x00FFF2D6, alpha);
    }

    public static void drawValuePill(GuiGraphics g, int x, int y, int w, int h, int accentARGB, float alpha, float pulse01) {
        int bg = ColorUtil.withAlpha(0xFF0A0C10, alpha * 0.62f);
        int outline = ColorUtil.withAlpha(accentARGB, alpha * (0.72f + 0.22f * pulse01));
        int inner = ColorUtil.withAlpha(0xFFFFFFFF, alpha * 0.10f);

        fillRounded(g, x, y, w, h, 7, bg);
        strokeRounded(g, x, y, w, h, 7, 1, outline);
        strokeRounded(g, x + 1, y + 1, w - 2, h - 2, 6, 1, inner);

        g.fill(x + 8, y + 1, x + w - 8, y + 2, ColorUtil.withAlpha(0xFFFFFFFF, alpha * 0.10f));
    }

    public static void shineSweep(GuiGraphics g, int x, int y, int w, int h, int shineARGB, float alpha, float phase01) {
        int bandW = Math.max(18, w / 4);
        int cx = x + (int) ((w + bandW) * phase01) - bandW;

        int sx0 = Math.max(x, cx);
        int sx1 = Math.min(x + w, cx + bandW);
        if (sx1 <= sx0) return;

        int steps = 18;
        for (int i = 0; i < steps; i++) {
            float t = i / (float) steps;
            float strength = Mth.sin(t * Mth.PI);
            strength = strength * strength;

            int col = ColorUtil.withAlpha(shineARGB, alpha * (0.05f + 0.17f * strength));
            int px0 = Mth.lerpInt(t, sx0, sx1);
            int px1 = Mth.lerpInt((i + 1) / (float) steps, sx0, sx1);
            g.fill(px0, y, px1, y + h, col);
        }
    }

    public static void gradientVertical(GuiGraphics g, int x, int y, int w, int h, int topARGB, int bottomARGB, float alpha) {
        int steps = Math.max(10, Math.min(26, h));
        for (int i = 0; i < steps; i++) {
            float t0 = i / (float) steps;
            float t1 = (i + 1) / (float) steps;
            int c = lerpARGB(topARGB, bottomARGB, t0);
            c = ColorUtil.withAlpha(c, alpha);
            int yy0 = y + Math.round(t0 * h);
            int yy1 = y + Math.round(t1 * h);
            g.fill(x, yy0, x + w, yy1, c);
        }
    }

    private static int lerpARGB(int a, int b, float t) {
        int aA = (a >>> 24) & 0xFF, aR = (a >>> 16) & 0xFF, aG = (a >>> 8) & 0xFF, aB = a & 0xFF;
        int bA = (b >>> 24) & 0xFF, bR = (b >>> 16) & 0xFF, bG = (b >>> 8) & 0xFF, bB = b & 0xFF;
        int oA = (int) (aA + (bA - aA) * t);
        int oR = (int) (aR + (bR - aR) * t);
        int oG = (int) (aG + (bG - aG) * t);
        int oB = (int) (aB + (bB - aB) * t);
        return (oA << 24) | (oR << 16) | (oG << 8) | oB;
    }

    private static int brighten(int argb, float amt) {
        int a = (argb >>> 24) & 0xFF;
        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = argb & 0xFF;
        r = clamp((int) (r + (255 - r) * amt));
        g = clamp((int) (g + (255 - g) * amt));
        b = clamp((int) (b + (255 - b) * amt));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int darken(int argb, float amt) {
        int a = (argb >>> 24) & 0xFF;
        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = argb & 0xFF;
        r = clamp((int) (r * (1f - amt)));
        g = clamp((int) (g * (1f - amt)));
        b = clamp((int) (b * (1f - amt)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int clamp(int v) { return Math.max(0, Math.min(255, v)); }

    private static void fillRounded(GuiGraphics g, int x, int y, int w, int h, int r, int argb) {
        ColorUtil.fillRounded(g, x, y, w, h, r, argb);
    }

    private static void strokeRounded(GuiGraphics g, int x, int y, int w, int h, int r, int thickness, int argb) {
        ColorUtil.strokeRounded(g, x, y, w, h, r, thickness, argb);
    }
}