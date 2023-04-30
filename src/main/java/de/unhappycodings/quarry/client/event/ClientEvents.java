package de.unhappycodings.quarry.client.event;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.container.AreaCardScreen;
import de.unhappycodings.quarry.common.container.ContainerTypes;
import de.unhappycodings.quarry.common.container.QuarryScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Quarry.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(ContainerTypes.QUARRY_CONTAINER.get(), QuarryScreen::new);
        MenuScreens.register(ContainerTypes.AREA_CARD_CONTAINER.get(), AreaCardScreen::new);
    }

}
