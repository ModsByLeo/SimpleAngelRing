package adudecalledleo.simpleangelring.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public final class StandardAngelRingItem extends AngelRingItem {
    public StandardAngelRingItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!user.isSneaking())
            return TypedActionResult.pass(stack);
        return toggleRing(world, user, stack);
    }
}
