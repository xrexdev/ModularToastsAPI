package com.fishguy129.modulartoasts;

import com.fishguy129.modulartoasts.net.Networks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(ModularToasts.MODID)
public class ModularToasts {
    public static final String MODID = "modulartoasts";

    public ModularToasts(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerPayloads);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        Networks.register(event.registrar("1"));
    }
}
