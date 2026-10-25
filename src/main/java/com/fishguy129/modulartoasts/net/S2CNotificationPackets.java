package com.fishguy129.modulartoasts.net;

import com.fishguy129.modulartoasts.ModularToasts;
import com.fishguy129.modulartoasts.api.Notification;
import com.fishguy129.modulartoasts.api.NotificationKind;
import com.fishguy129.modulartoasts.api.NotificationTheme;
import com.fishguy129.modulartoasts.api.NotificationDisplay;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

public record S2CNotificationPackets(Notification notification) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CNotificationPackets> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("modulartoasts", "notification"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CNotificationPackets> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public S2CNotificationPackets decode(RegistryFriendlyByteBuf buf) {
            return S2CNotificationPackets.decode(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, S2CNotificationPackets value) {
            S2CNotificationPackets.encode(buf, value);
        }
    };

    public static void encode(RegistryFriendlyByteBuf buf, S2CNotificationPackets pkt) {
        Notification n = pkt.notification();

        buf.writeVarInt(n.kind().ordinal());
        buf.writeUtf(n.title(), 256);
        buf.writeUtf(n.message(), 512);

        buf.writeBoolean(n.valueLine() != null);
        if (n.valueLine() != null) buf.writeUtf(n.valueLine(), 128);

        boolean hasIcon = n.icon() != null && !n.icon().isEmpty();
        buf.writeBoolean(hasIcon);
        if (hasIcon) ItemStack.STREAM_CODEC.encode(buf, n.icon());

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

        var d = n.display();
        buf.writeVarInt(d.anchor().ordinal());
        buf.writeVarInt(d.maxVisible());
        buf.writeVarInt(d.priority());
        buf.writeBoolean(d.stack());
    }

    public static S2CNotificationPackets decode(RegistryFriendlyByteBuf buf) {
        NotificationKind kind = NotificationKind.values()[buf.readVarInt()];
        String title = buf.readUtf(256);
        String message = buf.readUtf(512);

        String valueLine = null;
        if (buf.readBoolean()) valueLine = buf.readUtf(128);

        ItemStack icon = ItemStack.EMPTY;
        if (buf.readBoolean()) icon = ItemStack.STREAM_CODEC.decode(buf);

        int displayTicks = buf.readVarInt();

        NotificationTheme theme = new NotificationTheme(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );

        @Nullable SoundEvent sound = null;
        if (buf.readBoolean()) {
            ResourceLocation id = buf.readResourceLocation();
            sound = BuiltInRegistries.SOUND_EVENT.get(id);
        }

        boolean mergeable = buf.readBoolean();

        NotificationDisplay.Anchor anchor = NotificationDisplay.Anchor.values()[buf.readVarInt()];
        int maxVisible = buf.readVarInt();
        int priority = buf.readVarInt();
        boolean stack = buf.readBoolean();
        NotificationDisplay display = new NotificationDisplay(anchor, maxVisible, priority, stack);

        Notification notification = new Notification(kind, title, message, valueLine, icon, displayTicks, theme, sound, mergeable, display);
        return new S2CNotificationPackets(notification);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static void handle(S2CNotificationPackets pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            com.fishguy129.modulartoasts.client.overlay.NotificationQueue.enqueue(pkt.notification());
        });
    }
}
