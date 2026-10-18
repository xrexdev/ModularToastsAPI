package com.fishguy129.modulartoasts.client;

import com.fishguy129.modulartoasts.ModularToasts;
import com.fishguy129.modulartoasts.client.log.NotificationLogScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public final class Keybinds {

    public static final KeyMapping OPEN_LOG = new KeyMapping(
            "key.modulartoasts.open_log",
            GLFW.GLFW_KEY_O,
            "key.categories.modulartoasts"
    );

    public static void register(RegisterKeyMappingsEvent e) {
        e.register(OPEN_LOG);
    }

    @EventBusSubscriber(modid = ModularToasts.MODID, value = Dist.CLIENT)
    public static class Events {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post e) {
            if (OPEN_LOG.consumeClick()) {
                Minecraft.getInstance().setScreen(new NotificationLogScreen());
            }
        }
    }
}