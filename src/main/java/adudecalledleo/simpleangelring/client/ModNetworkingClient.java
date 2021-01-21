package adudecalledleo.simpleangelring.client;

import adudecalledleo.simpleangelring.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import static adudecalledleo.simpleangelring.ModNetworking.PACKET_CONFIG_SYNC;
import static adudecalledleo.simpleangelring.ModNetworking.PACKET_CONFIG_VERSION;

@Environment(EnvType.CLIENT)
public final class ModNetworkingClient {
    private ModNetworkingClient() { }

    public static void register() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            client.execute(() -> {
                if (client.isIntegratedServerRunning())
                    ModConfig.setRemoteConfig(null);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(PACKET_CONFIG_VERSION, (client, handler, buf, responseSender) -> {
            PacketByteBuf responseBuf = PacketByteBufs.create();
            responseBuf.writeLong(ModConfig.getVersion());
            responseSender.sendPacket(PACKET_CONFIG_VERSION, responseBuf);
        });
        ClientPlayNetworking.registerGlobalReceiver(PACKET_CONFIG_SYNC, (client, handler, buf, responseSender) -> {
            ModConfig remoteConfig;
            remoteConfig = new ModConfig();
            remoteConfig.fromByteBuf(buf);
            client.execute(() -> ModConfig.setRemoteConfig(remoteConfig));
        });
    }
}
