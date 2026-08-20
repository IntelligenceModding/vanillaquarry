package de.unhappycodings.quarry.client.config;

import de.unhappycodings.quarry.common.config.ConfigValue;

public class ClientConfig {
    public static final ConfigValue<Boolean> enableQuarryDarkmode = new ConfigValue<>(false);
    public static final ConfigValue<Boolean> enableQuarryHolograph = new ConfigValue<>(false);
    public static final ConfigValue<Boolean> enableAreaCardCornerRendering = new ConfigValue<>(true);

    public static void setQuarryDarkmode(boolean enabled) {
        setAndSave(enableQuarryDarkmode, enabled);
    }

    public static void setQuarryHolograph(boolean enabled) {
        setAndSave(enableQuarryHolograph, enabled);
    }

    public static void setAreaCardCornerRendering(boolean enabled) {
        setAndSave(enableAreaCardCornerRendering, enabled);
    }

    private static <T> void setAndSave(ConfigValue<T> value, T state) {
        value.set(state);
        value.save();
    }
}
