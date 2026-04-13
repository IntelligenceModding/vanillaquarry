package de.unhappycodings.quarry.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NbtUtil {

    public static void writePos(CompoundTag nbt, BlockPos pos) {
        nbt.putInt("x", pos.getX());
        nbt.putInt("y", pos.getY());
        nbt.putInt("z", pos.getZ());
    }

    public static BlockPos getPos(@Nullable CompoundTag tag) {
        if (tag == null)
            return BlockPos.ZERO;

        if (!tag.contains("x") || !tag.contains("y") || !tag.contains("z"))
            return BlockPos.ZERO;
        return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }

}
