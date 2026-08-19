package de.unhappycodings.quarry.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {

    public static ModConfigSpec commonConfig;

    //region General
    public static ModConfigSpec.ConfigValue<Integer> quarryIdleConsumption;

    public static ModConfigSpec.ConfigValue<Integer> quarryDefaultModeConsumption;
    public static ModConfigSpec.ConfigValue<Integer> quarryEfficientModeConsumption;
    public static ModConfigSpec.ConfigValue<Integer> quarryFortuneModeConsumption;
    public static ModConfigSpec.ConfigValue<Integer> quarrySilkTouchModeConsumption;
    public static ModConfigSpec.ConfigValue<Integer> quarryVoidModeConsumption;

    public static ModConfigSpec.ConfigValue<Double> quarrySpeedOneModifier;
    public static ModConfigSpec.ConfigValue<Double> quarrySpeedTwoModifier;
    public static ModConfigSpec.ConfigValue<Double> quarrySpeedThreeModifier;
    public static ModConfigSpec.ConfigValue<Double> quarrySpeedFourModifier;
    public static ModConfigSpec.ConfigValue<Double> quarrySpeedFiveModifier;
    public static ModConfigSpec.ConfigValue<Double> quarrySpeedSixModifier;
    public static ModConfigSpec.ConfigValue<Double> quarrySpeedSevenModifier;

    public static ModConfigSpec.ConfigValue<Integer> feQuarryIdleConsumption;

    public static ModConfigSpec.ConfigValue<Integer> feQuarryDefaultModeConsumption;
    public static ModConfigSpec.ConfigValue<Integer> feQuarryEfficientModeConsumption;
    public static ModConfigSpec.ConfigValue<Integer> feQuarryFortuneModeConsumption;
    public static ModConfigSpec.ConfigValue<Integer> feQuarrySilkTouchModeConsumption;
    public static ModConfigSpec.ConfigValue<Integer> feQuarryVoidModeConsumption;

    public static ModConfigSpec.ConfigValue<Double> feQuarrySpeedOneModifier;
    public static ModConfigSpec.ConfigValue<Double> feQuarrySpeedTwoModifier;
    public static ModConfigSpec.ConfigValue<Double> feQuarrySpeedThreeModifier;
    public static ModConfigSpec.ConfigValue<Double> feQuarrySpeedFourModifier;
    public static ModConfigSpec.ConfigValue<Double> feQuarrySpeedFiveModifier;
    public static ModConfigSpec.ConfigValue<Double> feQuarrySpeedSixModifier;
    public static ModConfigSpec.ConfigValue<Double> feQuarrySpeedSevenModifier;
    public static ModConfigSpec.ConfigValue<Integer> quarryMineRadius;
    //endregion

    static {
        ModConfigSpec.Builder commonBuilder = new ModConfigSpec.Builder();

        init(commonBuilder);
        commonConfig = commonBuilder.build();
    }

    private static void init(ModConfigSpec.Builder commonBuilder) {
        commonBuilder.push("General");

        quarryIdleConsumption = commonBuilder.comment("BurnTick consumption of the quarry in idle mode (per second)").define("quarry_idle_consumption", 1);
        quarryDefaultModeConsumption = commonBuilder.comment("Default mode BurnTick consumption").define("quarry_mode_default_consumption", 50);
        quarryEfficientModeConsumption = commonBuilder.comment("Efficient mode BurnTick consumption").define("quarry_mode_efficient_consumption", 40);
        quarryFortuneModeConsumption = commonBuilder.comment("Fortune mode BurnTick consumption").define("quarry_mode_fortune_consumption", 100);
        quarrySilkTouchModeConsumption = commonBuilder.comment("Silk Touch mode BurnTick consumption").define("quarry_mode_silktouch_consumption", 100);
        quarryVoidModeConsumption = commonBuilder.comment("Void mode BurnTick consumption").define("quarry_mode_void_consumption", 50);

        quarrySpeedOneModifier = commonBuilder.comment("Speed 1 BurnTick consumption multiplier").define("quarry_speed_one_multiplier", 1.0);
        quarrySpeedTwoModifier = commonBuilder.comment("Speed 2 BurnTick consumption multiplier").define("quarry_speed_two_multiplier", 1.2);
        quarrySpeedThreeModifier = commonBuilder.comment("Speed 3 BurnTick consumption multiplier").define("quarry_speed_three_multiplier", 1.5);
        quarrySpeedFourModifier = commonBuilder.comment("Speed 4 BurnTick consumption multiplier").define("quarry_speed_four_multiplier", 1.8);
        quarrySpeedFiveModifier = commonBuilder.comment("Speed 5 BurnTick consumption multiplier").define("quarry_speed_five_multiplier", 2.3);
        quarrySpeedSixModifier = commonBuilder.comment("Speed 6 BurnTick consumption multiplier").define("quarry_speed_six_multiplier", 2.9);
        quarrySpeedSevenModifier = commonBuilder.comment("Speed 7 BurnTick consumption multiplier").define("quarry_speed_seven_multiplier", 3.6);

        feQuarryIdleConsumption = commonBuilder.comment("FE consumption of the FE Quarry in idle mode (per second)").define("fe_quarry_idle_consumption", 1);
        feQuarryDefaultModeConsumption = commonBuilder.comment("Default mode FE consumption").define("fe_quarry_mode_default_consumption", 150);
        feQuarryEfficientModeConsumption = commonBuilder.comment("Efficient mode FE consumption").define("fe_quarry_mode_efficient_consumption", 120);
        feQuarryFortuneModeConsumption = commonBuilder.comment("Fortune mode FE consumption").define("fe_quarry_mode_fortune_consumption", 300);
        feQuarrySilkTouchModeConsumption = commonBuilder.comment("Silk Touch mode FE consumption").define("fe_quarry_mode_silktouch_consumption", 300);
        feQuarryVoidModeConsumption = commonBuilder.comment("Void mode FE consumption").define("fe_quarry_mode_void_consumption", 150);

        feQuarrySpeedOneModifier = commonBuilder.comment("Speed 1 FE consumption multiplier").define("fe_quarry_speed_one_multiplier", 1.0);
        feQuarrySpeedTwoModifier = commonBuilder.comment("Speed 2 FE consumption multiplier").define("fe_quarry_speed_two_multiplier", 1.2);
        feQuarrySpeedThreeModifier = commonBuilder.comment("Speed 3 FE consumption multiplier").define("fe_quarry_speed_three_multiplier", 1.5);
        feQuarrySpeedFourModifier = commonBuilder.comment("Speed 4 FE consumption multiplier").define("fe_quarry_speed_four_multiplier", 1.8);
        feQuarrySpeedFiveModifier = commonBuilder.comment("Speed 5 FE consumption multiplier").define("fe_quarry_speed_five_multiplier", 2.3);
        feQuarrySpeedSixModifier = commonBuilder.comment("Speed 6 FE consumption multiplier").define("fe_quarry_speed_six_multiplier", 2.9);
        feQuarrySpeedSevenModifier = commonBuilder.comment("Speed 7 FE consumption multiplier").define("fe_quarry_speed_seven_multiplier", 3.6);

        quarryMineRadius = commonBuilder.comment("Radius where quarry can mine around it. Radius is square, like blocks").define("quarry_mine_radius", 64);
        commonBuilder.pop();
    }

}
