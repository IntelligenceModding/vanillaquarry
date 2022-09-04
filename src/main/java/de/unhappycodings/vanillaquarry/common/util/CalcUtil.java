package de.unhappycodings.vanillaquarry.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class CalcUtil {

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

        for (y = yMax; y >= yMin; y--) {
            for (x = xMin; x <= xMax; x++) {
                for (z = zMin; z <= zMax; z++) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static float getNeededTicks(int mode, int speed) {
        int fuelModifier = switch (mode) {
            case 1 -> 80;     // Efficient
            case 2, 3 -> 200; // Fortune, Silktouch
            default -> 100;   // Default, Void
        };
        switch (speed) {
            case 1 -> fuelModifier *= 1.25;
            case 2 -> fuelModifier *= 1.5;
        }
        return fuelModifier;
    }

}
