package adudecalledleo.simpleangelring.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;

@Config(name = "client")
public final class ModConfigClient implements ConfigData {
    public static ModConfigClient get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig().client;
    }

    static void applyConfig(ModConfigClient clientConfig) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            ModConfigClientEnv.syncClientSettings(clientConfig);
    }

    @Environment(EnvType.CLIENT)
    public PacketByteBuf toByteBuf() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(warnOnLowCharge);
        if (warnOnLowCharge)
            buf.writeVarInt(chargeWarnThreshold);
        return buf;
    }

    public void fromByteBuf(PacketByteBuf buf) {
        warnOnLowCharge = buf.readBoolean();
        if (warnOnLowCharge)
            chargeWarnThreshold = buf.readVarInt();
    }

    public boolean warnOnLowCharge = true;
    @ConfigEntry.BoundedDiscrete(max = 100)
    public int chargeWarnThreshold = 10;
}
