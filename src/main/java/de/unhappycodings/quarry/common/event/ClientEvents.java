package de.unhappycodings.quarry.common.event;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.blockentity.renderer.QuarryEntityRenderer;
import de.unhappycodings.quarry.common.container.AreaCardScreen;
import de.unhappycodings.quarry.common.container.QuarryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Quarry.MOD_ID)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(RegisterMenuScreensEvent event) {
        event.register(Quarry.QUARRY_CONTAINER.get(), QuarryScreen::new);
        event.register(Quarry.AREA_CARD_CONTAINER.get(), AreaCardScreen::new);
    }

    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Quarry.QUARRY_ENTITY.get(), QuarryEntityRenderer::new);
        event.registerBlockEntityRenderer(Quarry.FE_QUARRY_ENTITY.get(), QuarryEntityRenderer::new);
    }

}
