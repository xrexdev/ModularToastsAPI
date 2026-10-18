package com.fishguy129.modulartoasts.commands;

import com.fishguy129.modulartoasts.ModularToasts;
import com.fishguy129.modulartoasts.client.overlay.NotificationOverlay;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;


@EventBusSubscriber(
        modid = ModularToasts.MODID,
        value = Dist.CLIENT
)
public final class ClientToastTestCommand {
    private ClientToastTestCommand() {}

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent e) {
        e.getDispatcher().register(
                Commands.literal("mtoastclient")
                        .then(Commands.literal("rightmode")
                                .then(Commands.literal("edge").executes(ctx -> {
                                    NotificationOverlay.setRightAnchorMode(NotificationOverlay.RightAnchorMode.EDGE);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Right anchor mode: EDGE"), false);
                                    return 1;
                                }))
                                .then(Commands.literal("center").executes(ctx -> {
                                    NotificationOverlay.setRightAnchorMode(NotificationOverlay.RightAnchorMode.RIGHT_HALF_CENTER);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Right anchor mode: RIGHT_HALF_CENTER"), false);
                                    return 1;
                                }))
                        )
        );
    }
}