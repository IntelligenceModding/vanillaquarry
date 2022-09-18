package de.unhappycodings.quarry.common.setup;

import de.unhappycodings.quarry.common.container.QuarryContainer;
import de.unhappycodings.quarry.common.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

public class ContainerTypes {

    public static final RegistryObject<MenuType<QuarryContainer>> QUARRY_CONTAINER = Registration.CONTAINER_TYPES.register("quarry_container", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level level = inv.player.getCommandSenderWorld();
        return new QuarryContainer(windowId, inv, pos, level);
    }));

    public static void register() {
    }

}
