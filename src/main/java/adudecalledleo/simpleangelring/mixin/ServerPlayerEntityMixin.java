package adudecalledleo.simpleangelring.mixin;

import adudecalledleo.simpleangelring.Initializer;
import adudecalledleo.simpleangelring.duck.ServerPlayerEntityHooks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static adudecalledleo.simpleangelring.ModItems.ANGEL_RING;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerEntityHooks {
    @Unique private boolean nearBeacon;

    @Shadow public abstract ServerWorld getServerWorld();

    private ServerPlayerEntityMixin() {
        //noinspection ConstantConditions
        super(null, null, 0, null);
        throw new RuntimeException("Mixin constructor invoked!");
    }

    @Override
    public boolean simpleangelring_isNearBeacon() {
        return nearBeacon;
    }

    @Override
    public void simpleangelring_setNearBeacon() {
        nearBeacon = true;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void resetNearBeacon(CallbackInfo ci) {
        nearBeacon = false;
        ItemStack cursorStack = inventory.getCursorStack();
        if (!cursorStack.isEmpty() && cursorStack.getItem() == ANGEL_RING)
            //noinspection ConstantConditions
            Initializer.addRingStack(getServerWorld(), ((ServerPlayerEntity) ((Object) this)), cursorStack);
    }
}
