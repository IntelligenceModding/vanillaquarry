package de.unhappycodings.quarry.common;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.blocks.ModBlocks;
import de.unhappycodings.quarry.common.registration.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Quarry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemCreativeTab {

    @SubscribeEvent
    public static void registerCreativeTab(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(Quarry.getRL("creativetab"), ItemCreativeTab::populateCreativeTabBuilder);
    }

    private static void populateCreativeTabBuilder(CreativeModeTab.Builder builder) {
        builder.displayItems((features, output, displayOperatorCreativeTab) -> {
            Registration.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(output::accept);
        });
        builder.icon(() -> ModBlocks.QUARRY.get().asItem().getDefaultInstance());
        builder.title(Component.translatable("itemGroup.quarry.items"));
    }

}
