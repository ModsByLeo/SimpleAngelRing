package adudecalledleo.simpleangelring;

import adudecalledleo.simpleangelring.config.ModConfig;
import adudecalledleo.simpleangelring.config.ModConfigClient;
import adudecalledleo.simpleangelring.config.ModConfigServer;
import adudecalledleo.simpleangelring.duck.ServerPlayerEntityHooks;
import adudecalledleo.simpleangelring.item.AngelRingItem;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import java.util.UUID;

import static adudecalledleo.simpleangelring.ModItems.ANGEL_RING;

public final class Initializer implements ModInitializer {
    public static final String MOD_ID = "simpleangelring";
    public static final String MOD_NAME = "Simple Angel Ring";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static final AbilitySource ANGEL_RING_SOURCE = Pal.getAbilitySource(MOD_ID, "angel_ring");

    public static final boolean TRINKETS_LOADED = FabricLoader.getInstance().isModLoaded("trinkets");

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    private static final Object2ReferenceOpenHashMap<UUID, ModConfigClient> CLIENT_SETTINGS =
            new Object2ReferenceOpenHashMap<>();
    private static final ObjectOpenHashSet<UUID> WARNED_PLAYERS = new ObjectOpenHashSet<>();
    private static ModConfigServer configToResync;

    @Override
    public void onInitialize() {
        ModItems.register();
        ModConfig.register();
        ModNetworking.register();
        ModSoundEvents.register();
        ServerTickEvents.END_WORLD_TICK.register(this::onEndWorldTick);
        ServerTickEvents.END_SERVER_TICK.register(this::onEndServerTick);
        LOGGER.info("Angel Rings: So easy, a Spider could do it. [Simple Angel Ring has initialized!]");
    }

    public static void putClientSettings(ServerPlayerEntity player, ModConfigClient clientConfig) {
        LOGGER.info("Updating client settings for player " + player.getEntityName());
        UUID uuid = player.getUuid();
        CLIENT_SETTINGS.put(uuid, clientConfig);
        WARNED_PLAYERS.remove(uuid);
    }

    public static void removeClientSettings(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        CLIENT_SETTINGS.remove(uuid);
        WARNED_PLAYERS.remove(uuid);
    }

    public static ModConfigClient getClientSettings(ServerPlayerEntity player) {
        return CLIENT_SETTINGS.get(player.getUuid());
    }

    public static void resyncConfig(ModConfigServer config) {
        configToResync = config;
    }

    private static final Hand[] HANDS = Hand.values();
    public static ItemStack getRingStack(ServerPlayerEntity player) {
        if (!player.interactionManager.isSurvivalLike())
            return ItemStack.EMPTY;

        ItemStack cursorStack = player.currentScreenHandler.getCursorStack();
        if (!cursorStack.isEmpty() && cursorStack.isOf(ANGEL_RING))
            return cursorStack;

        for (Hand hand : HANDS) {
            ItemStack stack = player.getStackInHand(hand);
            if (!stack.isEmpty() && stack.isOf(ANGEL_RING))
                return stack;
        }

        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty() && stack.isOf(ANGEL_RING))
                return stack;
        }

        if (TRINKETS_LOADED)
            return TrinketsCompat.getRingTrinket(player);

        return ItemStack.EMPTY;
    }

    private void onEndWorldTick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            ItemStack ringStack = getRingStack(player);
            if (ringStack.isEmpty()) {
                Pal.revokeAbility(player, VanillaAbilities.ALLOW_FLYING, ANGEL_RING_SOURCE);
                continue;
            }
            if (updateRingStack(player, ringStack))
                Pal.grantAbility(player, VanillaAbilities.ALLOW_FLYING, ANGEL_RING_SOURCE);
            else
                Pal.revokeAbility(player, VanillaAbilities.ALLOW_FLYING, ANGEL_RING_SOURCE);
        }
    }

    private void onEndServerTick(MinecraftServer server) {
        if (configToResync != null) {
            PacketByteBuf buf = configToResync.toByteBuf();
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList())
                ServerPlayNetworking.send(player, ModNetworking.PACKET_CONFIG_SYNC, buf);
            configToResync = null;
        }
    }

    private static boolean updateRingStack(ServerPlayerEntity player, ItemStack ringStack) {
        if (ringStack.isEmpty() || !ringStack.isOf(ANGEL_RING))
            return false;
        final ModConfigServer config = ModConfigServer.get();
        if (!config.chargeEnabled)
            return AngelRingItem.isRingEnabled(ringStack);
        final boolean isNearBeacon = ServerPlayerEntityHooks.isNearBeacon(player);
        final boolean isFlying = VanillaAbilities.FLYING.isEnabledFor(player);
        boolean doRegen = true;
        int maxRegenTicks = config.chargeRegenTicks;
        if (config.chargeRegenBoostedByBeacon && isNearBeacon)
            maxRegenTicks = config.chargeRegenTicksBoosted;
        switch (config.chargeRegenWhenFlying) {
        case WHEN_NEAR_BEACON:
            if (isNearBeacon)
                break;
        case NEVER:
            doRegen = !isFlying;
        case ALWAYS:
            break;
        }
        int ringCharge = ringStack.getDamage();
        int ringRegenTicks = AngelRingItem.getRingRegenTicks(ringStack);
        if (doRegen && ringCharge > 0) {
            ringRegenTicks++;
            if (ringRegenTicks >= maxRegenTicks) {
                ringRegenTicks = 0;
                ringCharge--;
            }
        } else if (isFlying && ringCharge < ringStack.getMaxDamage())
            ringCharge++;
        ringStack.setDamage(ringCharge);
        ringStack.getOrCreateNbt().putInt("regenTicks", ringRegenTicks);
        ModConfigClient clientSettings = getClientSettings(player);
        if (clientSettings != null) {
            if (clientSettings.warnOnLowCharge) {
                int percent = (int) (((ringStack.getMaxDamage() - ringCharge) / ((float) ringStack.getMaxDamage())) * 100);
                if (percent <= clientSettings.chargeWarnThreshold) {
                    if (WARNED_PLAYERS.add(player.getUuid()))
                        sendLowChargeWarning(player);
                } else
                    WARNED_PLAYERS.remove(player.getUuid());
            }
        }
        return ringCharge < ringStack.getMaxDamage() && AngelRingItem.isRingEnabled(ringStack);
    }

    private static void sendLowChargeWarning(ServerPlayerEntity player) {
        ServerPlayNetworkHandler handler = player.networkHandler;
        handler.sendPacket(new TitleS2CPacket(new TranslatableText("text.simpleangelring.low_charge")
                .styled(style -> style.withColor(Formatting.RED).withBold(true))));
        handler.sendPacket(new SubtitleS2CPacket(new TranslatableText("text.simpleangelring.low_charge.subtitle")
                .styled(style -> style.withColor(Formatting.GRAY))));
        handler.sendPacket(new TitleFadeS2CPacket(5, 100, 5));
    }
}
