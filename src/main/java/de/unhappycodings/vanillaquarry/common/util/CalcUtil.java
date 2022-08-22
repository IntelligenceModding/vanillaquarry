package de.unhappycodings.vanillaquarry.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CalcUtil {

    public static List<Block> getBlocks(BlockPos loc1, BlockPos loc2, Level level) {
        List<Block> blocks = new ArrayList<>();
        int x1 = loc1.getX();
        int y1 = loc1.getY();
        int z1 = loc1.getZ();
        int x2 = loc2.getX();
        int y2 = loc2.getY();
        int z2 = loc2.getZ();
        int xMin, yMin, zMin;
        int xMax, yMax, zMax;
        int x, y, z;
        if (x1 > x2) {
            xMin = x2;
            xMax = x1;
        } else {
            xMin = x1;
            xMax = x2;
        }
        if (y1 > y2) {
            yMin = y2;
            yMax = y1;
        } else {
            yMin = y1;
            yMax = y2;
        }
        if (z1 > z2) {
            zMin = z2;
            zMax = z1;
        } else {
            zMin = z1;
            zMax = z2;
        }

        for (x = xMin; x <= xMax; x++) {
            for (y = yMin; y <= yMax; y++) {
                for (z = zMin; z <= zMax; z++) {
                    BlockState block = level.getBlockState(new BlockPos(x, y, z));
                    if (!level.isEmptyBlock(new BlockPos(x, y, z)) && !(block.getDestroySpeed(level, new BlockPos(x, y, z)) <= -1))
                        blocks.add(block.getBlock());
                }
            }
        }
        return blocks;
    }


    public static List<BlockPos> getBlockStates(BlockPos loc1, BlockPos loc2, Level level) {
        List<BlockPos> blocks = new ArrayList<>();
        int x1 = loc1.getX();
        int y1 = loc1.getY();
        int z1 = loc1.getZ();
        int x2 = loc2.getX();
        int y2 = loc2.getY();
        int z2 = loc2.getZ();
        int xMin, yMin, zMin;
        int xMax, yMax, zMax;
        int x, y, z;
        if (x1 > x2) {
            xMin = x2;
            xMax = x1;
        } else {
            xMin = x1;
            xMax = x2;
        }
        if (y1 > y2) {
            yMin = y2;
            yMax = y1;
        } else {
            yMin = y1;
            yMax = y2;
        }
        if (z1 > z2) {
            zMin = z2;
            zMax = z1;
        } else {
            zMin = z1;
            zMax = z2;
        }

        for (x = xMin; x <= xMax; x++) {
            for (y = yMin; y <= yMax; y++) {
                for (z = zMin; z <= zMax; z++) {
                    BlockState block = level.getBlockState(new BlockPos(x, y, z));
                    if (!level.isEmptyBlock(new BlockPos(x, y, z)) && !(block.getDestroySpeed(level, new BlockPos(x, y, z)) <= -1))
                        blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

}
