package dev._0yh.adachireiNui.config;

import dev._0yh.adachireiNui.AdachireiNui;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public class AdachireiConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue enableCustomSplash;

    static {
        BUILDER.push("general");
        enableCustomSplash = BUILDER
                .comment("Enable custom splash text from API")
                .define("enableCustomSplash", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void init(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, SPEC);
    }

    public static boolean getEnableCustomSplash() {
        if (!SPEC.isLoaded()) return true;
        return enableCustomSplash.get();
    }

    public static void setEnableCustomSplash(boolean value) {
        enableCustomSplash.set(value);
        SPEC.save();
    }

    public static ConfigValues get() {
        return new ConfigValues();
    }

    public static class ConfigValues {
        public final boolean enableCustomSplash = getEnableCustomSplash();
    }
}
