package adudecalledleo.simpleangelring.client;

import adudecalledleo.simpleangelring.Initializer;
import adudecalledleo.simpleangelring.config.ChargeRegenWhenFlyingBehavior;
import adudecalledleo.simpleangelring.config.ModConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.getUnsafely;
import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.setUnsafely;

@Environment(EnvType.CLIENT)
public final class ClientInitializer implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(Initializer.MOD_NAME + "|Client");
    private static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();

    @Override
    public void onInitializeClient() {
        ModNetworkingClient.register();
        AutoConfig.getGuiRegistry(ModConfig.class).registerTypeProvider((i13n, field, config, defaults, registry) ->
                Collections.singletonList(
                    ENTRY_BUILDER.startSelector(
                            new TranslatableText(i13n),
                            ChargeRegenWhenFlyingBehavior.values(),
                            getUnsafely(field, config, getUnsafely(field, defaults))
                    )
                            .setDefaultValue(() -> getUnsafely(field, defaults))
                            .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                            .setNameProvider(ChargeRegenWhenFlyingBehavior::toText)
                            .build()
        ), ChargeRegenWhenFlyingBehavior.class);
        LOGGER.info("Simple Angel Ring has initialized on the client!");
    }
}
