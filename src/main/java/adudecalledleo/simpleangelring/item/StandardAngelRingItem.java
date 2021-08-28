package adudecalledleo.simpleangelring.item;

import adudecalledleo.simpleangelring.Initializer;
import adudecalledleo.simpleangelring.ModSoundEvents;
import adudecalledleo.simpleangelring.config.ModConfigServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof ServerPlayerEntity)
            Initializer.addRingStack((ServerWorld) world, (ServerPlayerEntity) entity, stack);
    }
}
