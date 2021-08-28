package adudecalledleo.simpleangelring.mixin;

import adudecalledleo.simpleangelring.duck.ItemHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.item.Item;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemHooks {
    @Shadow @Final @Mutable
    private int maxDamage;

    @Override
    public void simpleangelring_setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }
}
