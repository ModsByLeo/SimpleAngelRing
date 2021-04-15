package adudecalledleo.simpleangelring.item;

import adudecalledleo.simpleangelring.Initializer;
import adudecalledleo.simpleangelring.ModSoundEvents;
import adudecalledleo.simpleangelring.config.ModConfigServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
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

import static adudecalledleo.simpleangelring.ModItems.ANGEL_RING;

public class AngelRingItem extends Item {
    public AngelRingItem(Settings settings) {
        super(settings);
    }

    public static boolean isRingEnabled(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != ANGEL_RING)
            return false;
        boolean ringEnabled = true;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("enabled", NbtType.BYTE))
            ringEnabled = tag.getBoolean("enabled");
        return ringEnabled;
    }

    public static int getRingRegenTicks(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != ANGEL_RING)
            return 0;
        int regenTicks = 0;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("regenTicks", NbtType.NUMBER))
            regenTicks = tag.getInt("regenTicks");
        return regenTicks;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!user.isSneaking())
            return TypedActionResult.pass(stack);
        return toggleRing(world, user, stack);
    }

    protected TypedActionResult<ItemStack> toggleRing(World world, PlayerEntity user, ItemStack stack) {
        boolean wasRingEnabled = isRingEnabled(stack);
        if (world.isClient())
            world.playSound(user, user.getX(), user.getY(), user.getZ(),
                    wasRingEnabled ? ModSoundEvents.ANGEL_RING_DISABLED
                            : ModSoundEvents.ANGEL_RING_ENABLED,
                    SoundCategory.PLAYERS, 1, wasRingEnabled ? 1.2F : 1);
        else {
            stack.getOrCreateTag().putBoolean("enabled", !wasRingEnabled);
            user.sendMessage(new TranslatableText(getTranslationKey() + (wasRingEnabled ? ".disabled" : ".enabled")),
                    true);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof ServerPlayerEntity)
            Initializer.addRingStack((ServerWorld) world, (ServerPlayerEntity) entity, stack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return isRingEnabled(stack);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        for (int i = 0; i < 3; i++) {
            String key = getTranslationKey() + ".tooltip[" + i + "]";
            if (i == 1 && ModConfigServer.get().chargeEnabled)
                key += ".alt";
            tooltip.add(new TranslatableText(key)
                    .styled(style -> style.withColor(Formatting.DARK_GRAY).withItalic(true)));
        }
        if (!isRingEnabled(stack))
            tooltip.add(new TranslatableText(getTranslationKey() + ".tooltip.disabled")
                    .styled(style -> style.withColor(Formatting.RED).withBold(true)));
    }
}
