package de.unhappycodings.vanillaquarry.common.util;

import de.unhappycodings.vanillaquarry.common.config.CommonConfig;
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
            case 1 -> CommonConfig.quarryEfficientModeConsumption.get();  // Efficient
            case 2 -> CommonConfig.quarryFortuneModeConsumption.get();    // Fortune
            case 3 -> CommonConfig.quarrySilkTouchModeConsumption.get();  // Silktouch
            case 4 -> CommonConfig.quarryVoidModeConsumption.get();       // Void
            default -> CommonConfig.quarryDefaultModeConsumption.get();   // Default
        };
        switch (speed) {
            case 0 -> fuelModifier *= CommonConfig.quarrySpeedOneModifier.get();
            case 1 -> fuelModifier *= CommonConfig.quarrySpeedTwoModifier.get();
            case 2 -> fuelModifier *= CommonConfig.quarrySpeedThreeModifier.get();
        }
        return fuelModifier;
    }

}
