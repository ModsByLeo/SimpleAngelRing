package adudecalledleo.simpleangelring;

import adudecalledleo.simpleangelring.item.AngelRingItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static adudecalledleo.simpleangelring.Initializer.id;

public final class ModItems {
    private ModItems() { }

    public static final AngelRingItem ANGEL_RING = AngelRingItem.create(new FabricItemSettings()
            .maxCount(1)
            .fireproof() // this thing is (probably) gonna be expensive!
            .rarity(Rarity.EPIC)
            .group(ItemGroup.TRANSPORTATION));

    public static void register() {
        Registry.register(Registry.ITEM, id("angel_ring"), ANGEL_RING);
    }
}
