package adudecalledleo.simpleangelring;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketSlots;
import net.minecraft.util.Identifier;

final class TrinketsCompat {
    private TrinketsCompat() { }

    public static void registerRingTrinketSlots() {
        Identifier texId = new Identifier("trinkets", "textures/item/empty_trinket_slot_ring.png");
        TrinketSlots.addSlot(SlotGroups.HAND, Slots.RING, texId);
        TrinketSlots.addSlot(SlotGroups.OFFHAND, Slots.RING, texId);
    }
}
