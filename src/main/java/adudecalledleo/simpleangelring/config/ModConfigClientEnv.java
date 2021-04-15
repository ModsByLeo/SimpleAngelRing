package adudecalledleo.simpleangelring.config;

import adudecalledleo.simpleangelring.ModNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
final class ModConfigClientEnv {
    private ModConfigClientEnv() { }

    public static void syncClientSettings(ModConfigClient clientConfig) {
        System.out.println("Sending client settings");
        ClientPlayNetworking.send(ModNetworking.PACKET_CONFIG_SYNC_CLIENT, clientConfig.toByteBuf());
    }
}
