package adudecalledleo.simpleangelring.config;

// the only reason this class exists is because AutoConfig doesn't know what *not* to add a UI element for
final class ModConfigStorage {
    private ModConfigStorage() { }

    public static final long VERSION = 1;
    public static ModConfig remoteConfig;
}
