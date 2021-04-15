package adudecalledleo.simpleangelring.client;

import adudecalledleo.simpleangelring.config.ModConfig;
import adudecalledleo.simpleangelring.config.ModConfigClient;
import adudecalledleo.simpleangelring.config.ModConfigServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import java.util.concurrent.atomic.AtomicBoolean;

import static adudecalledleo.simpleangelring.ModNetworking.*;

@Environment(EnvType.CLIENT)
public final class ModNetworkingClient {
    private ModNetworkingClient() { }

    private static final AtomicBoolean SENT_FIRST_CLIENT_SYNC = new AtomicBoolean(false);

    public static void register() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> client.execute(() -> {
            SENT_FIRST_CLIENT_SYNC.set(false);
        }));
        ClientPlayNetworking.registerGlobalReceiver(PACKET_CONFIG_VERSION, (client, handler, buf, responseSender) -> {
            PacketByteBuf responseBuf = PacketByteBufs.create();
            responseBuf.writeLong(ModConfig.getVersion());
            responseSender.sendPacket(PACKET_CONFIG_VERSION, responseBuf);
        });
        ClientPlayNetworking.registerGlobalReceiver(PACKET_CONFIG_SYNC, (client, handler, buf, responseSender) -> {
            ClientInitializer.LOGGER.info("Received config from server");
            ModConfigServer remoteConfig = new ModConfigServer();
            remoteConfig.fromByteBuf(buf);
            client.execute(() -> ModConfigServer.setRemoteConfig(remoteConfig));
            if (SENT_FIRST_CLIENT_SYNC.compareAndSet(false, true))
                responseSender.sendPacket(PACKET_CONFIG_SYNC_CLIENT, ModConfigClient.get().toByteBuf());
        });
    }
}
