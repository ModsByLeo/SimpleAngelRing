package adudecalledleo.simpleangelring.duck;

import net.minecraft.server.network.ServerPlayerEntity;

public interface ServerPlayerEntityHooks {
    static boolean isNearBeacon(ServerPlayerEntity player) {
        return ((ServerPlayerEntityHooks) player).simpleangelring_isNearBeacon();
    }

    static void setNearBeacon(ServerPlayerEntity player) {
        ((ServerPlayerEntityHooks) player).simpleangelring_setNearBeacon();
    }

    boolean simpleangelring_isNearBeacon();
    void simpleangelring_setNearBeacon();
}
