package de.unhappycodings.quarry.common.config;

public class ConfigValue<T> {
    private T value;

    public ConfigValue(T defaultValue) {
        this.value = defaultValue;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void save() {
    }
}
