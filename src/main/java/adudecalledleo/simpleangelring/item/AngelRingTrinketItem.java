package adudecalledleo.simpleangelring.item;

import adudecalledleo.simpleangelring.Initializer;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public final class AngelRingTrinketItem extends AngelRingItem implements Trinket {
    public AngelRingTrinketItem(Settings settings) {
        super(settings);
        DispenserBlock.registerBehavior(this, TrinketItem.TRINKET_DISPENSER_BEHAVIOR);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSneaking())
            return toggleRing(world, user, stack);
        else
            return Trinket.equipTrinket(user, hand);
    }

    @Override
    public boolean canWearInSlot(String group, String slot) {
        return Slots.RING.equals(slot);
    }

    @Override
    public void tick(PlayerEntity player, ItemStack stack) {
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            Initializer.addRingStack(serverPlayer.getServerWorld(), serverPlayer, stack);
        }
    }
}
