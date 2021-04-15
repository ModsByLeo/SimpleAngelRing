package adudecalledleo.simpleangelring.config;

import adudecalledleo.simpleangelring.Initializer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.minecraft.util.ActionResult;

@Config(name = Initializer.MOD_ID)
public final class ModConfig extends PartitioningSerializer.GlobalData {
    public static long getVersion() {
        return ModConfigStorage.VERSION;
    }

    public static void register() {
        ConfigHolder<ModConfig> holder = AutoConfig.register(ModConfig.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        holder.registerLoadListener((manager, newData) -> {
            ModConfigClient.applyConfig(newData.client);
            ModConfigServer.applyConfig(newData.server);
            Initializer.resyncConfig(newData.server);
            return ActionResult.PASS;
        });
        holder.registerSaveListener((manager, newData) -> {
            ModConfigClient.applyConfig(newData.client);
            ModConfigServer.applyConfig(newData.server);
            Initializer.resyncConfig(newData.server);
            return ActionResult.PASS;
        });
    }

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    public ModConfigClient client;

    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    public ModConfigServer server;
}
