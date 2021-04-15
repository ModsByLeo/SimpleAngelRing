package adudecalledleo.simpleangelring.config;

// the only reason this class exists is because AutoConfig doesn't know what *not* to add a UI element for
final class ModConfigStorage {
    private ModConfigStorage() { }

    public static final long VERSION = 2;
    public static ModConfigServer remoteConfig;
}
