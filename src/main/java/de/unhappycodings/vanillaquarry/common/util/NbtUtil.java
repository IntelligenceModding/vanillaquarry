package de.unhappycodings.vanillaquarry.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class NbtUtil {

    public static void writePos(CompoundTag nbt, BlockPos pos) {
        nbt.putInt("x", pos.getX());
        nbt.putInt("y", pos.getY());
        nbt.putInt("z", pos.getZ());
    }

    public static BlockPos getPos(CompoundTag tag) throws IllegalStateException {
        if (!tag.contains("x") || !tag.contains("y") || !tag.contains("z"))
            throw new IllegalStateException("Tag does not contain position");
        return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }

    public static void removePosFromList(ListTag listNbt, BlockPos pos) {
        for (Tag tag : listNbt) {
            CompoundTag position = (CompoundTag) tag;
            BlockPos blockPos = new BlockPos(position.getInt("x"), position.getInt("y"), position.getInt("z"));
            if (pos.equals(blockPos)) {
                listNbt.remove(tag);
                return;
            }
        }
    }

    public static boolean posExistsInList(ListTag listNbt, BlockPos pos) {
        for (Tag tag : listNbt) {
            CompoundTag position = (CompoundTag) tag;
            BlockPos blockPos = new BlockPos(position.getInt("x"), position.getInt("y"), position.getInt("z"));
            if (pos.equals(blockPos))
                return true;
        }
        return false;
    }

}
