package com.fishguy129.modulartoasts.client.log;

import com.fishguy129.modulartoasts.api.Notification;
import com.fishguy129.modulartoasts.api.NotificationTheme;
import com.fishguy129.modulartoasts.client.ui.ToastDraw;
import com.fishguy129.modulartoasts.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class NotificationLogScreen extends Screen {

    private int scrollPx = 0;

    public NotificationLogScreen() {
        super(Component.literal("Notification Log"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollPx -= (int) (verticalAmount * 18);
        if (scrollPx < 0) scrollPx = 0;
        return true;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        NotificationTheme theme = NotificationTheme.rpgDefault();

        int sw = width;
        int sh = height;

        int panelW = Math.min(520, sw - 40);
        int panelH = Math.min(320, sh - 40);

        int x = (sw - panelW) / 2;
        int y = (sh - panelH) / 2;

        // Panel
        float alpha = 1f;
        ToastDraw.drawRpgPanel(
                g, x, y, panelW, panelH, 12,
                theme.backgroundARGB(),
                theme.outlineARGB(),
                theme.accentARGB(),
                alpha,
                0.15f,
                0.10f
        );

        // Header
        g.drawString(font, "Notification Log", x + 16, y + 12, 0xFFFFFFFF, true);
        g.drawString(font, "Scroll • ESC to close", x + 16, y + 26, 0xFFB0B0B0, false);

        // List bounds
        int listX = x + 14;
        int listY = y + 44;
        int listW = panelW - 28;
        int listH = panelH - 62;

        // Inner list background
        int listBg = ColorUtil.withAlpha(0xFF000000, 0.22f);
        ColorUtil.fillRounded(g, listX, listY, listW, listH, 10, listBg);

        // Rows
        List<Notification> items = NotificationHistory.list();

        final int rowH = 30;
        int totalH = items.size() * rowH;

        int maxScroll = Math.max(0, totalH - listH);
        scrollPx = Mth.clamp(scrollPx, 0, maxScroll);

        // Clip list rendering
        g.enableScissor(listX, listY, listX + listW, listY + listH);

        int startIndex = scrollPx / rowH;
        int yOffset = -(scrollPx % rowH);

        int hoverIndex = -1;

        for (int i = startIndex; i < items.size(); i++) {
            int ry = listY + yOffset + (i - startIndex) * rowH;
            if (ry > listY + listH) break;
            if (ry + rowH < listY) continue;

            Notification n = items.get(i);

            // Hover
            boolean hover = mouseX >= listX && mouseX < (listX + listW)
                    && mouseY >= ry && mouseY < (ry + rowH);
            if (hover) hoverIndex = i;

            int accent = n.theme().accentARGB();
            int rowBase = (i % 2 == 0) ? ColorUtil.withAlpha(0xFF000000, 0.20f) : ColorUtil.withAlpha(0xFF000000, 0.14f);
            int rowHover = ColorUtil.withAlpha(0xFFFFFFFF, 0.06f);

            // Row background
            g.fill(listX + 8, ry + 2, listX + listW - 8, ry + rowH - 2, rowBase);
            if (hover) {
                g.fill(listX + 8, ry + 2, listX + listW - 8, ry + rowH - 2, rowHover);
            }

            // Accent bar
            g.fill(listX + 10, ry + 6, listX + 12, ry + rowH - 6, ColorUtil.withAlpha(accent, 0.85f));

            // Icon socket
            ItemStack icon = (n.icon() == null) ? ItemStack.EMPTY : n.icon();
            int ixBox = listX + 18;
            int iyBox = ry + (rowH - 24) / 2;

            if (!icon.isEmpty()) {
                ToastDraw.drawIconSocket(g, ixBox - 3, iyBox - 3, 30, accent, 1f, 0.12f);
                int itemX = ixBox + (24 - 16) / 2;
                int itemY = iyBox + (24 - 16) / 2;
                g.renderItem(icon, itemX, itemY);
            }

            int tx = listX + (icon.isEmpty() ? 18 : 54);
            int right = listX + listW - 16;

            // Title and Message
            String line1 = n.title() + " — " + n.message();
            line1 = trim(font, line1, right - tx - 120);

            g.drawString(font, line1, tx, ry + 8, 0xFFFFFFFF, false);

            // Value pill
            if (n.valueLine() != null && !n.valueLine().isBlank()) {
                String val = trim(font, n.valueLine(), 110);
                int vw = font.width(val);

                int pillW = vw + 16;
                int pillH = 16;

                int pillX = right - pillW;
                int pillY = ry + 7;

                ToastDraw.drawValuePill(g, pillX, pillY, pillW, pillH, accent, 1f, 0.10f);
                g.drawString(font, val, pillX + 8, pillY + 4, 0xFFFFFFFF, true);
            }
        }

        g.disableScissor();

        if (hoverIndex >= 0 && hoverIndex < items.size()) {
            Notification n = items.get(hoverIndex);

            List<Component> lines = new java.util.ArrayList<>();
            lines.add(Component.literal(n.title()));
            lines.add(Component.literal(n.message()));
            if (n.valueLine() != null && !n.valueLine().isBlank()) {
                lines.add(Component.literal(n.valueLine()));
            }

            g.renderTooltip(font, lines, java.util.Optional.empty(), mouseX, mouseY);
        }

        // Scrollbar
        if (maxScroll > 0) {
            int barX = listX + listW - 10;
            int barY = listY + 8;
            int barH = listH - 16;

            int track = ColorUtil.withAlpha(0xFF000000, 0.35f);
            int thumb = ColorUtil.withAlpha(theme.accentARGB(), 0.70f);
            int thumbHi = ColorUtil.withAlpha(0xFFFFFFFF, 0.10f);

            g.fill(barX, barY, barX + 4, barY + barH, track);

            float t = scrollPx / (float) maxScroll;
            int thumbH = Mth.clamp((int) (barH * (listH / (float) Math.max(listH, totalH))), 18, barH);
            int thumbY = barY + (int) ((barH - thumbH) * t);

            g.fill(barX, thumbY, barX + 4, thumbY + thumbH, thumb);
            g.fill(barX, thumbY, barX + 4, thumbY + 1, thumbHi);
        }

        // Footer
        String footer = "Stored: " + NotificationHistory.size();
        int fw = font.width(footer);
        g.drawString(font, footer, x + panelW - 16 - fw, y + panelH - 16, 0xFFB0B0B0, false);

        super.render(g, mouseX, mouseY, partialTick);
    }

    private static String trim(Font font, String s, int maxW) {
        if (maxW <= 0) return "";
        if (font.width(s) <= maxW) return s;
        int dots = font.width("...");
        return font.plainSubstrByWidth(s, Math.max(0, maxW - dots)) + "...";
    }
}