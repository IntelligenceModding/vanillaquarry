package de.unhappycodings.quarry.common.util;

import de.unhappycodings.quarry.common.config.CommonConfig;
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

    public static int getBlockAmount(BlockPos loc1, BlockPos loc2) {
        int i = 0;
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
                    i++;
                }
            }
        }
        return i;
    }

    public static int getBlockCount(BlockPos pos1, BlockPos pos2) {
        int x1 = pos1.getX();
        int y1 = pos1.getY();
        int z1 = pos1.getZ();
        int x2 = pos2.getX();
        int y2 = pos2.getY();
        int z2 = pos2.getZ();
        if (y1 > 320) y1 = 320;
        if (y1 < -64) y1 = -64;
        if (y2 > 320) y2 = 230;
        if (y2 < -64) y2 = -64;
        return Math.abs((x1 - x2 + 1) * (y1 - y2 + 1) * (z1 - z2 + 1));
    }

    public static float getNeededTicks(int mode, int speed) {
        return getNeededTicks(mode, speed, false);
    }

    public static float getNeededTicks(int mode, int speed, boolean energyPowered) {
        int fuelModifier = switch (mode) {
            case 1 -> energyPowered ? CommonConfig.feQuarryEfficientModeConsumption.get() : CommonConfig.quarryEfficientModeConsumption.get();
            case 2 -> energyPowered ? CommonConfig.feQuarryFortuneModeConsumption.get() : CommonConfig.quarryFortuneModeConsumption.get();
            case 3 -> energyPowered ? CommonConfig.feQuarrySilkTouchModeConsumption.get() : CommonConfig.quarrySilkTouchModeConsumption.get();
            case 4 -> energyPowered ? CommonConfig.feQuarryVoidModeConsumption.get() : CommonConfig.quarryVoidModeConsumption.get();
            default -> energyPowered ? CommonConfig.feQuarryDefaultModeConsumption.get() : CommonConfig.quarryDefaultModeConsumption.get();
        };
        switch (speed) {
            case 0 -> fuelModifier *= energyPowered ? CommonConfig.feQuarrySpeedOneModifier.get() : CommonConfig.quarrySpeedOneModifier.get();
            case 1 -> fuelModifier *= energyPowered ? CommonConfig.feQuarrySpeedTwoModifier.get() : CommonConfig.quarrySpeedTwoModifier.get();
            case 2 -> fuelModifier *= energyPowered ? CommonConfig.feQuarrySpeedThreeModifier.get() : CommonConfig.quarrySpeedThreeModifier.get();
            case 3 -> fuelModifier *= energyPowered ? CommonConfig.feQuarrySpeedFourModifier.get() : CommonConfig.quarrySpeedFourModifier.get();
            case 4 -> fuelModifier *= energyPowered ? CommonConfig.feQuarrySpeedFiveModifier.get() : CommonConfig.quarrySpeedFiveModifier.get();
            case 5 -> fuelModifier *= energyPowered ? CommonConfig.feQuarrySpeedSixModifier.get() : CommonConfig.quarrySpeedSixModifier.get();
            case 6 -> fuelModifier *= energyPowered ? CommonConfig.feQuarrySpeedSevenModifier.get() : CommonConfig.quarrySpeedSevenModifier.get();
        }
        return fuelModifier;
    }

}
