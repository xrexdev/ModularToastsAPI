package com.fishguy129.modulartoasts.client;

import com.fishguy129.modulartoasts.ModularToasts;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;


@EventBusSubscriber(modid = ModularToasts.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModularToastsClient {

    private ModularToastsClient() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ClientInit.onClientSetup(event);
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        ClientInit.registerOverlays(event);
    }

    @SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        Keybinds.register(event);
    }
}