package de.unhappycodings.quarry.client;

import de.unhappycodings.quarry.client.gui.QuarryConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return QuarryConfigScreen::new;
    }
}
