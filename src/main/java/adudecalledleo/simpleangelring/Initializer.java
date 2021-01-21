package adudecalledleo.simpleangelring;

import adudecalledleo.simpleangelring.config.ModConfig;
import adudecalledleo.simpleangelring.duck.ServerPlayerEntityHooks;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Initializer implements ModInitializer {
    public static final String MOD_ID = "simpleangelring";
    public static final String MOD_NAME = "Simple Angel Ring";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static final AngelRingItem ANGEL_RING = new AngelRingItem(new Item.Settings()
            .maxCount(1)
            .fireproof() // this thing is (probably) gonna be expensive!
            .rarity(Rarity.EPIC)
            .group(ItemGroup.TRANSPORTATION));

    public static final AbilitySource ANGEL_RING_SOURCE = Pal.getAbilitySource(MOD_ID, "angel_ring");

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModConfig.register();
        ModNetworking.register();
        ModSoundEvents.register();
        Registry.register(Registry.ITEM, id("angel_ring"), ANGEL_RING);
        ServerTickEvents.END_WORLD_TICK.register(this::onEndWorldTick);
        LOGGER.info("Angel Rings: So easy, a Spider could do it. [Simple Angel Ring has initialized!]");
    }

    private void onEndWorldTick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!player.interactionManager.isSurvivalLike())
                continue;
            ItemStack ringStack = getRingStack(player.inventory);
            if (updateRingStack(player, ringStack))
                Pal.grantAbility(player, VanillaAbilities.ALLOW_FLYING, ANGEL_RING_SOURCE);
            else
                Pal.revokeAbility(player, VanillaAbilities.ALLOW_FLYING, ANGEL_RING_SOURCE);
        }
    }

    private static ItemStack getRingStack(PlayerInventory playerInventory) {
        ItemStack ringStack = playerInventory.getCursorStack();
        if (ringStack.getItem() != ANGEL_RING)
            ringStack = playerInventory.offHand.get(0);
        if (ringStack.getItem() != ANGEL_RING) {
            ringStack = ItemStack.EMPTY;
            for (ItemStack stack : playerInventory.main) {
                if (stack.getItem() == ANGEL_RING) {
                    ringStack = stack;
                    break;
                }
            }
        }
        return ringStack;
    }

    private static boolean updateRingStack(ServerPlayerEntity player, ItemStack ringStack) {
        if (ringStack.isEmpty() || ringStack.getItem() != ANGEL_RING)
            return false;
        final ModConfig config = ModConfig.get();
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
        ringStack.getOrCreateTag().putInt("regenTicks", ringRegenTicks);
        return ringCharge < ringStack.getMaxDamage() && AngelRingItem.isRingEnabled(ringStack);
    }
}
