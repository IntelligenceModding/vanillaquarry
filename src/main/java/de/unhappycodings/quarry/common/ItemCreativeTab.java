package de.unhappycodings.quarry.common;

import de.unhappycodings.quarry.common.blocks.ModBlocks;
import de.unhappycodings.quarry.common.registration.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.RegistryObject;

public class ItemCreativeTab {

    public static void populateCreativeTabBuilder(CreativeModeTab.Builder builder) {
        builder.displayItems((features, output) -> {
            Registration.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(output::accept);
        });
        builder.icon(() -> ModBlocks.QUARRY.get().asItem().getDefaultInstance());
        builder.withTabsBefore(CreativeModeTabs.SPAWN_EGGS);
        builder.title(Component.translatable("itemGroup.quarry.items"));
    }

}
