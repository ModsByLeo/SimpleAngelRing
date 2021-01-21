package adudecalledleo.simpleangelring.mixin;

import adudecalledleo.simpleangelring.duck.ServerPlayerEntityHooks;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerEntityHooks {
    @Unique private boolean nearBeacon;

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
    }
}
