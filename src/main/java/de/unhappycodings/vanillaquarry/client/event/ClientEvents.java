package de.unhappycodings.vanillaquarry.client.event;

import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.common.blockentity.QuarryBlockEntity;
import de.unhappycodings.vanillaquarry.common.container.QuarryScreen;
import de.unhappycodings.vanillaquarry.common.setup.ContainerTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = VanillaQuarry.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(ContainerTypes.QUARRY_CONTAINER.get(), QuarryScreen::new);
    }

}
