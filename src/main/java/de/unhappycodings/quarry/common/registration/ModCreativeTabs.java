package de.unhappycodings.quarry.common.registration;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.ItemCreativeTab;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final RegistryObject<CreativeModeTab> QUARRY_MODE_TAB = Registration.CREATIVE_MODE_TABS.register(Quarry.MOD_ID, ModCreativeTabs::createCreativeTab);

    private static CreativeModeTab createCreativeTab() {
        CreativeModeTab.Builder builder = new CreativeModeTab.Builder(CreativeModeTab.Row.BOTTOM, -1);
        ItemCreativeTab.populateCreativeTabBuilder(builder);
        return builder.build();
    }
    public static void register() {

    }

}
