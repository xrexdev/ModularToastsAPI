package com.fishguy129.modulartoasts.net;

import com.fishguy129.modulartoasts.ModularToasts;
import com.fishguy129.modulartoasts.api.Notification;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class Network {
    private Network() {}

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(ModularToasts.MODID, "main"))
            .networkProtocolVersion(() -> PROTOCOL)
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .simpleChannel();

    private static int index = 0;

    public static void init() {
        CHANNEL.messageBuilder(S2CNotificationPacket.class, index++)
                .encoder(S2CNotificationPacket::encode)
                .decoder(S2CNotificationPacket::decode)
                .consumerMainThread(S2CNotificationPacket::handle)
                .add();
    }

    public static void sendPacket(ServerPlayer player, S2CNotificationPacket packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendNotification(ServerPlayer player, Notification notification) {
        sendPacket(player, new S2CNotificationPacket(notification));
    }

    private static void sendToPlayer(ServerPlayer player, Object packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}