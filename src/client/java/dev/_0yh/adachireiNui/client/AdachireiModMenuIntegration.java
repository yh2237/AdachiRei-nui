package dev._0yh.adachireiNui.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import dev._0yh.adachireiNui.config.AdachireiConfig;

public class AdachireiModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(AdachireiConfig.class, parent).get();
    }
}