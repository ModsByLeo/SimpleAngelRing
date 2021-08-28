package adudecalledleo.simpleangelring.item;

import adudecalledleo.simpleangelring.ModSoundEvents;
import adudecalledleo.simpleangelring.config.ModConfigServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

import static adudecalledleo.simpleangelring.ModItems.ANGEL_RING;

public abstract class AngelRingItem extends Item {
    public AngelRingItem(Settings settings) {
        super(settings);
    }

    public static boolean isRingEnabled(ItemStack stack) {
        if (stack.isEmpty() || !stack.isOf(ANGEL_RING))
            return false;
        boolean ringEnabled = true;
        NbtCompound tag = stack.getNbt();
        if (tag != null && tag.contains("enabled", NbtType.BYTE))
            ringEnabled = tag.getBoolean("enabled");
        return ringEnabled;
    }

    public static int getRingRegenTicks(ItemStack stack) {
        if (stack.isEmpty() || !stack.isOf(ANGEL_RING))
            return 0;
        int regenTicks = 0;
        NbtCompound tag = stack.getNbt();
        if (tag != null && tag.contains("regenTicks", NbtType.NUMBER))
            regenTicks = tag.getInt("regenTicks");
        return regenTicks;
    }

    protected TypedActionResult<ItemStack> toggleRing(World world, PlayerEntity user, ItemStack stack) {
        boolean wasRingEnabled = isRingEnabled(stack);
        if (world.isClient())
            world.playSound(user, user.getX(), user.getY(), user.getZ(),
                    wasRingEnabled ? ModSoundEvents.ANGEL_RING_DISABLED
                            : ModSoundEvents.ANGEL_RING_ENABLED,
                    SoundCategory.PLAYERS, 1, wasRingEnabled ? 1.2F : 1);
        else {
            stack.getOrCreateNbt().putBoolean("enabled", !wasRingEnabled);
            user.sendMessage(new TranslatableText(getTranslationKey() + (wasRingEnabled ? ".disabled" : ".enabled")),
                    true);
        }
        return TypedActionResult.success(stack, world.isClient());
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
