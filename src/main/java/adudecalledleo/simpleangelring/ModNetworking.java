package adudecalledleo.simpleangelring;

import adudecalledleo.simpleangelring.config.ModConfig;
import adudecalledleo.simpleangelring.config.ModConfigClient;
import adudecalledleo.simpleangelring.config.ModConfigServer;
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
    public static final Identifier PACKET_CONFIG_SYNC_CLIENT = id("config.sync.client");

    public static void register() {
        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            ServerPlayNetworking.registerReceiver(handler, PACKET_CONFIG_VERSION, ModNetworking::receiveConfigVersion);
            ServerPlayNetworking.registerReceiver(handler, PACKET_CONFIG_SYNC_CLIENT, ModNetworking::receiveClientSettings);
            handler.sendPacket(createS2CPacket(PACKET_CONFIG_VERSION, PacketByteBufs.empty()));
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                server.execute(() -> Initializer.removeClientSettings(handler.player)));
    }

    private static void receiveConfigVersion(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        long version = buf.readLong();
        if (version == ModConfig.getVersion())
            handler.sendPacket(createS2CPacket(PACKET_CONFIG_SYNC, ModConfigServer.get().toByteBuf()));
        else
            server.execute(() -> handler.disconnect(new TranslatableText("text.simpleangelring.config_version_mismatch")));
    }

    private static void receiveClientSettings(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ModConfigClient clientConfig = new ModConfigClient();
        clientConfig.fromByteBuf(buf);
        server.execute(() -> Initializer.putClientSettings(player, clientConfig));
    }
}
