package dev._0yh.adachireiNui.config;

import dev._0yh.adachireiNui.AdachireiNui;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class AdachireiConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue enableCustomSplash;

    static {
        BUILDER.push("general");
        enableCustomSplash = BUILDER
                .comment("Enable custom splash text from API")
                .define("enableCustomSplash", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC);
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
