package de.unhappycodings.quarry.common.container;

import de.unhappycodings.quarry.common.container.base.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AreaCardContainer extends BaseContainer {
    public BlockPos pos;
    public Level level;

    public AreaCardContainer(int id, Inventory inventory, BlockPos pos, Level level) {
        super(ContainerTypes.AREA_CARD_CONTAINER.get(), id, inventory, pos, level);
        this.pos = pos;
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return true;
    }
}
