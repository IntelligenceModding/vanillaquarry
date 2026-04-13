package de.unhappycodings.quarry.common.event;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.container.AreaCardScreen;
import de.unhappycodings.quarry.common.container.QuarryScreen;
import de.unhappycodings.quarry.common.networking.toClient.ClientPayloadHandler;
import de.unhappycodings.quarry.common.networking.toClient.QuarryClientBooleanPacket;
import de.unhappycodings.quarry.common.networking.toClient.QuarryClientIntPacket;
import de.unhappycodings.quarry.common.networking.toClient.QuarryClientModePacket;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Quarry.MOD_ID)
public class ClientEvents {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientSetup(RegisterMenuScreensEvent event) {
        event.register(Quarry.QUARRY_CONTAINER.get(), QuarryScreen::new);
        event.register(Quarry.AREA_CARD_CONTAINER.get(), AreaCardScreen::new);
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                QuarryClientBooleanPacket.TYPE,
                QuarryClientBooleanPacket.STREAM_CODEC,
                ClientPayloadHandler::handleQuarryClientBooleanPacketOnMain
        );
        registrar.playToClient(
                QuarryClientIntPacket.TYPE,
                QuarryClientIntPacket.STREAM_CODEC,
                ClientPayloadHandler::handleQuarryClientIntPacketOnMain
        );
        registrar.playToClient(
                QuarryClientModePacket.TYPE,
                QuarryClientModePacket.STREAM_CODEC,
                ClientPayloadHandler::handleQuarryClientModePacketOnMain
        );
    }

}
