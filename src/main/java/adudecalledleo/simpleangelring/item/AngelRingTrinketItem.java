package adudecalledleo.simpleangelring.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketItem;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public final class AngelRingTrinketItem extends AngelRingItem implements Trinket {
    public AngelRingTrinketItem(Settings settings) {
        super(settings);
        //DispenserBlock.registerBehavior(this, TrinketItem.TRINKET_DISPENSER_BEHAVIOR);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSneaking())
            return toggleRing(world, user, stack);
        else {
            if (TrinketItem.equipItem(user, stack))
                return TypedActionResult.success(stack, world.isClient());
            return super.use(world, user, hand);
        }
    }

    /*
    @Override
    public void tick(PlayerEntity player, ItemStack stack) {
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            Initializer.addRingStack(serverPlayer.getServerWorld(), serverPlayer, stack);
        }
    }*/
}
