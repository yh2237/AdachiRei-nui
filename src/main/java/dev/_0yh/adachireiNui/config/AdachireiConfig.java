package dev._0yh.adachireiNui.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "adachirei-nui")
public class AdachireiConfig implements ConfigData {
    public boolean enableCustomSplash = true;

    public static AdachireiConfig get() {
        return AutoConfig.getConfigHolder(AdachireiConfig.class).getConfig();
    }
}