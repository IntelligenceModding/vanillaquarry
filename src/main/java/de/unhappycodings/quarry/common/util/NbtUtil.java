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

    public static CompoundTag getNbtTag(ItemStack itemStack) {
        return itemStack.getOrCreateTag();
    }

    public static String getNbtString(CompoundTag tag, String key) {
        return tag.getString(key);
    }

    public static int getNbtInt(CompoundTag tag, String key) {
        return tag.getInt(key);
    }

    public static BlockPos getPos(@Nullable CompoundTag tag) throws IllegalStateException {
        if (tag == null)
            return null;

        if (!tag.contains("x") || !tag.contains("y") || !tag.contains("z"))
            return null;
        return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }

}
