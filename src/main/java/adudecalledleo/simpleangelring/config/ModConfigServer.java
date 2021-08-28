package adudecalledleo.simpleangelring.config;

import adudecalledleo.simpleangelring.ModItems;
import adudecalledleo.simpleangelring.duck.ItemHooks;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import static adudecalledleo.simpleangelring.config.ModConfigStorage.remoteConfig;

@Config(name = "server")
public final class ModConfigServer implements ConfigData {
    public static ModConfigServer get() {
        if (remoteConfig != null)
            return remoteConfig;
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig().server;
    }

    @Environment(EnvType.CLIENT)
    public static void setRemoteConfig(ModConfigServer remoteConfig) {
        ModConfigStorage.remoteConfig = remoteConfig;
        applyConfig(get());
    }

    static void applyConfig(ModConfigServer config) {
        if (remoteConfig == null || config == remoteConfig)
            ItemHooks.setMaxDamage(ModItems.ANGEL_RING, config.chargeEnabled ? config.chargeMax : 0);
    }

    public PacketByteBuf toByteBuf() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(chargeEnabled);
        if (chargeEnabled) {
            buf.writeVarInt(chargeMax);
            buf.writeVarInt(chargeRegenTicks);
            buf.writeEnumConstant(chargeRegenWhenFlying);
            buf.writeBoolean(chargeRegenBoostedByBeacon);
            if (chargeRegenBoostedByBeacon)
                buf.writeVarInt(chargeRegenTicksBoosted);
        }
        return buf;
    }

    @Environment(EnvType.CLIENT)
    public void fromByteBuf(PacketByteBuf buf) {
        chargeEnabled = buf.readBoolean();
        if (chargeEnabled) {
            chargeMax = buf.readVarInt();
            chargeRegenTicks = buf.readVarInt();
            chargeRegenWhenFlying = buf.readEnumConstant(ChargeRegenWhenFlyingBehavior.class);
            chargeRegenBoostedByBeacon = buf.readBoolean();
            if (chargeRegenBoostedByBeacon)
                chargeRegenTicksBoosted = buf.readVarInt();
        }
    }

    public boolean chargeEnabled = true;
    public int chargeMax = 20 * 120;
    public int chargeRegenTicks = 4;
    public ChargeRegenWhenFlyingBehavior chargeRegenWhenFlying = ChargeRegenWhenFlyingBehavior.NEVER;
    public boolean chargeRegenBoostedByBeacon = true;
    public int chargeRegenTicksBoosted = 2;
}
