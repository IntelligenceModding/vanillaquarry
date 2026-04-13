package de.unhappycodings.quarry.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {

    public static ModConfigSpec commonConfig;

    //region General
    public static ModConfigSpec.ConfigValue<String> areaCardOverlayColorFirstCorner;
    public static ModConfigSpec.ConfigValue<String> areaCardOverlayColorSecondCorner;

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
    public static ModConfigSpec.ConfigValue<Integer> quarryMineRadius;
    //endregion

    static {
        ModConfigSpec.Builder commonBuilder = new ModConfigSpec.Builder();

        init(commonBuilder);
        commonConfig = commonBuilder.build();
    }

    private static void init(ModConfigSpec.Builder commonBuilder) {
        commonBuilder.push("General");
        areaCardOverlayColorFirstCorner = commonBuilder.comment("What Color should the overlay at the first corner be [Format: #RRGGBB]").define("first_corner_overlay_color", "#004963");
        areaCardOverlayColorSecondCorner = commonBuilder.comment("What Color should the overlay at the second corner be [Format: #RRGGBB]").define("second_corner_overlay_color", "#630000");

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

        quarryMineRadius = commonBuilder.comment("Radius where quarry can mine around it. Radius is square, like blocks").define("quarry_mine_radius", 64);
        commonBuilder.pop();
    }

}
