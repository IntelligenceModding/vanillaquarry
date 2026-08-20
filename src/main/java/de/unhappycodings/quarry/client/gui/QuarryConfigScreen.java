package de.unhappycodings.quarry.client.gui;

import de.unhappycodings.quarry.client.config.ClientConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class QuarryConfigScreen extends Screen {
    private final Screen parent;

    public QuarryConfigScreen(Screen parent) {
        super(Component.translatable("config.quarry.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = width / 2 - 100;
        int y = height / 2 - 52;

        addRenderableWidget(Button.builder(label("config.quarry.darkmode", ClientConfig.enableQuarryDarkmode.get()), button -> {
            ClientConfig.setQuarryDarkmode(!ClientConfig.enableQuarryDarkmode.get());
            button.setMessage(label("config.quarry.darkmode", ClientConfig.enableQuarryDarkmode.get()));
        }).bounds(x, y, 200, 20).build());

        addRenderableWidget(Button.builder(label("config.quarry.holograph", ClientConfig.enableQuarryHolograph.get()), button -> {
            ClientConfig.setQuarryHolograph(!ClientConfig.enableQuarryHolograph.get());
            button.setMessage(label("config.quarry.holograph", ClientConfig.enableQuarryHolograph.get()));
        }).bounds(x, y + 24, 200, 20).build());

        addRenderableWidget(Button.builder(label("config.quarry.area_card_rendering", ClientConfig.enableAreaCardCornerRendering.get()), button -> {
            ClientConfig.setAreaCardCornerRendering(!ClientConfig.enableAreaCardCornerRendering.get());
            button.setMessage(label("config.quarry.area_card_rendering", ClientConfig.enableAreaCardCornerRendering.get()));
        }).bounds(x, y + 48, 200, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(x, y + 88, 200, 20)
                .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.text(font, title, width / 2 - font.width(title) / 2, height / 2 - 82, 0xFFFFFFFF, true);
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }

    private static Component label(String key, boolean value) {
        return Component.translatable(key).append(": ").append(Component.translatable(value ? "options.on" : "options.off"));
    }
}
