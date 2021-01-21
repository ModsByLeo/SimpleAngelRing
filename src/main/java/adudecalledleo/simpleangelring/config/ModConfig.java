package adudecalledleo.simpleangelring.config;

import adudecalledleo.simpleangelring.Initializer;
import adudecalledleo.simpleangelring.mixin.ItemAccessor;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;

import static adudecalledleo.simpleangelring.config.ModConfigStorage.remoteConfig;

@Config(name = Initializer.MOD_ID)
public final class ModConfig implements ConfigData {
    public static void register() {
        ConfigHolder<ModConfig> holder = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        holder.registerLoadListener((manager, newData) -> {
            if (remoteConfig == null)
                applyConfig(newData);
            return ActionResult.PASS;
        });
        holder.registerSaveListener((manager, newData) -> {
            if (remoteConfig == null)
                applyConfig(newData);
            return ActionResult.PASS;
        });
    }

    public static long getVersion() {
        return ModConfigStorage.VERSION;
    }

    public static ModConfig get() {
        if (remoteConfig != null)
            return remoteConfig;
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Environment(EnvType.CLIENT)
    public static void setRemoteConfig(ModConfig remoteConfig) {
        ModConfigStorage.remoteConfig = remoteConfig;
        applyConfig(get());
    }

    private static void applyConfig(ModConfig config) {
        //noinspection ConstantConditions
        ((ItemAccessor) (Object) Initializer.ANGEL_RING).setMaxDamage(config.chargeEnabled ? config.chargeMax : 0);
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
