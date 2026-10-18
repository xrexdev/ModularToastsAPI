package com.fishguy129.modulartoasts.client;

import com.fishguy129.modulartoasts.ModularToasts;
import com.fishguy129.modulartoasts.client.overlay.NotificationOverlay;
import com.fishguy129.modulartoasts.client.overlay.NotificationQueue;
import com.fishguy129.modulartoasts.api.ModularToastsAPI;
import com.fishguy129.modulartoasts.api.NotificationTheme;
import com.fishguy129.modulartoasts.api.Notifications;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;



public final class ClientInit {

    public static void onClientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            ModularToastsAPI.notifyClient(
                    Notifications.custom(
                            "Modular Toasts API",
                            "Overlay is working!",
                            "RPG style online",
                            new ItemStack(Items.EMERALD),
                            80,
                            NotificationTheme.rpgDefault(),
                            null,
                            false
                    )
            );
        });
    }

    public static void registerOverlays(RegisterGuiLayersEvent e) {
        e.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath("modulartoasts", "notifications"),
                (graphics, deltaTracker) -> {
                    var mc = Minecraft.getInstance();
                    int mx = (int) (mc.mouseHandler.xpos() * (double) graphics.guiWidth() / (double) mc.getWindow().getScreenWidth());
                    int my = (int) (mc.mouseHandler.ypos() * (double) graphics.guiHeight() / (double) mc.getWindow().getScreenHeight());
                    NotificationOverlay.render(graphics, deltaTracker.getRealtimeDeltaTicks(), mx, my);
                }
        );
    }

    @EventBusSubscriber(modid = ModularToasts.MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post e) {
            NotificationQueue.clientTick();
        }
    }
}