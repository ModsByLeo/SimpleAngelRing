package adudecalledleo.simpleangelring;

import adudecalledleo.simpleangelring.config.ModConfig;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import static adudecalledleo.simpleangelring.Initializer.id;
import static net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.createS2CPacket;

public final class ModNetworking {
    private ModNetworking() { }

    public static final Identifier PACKET_CONFIG_VERSION = id("config.version");
    public static final Identifier PACKET_CONFIG_SYNC = id("config.sync");

    public static void register() {
        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            if (server.isSinglePlayer())
                return;
            ServerPlayNetworking.registerReceiver(handler, PACKET_CONFIG_VERSION, ModNetworking::receiveConfigVersion);
            handler.sendPacket(createS2CPacket(PACKET_CONFIG_VERSION, PacketByteBufs.empty()));
        });
    }

    private static void receiveConfigVersion(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        long version = buf.readLong();
        if (version == ModConfig.getVersion())
            handler.sendPacket(createS2CPacket(PACKET_CONFIG_SYNC, ModConfig.get().toByteBuf()));
        else
            server.execute(() -> handler.disconnect(new TranslatableText("text.simpleangelring.config_version_mismatch")));
    }
}
