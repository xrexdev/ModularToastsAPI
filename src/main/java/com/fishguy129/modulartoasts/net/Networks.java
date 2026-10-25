package com.fishguy129.modulartoasts.net;

import com.fishguy129.modulartoasts.ModularToasts;
import com.fishguy129.modulartoasts.api.Notification;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class Networks {
    private Networks() {}

    public static void register(PayloadRegistrar registrar) {
        registrar.playToClient(
                S2CNotificationPackets.TYPE,
                S2CNotificationPackets.STREAM_CODEC,
                S2CNotificationPackets::handle
        );
    }

    public static void sendNotification(ServerPlayer player, Notification notification) {
        PacketDistributor.sendToPlayer(player, new S2CNotificationPackets(notification));
    }
}
