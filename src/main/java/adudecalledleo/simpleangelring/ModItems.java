package adudecalledleo.simpleangelring;

import adudecalledleo.simpleangelring.item.AngelRingItem;
import adudecalledleo.simpleangelring.item.AngelRingTrinketItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static adudecalledleo.simpleangelring.Initializer.TRINKETS_LOADED;
import static adudecalledleo.simpleangelring.Initializer.id;

public final class ModItems {
    private ModItems() { }

    public static final AngelRingItem ANGEL_RING = createAngelRing();

    private static AngelRingItem createAngelRing() {
        FabricItemSettings settings = new FabricItemSettings()
                .maxCount(1)
                .fireproof() // this thing is (probably) gonna be expensive!
                .rarity(Rarity.EPIC)
                .group(ItemGroup.TRANSPORTATION);
        if (TRINKETS_LOADED)
            return new AngelRingTrinketItem(settings);
        else
            return new AngelRingItem(settings);
    }

    public static void register() {
        if (TRINKETS_LOADED)
            TrinketsCompat.registerRingTrinketSlots();
        Registry.register(Registry.ITEM, id("angel_ring"), ANGEL_RING);
    }
}
