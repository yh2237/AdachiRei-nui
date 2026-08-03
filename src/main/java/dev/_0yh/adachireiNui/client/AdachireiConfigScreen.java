package dev._0yh.adachireiNui.client;

import dev._0yh.adachireiNui.config.AdachireiConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AdachireiConfigScreen extends Screen {
    private final Screen parent;

    public AdachireiConfigScreen(Screen parent) {
        super(Component.translatable("text.autoconfig.adachirei-nui.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addRenderableWidget(CycleButton.onOffBuilder(AdachireiConfig.getEnableCustomSplash())
                .create(width / 2 - 100, height / 2 - 24, 200, 20,
                        Component.translatable("text.autoconfig.adachirei-nui.option.enableCustomSplash"),
                        (button, value) -> AdachireiConfig.setEnableCustomSplash(value)));
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(width / 2 - 100, height / 2 + 12, 200, 20)
                .build());
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
