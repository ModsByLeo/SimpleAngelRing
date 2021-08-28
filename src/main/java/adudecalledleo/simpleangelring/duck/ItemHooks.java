package adudecalledleo.simpleangelring.duck;

import net.minecraft.item.Item;

public interface ItemHooks {
    static void setMaxDamage(Item item, int maxDamage) {
        ((ItemHooks) item).simpleangelring_setMaxDamage(maxDamage);
    }

    void simpleangelring_setMaxDamage(int maxDamage);
}
