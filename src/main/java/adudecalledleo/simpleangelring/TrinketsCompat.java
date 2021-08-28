package adudecalledleo.simpleangelring;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;

import java.util.List;

import static adudecalledleo.simpleangelring.ModItems.ANGEL_RING;

public final class TrinketsCompat {
    private TrinketsCompat() { }

    public static ItemStack getRingTrinket(ServerPlayerEntity player) {
        var compMaybe = TrinketsApi.getTrinketComponent(player);
        if (compMaybe.isPresent()) {
            var comp = compMaybe.get();
            List<Pair<SlotReference, ItemStack>> matchingStacks = comp.getEquipped(stack -> stack.isOf(ANGEL_RING));
            if (!matchingStacks.isEmpty())
                return matchingStacks.get(0).getRight();
        }
        return ItemStack.EMPTY;
    }
}
