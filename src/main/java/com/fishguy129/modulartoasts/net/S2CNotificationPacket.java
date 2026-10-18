package com.fishguy129.modulartoasts.net;

import com.fishguy129.modulartoasts.api.Notification;
import com.fishguy129.modulartoasts.api.NotificationKind;
import com.fishguy129.modulartoasts.api.NotificationTheme;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record S2CNotificationPacket(Notification notification) {

    public static void encode(S2CNotificationPacket pkt, FriendlyByteBuf buf) {
        Notification n = pkt.notification();

        buf.writeVarInt(n.kind().ordinal());
        buf.writeUtf(n.title(), 256);
        buf.writeUtf(n.message(), 512);

        buf.writeBoolean(n.valueLine() != null);
        if (n.valueLine() != null) buf.writeUtf(n.valueLine(), 128);

        boolean hasIcon = n.icon() != null && !n.icon().isEmpty();
        buf.writeBoolean(hasIcon);
        if (hasIcon) buf.writeItem(n.icon());

        buf.writeVarInt(n.displayTicks());

        NotificationTheme t = n.theme();
        buf.writeInt(t.accentARGB());
        buf.writeInt(t.backgroundARGB());
        buf.writeInt(t.titleARGB());
        buf.writeInt(t.textARGB());
        buf.writeInt(t.outlineARGB());

        buf.writeBoolean(n.sound() != null);
        if (n.sound() != null) {
            ResourceLocation id = BuiltInRegistries.SOUND_EVENT.getKey(n.sound());
            buf.writeResourceLocation(id);
        }

        buf.writeBoolean(n.mergeable());
        // display
        var d = n.display();
        buf.writeVarInt(d.anchor().ordinal());
        buf.writeVarInt(d.maxVisible());
        buf.writeVarInt(d.priority());
        buf.writeBoolean(d.stack());
    }

    public static S2CNotificationPacket decode(FriendlyByteBuf buf) {
        NotificationKind kind = NotificationKind.values()[buf.readVarInt()];
        String title = buf.readUtf(256);
        String message = buf.readUtf(512);

        String valueLine = null;
        if (buf.readBoolean()) valueLine = buf.readUtf(128);

        ItemStack icon = ItemStack.EMPTY;
        if (buf.readBoolean()) icon = buf.readItem();

        int displayTicks = buf.readVarInt();

        NotificationTheme theme = new NotificationTheme(
                buf.readInt(), // accent
                buf.readInt(), // bg
                buf.readInt(), // title
                buf.readInt(), // text
                buf.readInt()  // outline
        );

        @Nullable SoundEvent sound = null;
        if (buf.readBoolean()) {
            ResourceLocation id = buf.readResourceLocation();
            sound = BuiltInRegistries.SOUND_EVENT.get(id);
        }

        boolean mergeable = buf.readBoolean();

        var anchor = com.fishguy129.modulartoasts.api.NotificationDisplay.Anchor.values()[buf.readVarInt()];
        int maxVisible = buf.readVarInt();
        int priority = buf.readVarInt();
        boolean stack = buf.readBoolean();
        var display = new com.fishguy129.modulartoasts.api.NotificationDisplay(anchor, maxVisible, priority, stack);


        Notification n = new Notification(
                kind,
                title,
                message,
                valueLine,
                icon,
                displayTicks,
                theme,
                sound,
                mergeable,
                display
        );
        return new S2CNotificationPacket(n);
    }

    public static void handle(S2CNotificationPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Safe: only runs on client
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    com.fishguy129.modulartoasts.client.overlay.NotificationQueue.enqueue(pkt.notification())
            );
        });
        ctx.get().setPacketHandled(true);
    }
}