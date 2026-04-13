package de.unhappycodings.quarry.common.container;

import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.container.base.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class AreaCardContainer extends BaseContainer {
    public BlockPos pos;
    public Level level;

    public AreaCardContainer(int id, Inventory inventory, BlockPos pos, Level level) {
        super(Quarry.AREA_CARD_CONTAINER.get(), id, inventory, pos, level);
        layoutPlayerInventorySlots(8, 105);
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
    public boolean stillValid(@Nonnull Player playerIn) {
        return true;
    }
}
