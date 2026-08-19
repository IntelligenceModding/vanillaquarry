package de.unhappycodings.quarry;

import de.unhappycodings.quarry.common.event.AreaCardLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Quarry.MOD_ID, dist = Dist.CLIENT)
public class QuarryClient {

    public QuarryClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(AreaCardLevelRenderer::renderSquareAboveWorldCentre);
    }

}
