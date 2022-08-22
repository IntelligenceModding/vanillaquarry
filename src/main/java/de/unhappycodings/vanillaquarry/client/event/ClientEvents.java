package de.unhappycodings.vanillaquarry.client.event;

import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.client.gui.widgets.ModButton;
import de.unhappycodings.vanillaquarry.common.blocks.ModBlocks;
import de.unhappycodings.vanillaquarry.common.container.QuarryContainer;
import de.unhappycodings.vanillaquarry.common.container.QuarryScreen;
import de.unhappycodings.vanillaquarry.common.setup.ContainerTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = VanillaQuarry.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(ContainerTypes.QUARRY_CONTAINER.get(), QuarryScreen::new);
    }

}
