package adudecalledleo.simpleangelring;

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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
        ModSoundEvents.register();
        Registry.register(Registry.ITEM, id("angel_ring"), ANGEL_RING);
        ServerTickEvents.START_WORLD_TICK.register(this::onStartWorldTick);
        LOGGER.info("Angel Rings: So easy, a Spider could do it. [Simple Angel Ring has initialized!]");
    }

    private void onStartWorldTick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!player.interactionManager.isSurvivalLike())
                continue;
            ItemStack ringStack = player.inventory.getCursorStack();
            if (ringStack.getItem() != ANGEL_RING)
                ringStack = player.inventory.offHand.get(0);
            if (ringStack.getItem() != ANGEL_RING) {
                ringStack = ItemStack.EMPTY;
                for (ItemStack stack : player.inventory.main) {
                    if (stack.getItem() == ANGEL_RING) {
                        ringStack = stack;
                        break;
                    }
                }
            }
            if (AngelRingItem.isRingEnabled(ringStack))
                Pal.grantAbility(player, VanillaAbilities.ALLOW_FLYING, ANGEL_RING_SOURCE);
            else
                Pal.revokeAbility(player, VanillaAbilities.ALLOW_FLYING, ANGEL_RING_SOURCE);
        }
    }
}
