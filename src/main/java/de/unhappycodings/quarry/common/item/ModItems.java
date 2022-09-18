package de.unhappycodings.quarry.common.item;

import de.unhappycodings.quarry.common.registration.Registration;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final RegistryObject<AreaCardItem> AREA_CARD = Registration.ITEMS.register("area_card", AreaCardItem::new);

    public static void register() {
    }

}
