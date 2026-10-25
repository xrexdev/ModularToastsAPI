package com.fishguy129.modulartoasts.api;


import com.fishguy129.modulartoasts.net.Networks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLEnvironment;


public final class ModularToastsAPI {
    private ModularToastsAPI() {}

    public static void notify(ServerPlayer player, Notification notification) {
        Networks.sendNotification(player, notification);
    }

    public static void notifyAll(MinecraftServer server, Notification notification) {
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            notify(p, notification);
        }
    }

    public static void notifyClient(Notification notification) {
        if (FMLEnvironment.dist.isClient()){
            com.fishguy129.modulartoasts.client.overlay.NotificationQueue.enqueue(notification);
        }
    }
}