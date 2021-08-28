package adudecalledleo.simpleangelring.mixin;

import adudecalledleo.simpleangelring.duck.ServerPlayerEntityHooks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin {
    @SuppressWarnings("rawtypes")
    @Inject(method = "applyPlayerEffects", at = @At(value = "INVOKE",
                                                    target = "Lnet/minecraft/entity/player/PlayerEntity;addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static void setNearBeacon(World world, BlockPos pos, int beaconLevel, StatusEffect primaryEffect,
                                      StatusEffect secondaryEffect, CallbackInfo ci, double d, int i, int j, Box box,
                                      List list, Iterator var11, PlayerEntity playerEntity2) {
        if (playerEntity2 instanceof ServerPlayerEntity)
            ServerPlayerEntityHooks.setNearBeacon((ServerPlayerEntity) playerEntity2);
    }
}
