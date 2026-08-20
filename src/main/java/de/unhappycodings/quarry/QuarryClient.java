package de.unhappycodings.quarry;

import de.unhappycodings.quarry.common.blockentity.renderer.QuarryEntityRenderer;
import de.unhappycodings.quarry.common.container.AreaCardScreen;
import de.unhappycodings.quarry.common.container.QuarryScreen;
import de.unhappycodings.quarry.common.event.AreaCardLevelRenderer;
import de.unhappycodings.quarry.common.networking.toClient.ClientPayloadHandler;
import de.unhappycodings.quarry.common.networking.toClient.QuarryClientBooleanPacket;
import de.unhappycodings.quarry.common.networking.toClient.QuarryClientIntPacket;
import de.unhappycodings.quarry.common.networking.toClient.QuarryClientModePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.gui.screens.MenuScreens;

public class QuarryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(Quarry.QUARRY_CONTAINER.get(), QuarryScreen::new);
        MenuScreens.register(Quarry.AREA_CARD_CONTAINER.get(), AreaCardScreen::new);
        BlockEntityRendererRegistry.register(Quarry.QUARRY_ENTITY.get(), QuarryEntityRenderer::new);
        BlockEntityRendererRegistry.register(Quarry.ENERGY_QUARRY_ENTITY.get(), QuarryEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(QuarryClientBooleanPacket.TYPE, ClientPayloadHandler::handleQuarryClientBooleanPacketOnMain);
        ClientPlayNetworking.registerGlobalReceiver(QuarryClientIntPacket.TYPE, ClientPayloadHandler::handleQuarryClientIntPacketOnMain);
        ClientPlayNetworking.registerGlobalReceiver(QuarryClientModePacket.TYPE, ClientPayloadHandler::handleQuarryClientModePacketOnMain);

        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(AreaCardLevelRenderer::renderSquareAboveWorldCentre);
    }
}
