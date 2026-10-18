package com.fishguy129.modulartoasts.client.overlay;

import com.fishguy129.modulartoasts.api.Notification;
import com.fishguy129.modulartoasts.api.NotificationDisplay;
import com.fishguy129.modulartoasts.api.NotificationTheme;
import com.fishguy129.modulartoasts.client.ui.ToastDraw;
import com.fishguy129.modulartoasts.client.ui.TierFX;
import com.fishguy129.modulartoasts.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class NotificationOverlay {

    private static final int BASE_H = 48;     
    private static final int PAD_X = 12;
    private static final int PAD_Y = 10;

    private static final int ICON = 24;      
    private static final int GAP = 9;

    private static final int BASE_Y = 6;
    private static final int STACK_GAP_Y = 7;

    public enum RightAnchorMode {
        EDGE,
        RIGHT_HALF_CENTER
    }

    private static volatile RightAnchorMode RIGHT_ANCHOR_MODE = RightAnchorMode.EDGE;

    public static void setRightAnchorMode(RightAnchorMode mode) {
        RIGHT_ANCHOR_MODE = (mode == null) ? RightAnchorMode.EDGE : mode;
    }

    public static RightAnchorMode getRightAnchorMode() {
        return RIGHT_ANCHOR_MODE;
    }

    public static void render(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        List<ActiveNotification> actives = NotificationQueue.getActiveList();
        if (actives.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        int sw = mc.getWindow().getGuiScaledWidth();

        for (int i = 0; i < actives.size(); i++) {
            ActiveNotification active = actives.get(i);
            Notification n = active.notification();
            NotificationTheme t = n.theme();

            int width = computeWidth(font, n);

            NotificationDisplay.Anchor anchor = n.display().anchor();
            int x;
            if (anchor == NotificationDisplay.Anchor.TOP_RIGHT) {
                if (RIGHT_ANCHOR_MODE == RightAnchorMode.RIGHT_HALF_CENTER) {
                    int rightHalfCenterX = (int) (sw * 0.75f);
                    x = rightHalfCenterX - (width / 2);
                } else {
                    x = sw - width - 6;
                }
            } else {
                x = (sw - width) / 2;
            }

            float slide = active.slideProgress();
            float alpha = active.visibleAlpha();

            float depth = 1.0f - (i * 0.10f);
            alpha = Mth.clamp(alpha * depth, 0f, 1f);
            float scale = Mth.clamp(0.985f + (depth * 0.015f), 0.97f, 1.0f);

            float pulse = TierFX.pulse(n, partialTick);
            float sparkle = TierFX.sparkle(n, partialTick);
            float bob = TierFX.bob(n, partialTick);

            ItemStack icon = (n.icon() == null) ? ItemStack.EMPTY : n.icon();

            int textLeftBase = x + PAD_X + (icon.isEmpty() ? 0 : (ICON + GAP)) + 10;
            int textRight = x + width - PAD_X;

            boolean hasValue = n.valueLine() != null && !n.valueLine().isBlank();
            int reservedRight = hasValue ? 122 : 0;
            int maxTextW = Math.max(0, (textRight - reservedRight) - textLeftBase);

            List<FormattedCharSequence> titleLines = wrap(font, n.title(), maxTextW, 2);
            List<FormattedCharSequence> msgLines = wrap(font, n.message(), maxTextW, 2);

            int lines = titleLines.size() + msgLines.size();
            int dynH = BASE_H + Math.max(0, lines - 2) * 10;
            int height = Mth.clamp(dynH, 48, 68);

            int yTarget = BASE_Y + i * (height + STACK_GAP_Y);
            int y = (int) Mth.lerp(slide, -height - 2, yTarget);
            y += (int) bob;

            int bg = t.backgroundARGB();
            int outline = t.outlineARGB();
            int accent = t.accentARGB();

            var ps = g.pose();
            ps.pushPose();
            float cx = x + width / 2f;
            float cy = y + height / 2f;
            ps.translate(cx, cy, 0);
            ps.scale(scale, scale, 1f);
            ps.translate(-cx, -cy, 0);

            ToastDraw.drawRpgPanel(g, x, y, width, height, 12, bg, outline, accent, alpha, pulse, sparkle);

            float phase = TierFX.sweepPhase(n, partialTick);
            if (phase >= 0f) {
                ToastDraw.shineSweep(g, x + 14, y + 4, width - 28, height - 8, 0xFFFFFFFF, alpha, phase);
            }

            int iconBoxX = x + PAD_X + 8;
            int iconBoxY = y + (height - ICON) / 2;

            if (!icon.isEmpty()) {
                ToastDraw.drawIconSocket(g, iconBoxX - 3, iconBoxY - 3, ICON + 6, accent, alpha, pulse);

                final int ITEM = 16;
                int itemX = iconBoxX + (ICON - ITEM) / 2;
                int itemY = iconBoxY + (ICON - ITEM) / 2;

                g.renderItem(icon, itemX, itemY);
                g.renderItemDecorations(font, icon, itemX, itemY);
            }

            int titleColor = ColorUtil.withAlpha(t.titleARGB(), alpha);
            int textColor = ColorUtil.withAlpha(t.textARGB(), alpha);

            int ty = y + PAD_Y;
            int lineH = 10;

            for (FormattedCharSequence seq : titleLines) {
                g.drawString(font, seq, textLeftBase, ty, titleColor, true);
                ty += lineH;
            }
            ty += 2;

            for (FormattedCharSequence seq : msgLines) {
                g.drawString(font, seq, textLeftBase, ty, textColor, true);
                ty += lineH;
            }

            if (hasValue) {
                String v = trimToWidth(font, n.valueLine(), 110);
                int vw = font.width(v);

                int pillW = vw + 16;
                int pillH = 16;
                int pillX = (x + width - PAD_X) - pillW;
                int pillY = y + PAD_Y - 1;

                ToastDraw.drawValuePill(g, pillX, pillY, pillW, pillH, accent, alpha, pulse);
                g.drawString(font, v, pillX + 8, pillY + 4, ColorUtil.withAlpha(0xFFFFFFFF, alpha), true);
            }

            ps.popPose();

            if (!icon.isEmpty() && mc.player != null) {
                int hoverX0 = iconBoxX - 3;
                int hoverY0 = iconBoxY - 3;
                int hoverS = ICON + 6;

                boolean hover = mouseX >= hoverX0 && mouseX < (hoverX0 + hoverS)
                        && mouseY >= hoverY0 && mouseY < (hoverY0 + hoverS);

                if (hover) {
                    g.renderTooltip(mc.font, icon, mouseX, mouseY);
                }
            }
        }
    }

    private static int computeWidth(Font font, Notification n) {
        int base = 272;

        int titleW = font.width(n.title());
        int msgW = font.width(n.message());
        int valueW = (n.valueLine() == null) ? 0 : font.width(n.valueLine());

        int content = Math.max(titleW, msgW);
        content = Math.max(content, 168 + valueW);

        int w = Math.max(base, content + 92);
        return Mth.clamp(w, 248, 392);
    }

    private static List<FormattedCharSequence> wrap(Font font, String s, int maxW, int maxLines) {
        if (maxW <= 0) return List.of();
        if (s == null) s = "";

        List<FormattedCharSequence> split = font.split(Component.literal(s), maxW);
        if (split.isEmpty()) return List.of();

        List<FormattedCharSequence> out = new ArrayList<>();
        for (int i = 0; i < split.size() && i < maxLines; i++) {
            out.add(split.get(i));
        }
        return out;
    }

    private static String trimToWidth(Font font, String s, int maxW) {
        if (maxW <= 0) return "";
        if (s == null) return "";
        if (font.width(s) <= maxW) return s;
        int dots = font.width(".");
        return font.plainSubstrByWidth(s, Math.max(0, maxW - dots)) + ".";
    }
}