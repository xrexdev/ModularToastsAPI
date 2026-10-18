package com.fishguy129.modulartoasts.commands;

import com.fishguy129.modulartoasts.ModularToasts;
import com.fishguy129.modulartoasts.api.ModularToastsAPI;
import com.fishguy129.modulartoasts.api.Notifications;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;


@EventBusSubscriber(modid = ModularToasts.MODID)
public final class ToastTestCommand {
    private ToastTestCommand() {}

    @SubscribeEvent
    public static void onRegister(RegisterCommandsEvent e) {
        e.getDispatcher().register(
                Commands.literal("mtoast")
                        .requires(cs -> cs.hasPermission(2))

                        .then(Commands.literal("custom").executes(ctx -> {
                            var p = ctx.getSource().getPlayerOrException();
                            ModularToastsAPI.notify(p, Notifications.custom(
                                    "Custom",
                                    "Hello from Modular Toasts!",
                                    "Value line",
                                    new ItemStack(Items.DIAMOND),
                                    80,
                                    null,
                                    null,
                                    false
                            ));
                            return 1;
                        }))

                        .then(Commands.literal("auction").executes(ctx -> {
                            var p = ctx.getSource().getPlayerOrException();
                            ItemStack sold = new ItemStack(Items.DIAMOND_SWORD);
                            ModularToastsAPI.notify(p, Notifications.auctionSold(
                                    sold,
                                    12500L,
                                    "$",
                                    null
                            ));
                            return 1;
                        }))

                        .then(Commands.literal("lootcrate").executes(ctx -> {
                            var p = ctx.getSource().getPlayerOrException();
                            ModularToastsAPI.notify(p, Notifications.lootCrateSpawn(
                                    "Supply Crate",
                                    null
                            ));
                            return 1;
                        }))

                        .then(Commands.literal("skill").executes(ctx -> {
                            var p = ctx.getSource().getPlayerOrException();
                            ModularToastsAPI.notify(p, Notifications.skillGain(
                                    "Mining",
                                    12,
                                    null
                            ));
                            return 1;
                        }))

                        .then(Commands.literal("all").executes(ctx -> {
                            var p = ctx.getSource().getPlayerOrException();
                            ModularToastsAPI.notify(p, Notifications.auctionSold(new ItemStack(Items.EMERALD), 3400, "$", null));
                            ModularToastsAPI.notify(p, Notifications.lootCrateSpawn("Supply Crate", null));
                            ModularToastsAPI.notify(p, Notifications.skillGain("Swords", 5, null));
                            ctx.getSource().sendSuccess(() -> Component.literal("Sent 3 test toasts."), false);
                            return 1;
                        }))

                        .then(Commands.literal("spam").executes(ctx -> {
                            var p = ctx.getSource().getPlayerOrException();
                            for (int i = 0; i < 8; i++) {
                                ModularToastsAPI.notify(p, Notifications.skillGain("Alchemy", i + 1, null));
                            }
                            ctx.getSource().sendSuccess(() -> Component.literal("Spammed skill toasts (merge/cooldown test)."), false);
                            return 1;
                        }))
        );
    }
}