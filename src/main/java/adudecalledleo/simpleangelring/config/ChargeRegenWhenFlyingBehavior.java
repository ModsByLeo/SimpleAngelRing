package adudecalledleo.simpleangelring.config;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum ChargeRegenWhenFlyingBehavior {
    NEVER("never"), ALWAYS("always"), WHEN_NEAR_BEACON("whenNearBeacon");

    private final String key;

    ChargeRegenWhenFlyingBehavior(String key) {
        this.key = key;
    }

    public Text toText() {
        return new TranslatableText("text.autoconfig.simpleangelring.option.server.chargeRegenWhenFlying." + key);
    }
}
