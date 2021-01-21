package adudecalledleo.simpleangelring;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static adudecalledleo.simpleangelring.Initializer.id;

public final class ModSoundEvents {
    private ModSoundEvents() { }

    public static final Identifier ANGEL_RING_ENABLED_ID = id("angel_ring.enabled");
    public static final Identifier ANGEL_RING_DISABLED_ID = id("angel_ring.disabled");

    public static final SoundEvent ANGEL_RING_ENABLED = new SoundEvent(ANGEL_RING_ENABLED_ID);
    public static final SoundEvent ANGEL_RING_DISABLED = new SoundEvent(ANGEL_RING_DISABLED_ID);

    public static void register() {
        Registry.register(Registry.SOUND_EVENT, ANGEL_RING_ENABLED_ID, ANGEL_RING_ENABLED);
        Registry.register(Registry.SOUND_EVENT, ANGEL_RING_DISABLED_ID, ANGEL_RING_DISABLED);
    }
}
