package de.unhappycodings.quarry.common.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class CommonConfig {

    public static ForgeConfigSpec commonConfig;

    //region General
    public static ForgeConfigSpec.ConfigValue<String> areaCardOverlayColorFirstCorner;
    public static ForgeConfigSpec.ConfigValue<String> areaCardOverlayColorSecondCorner;

    public static ForgeConfigSpec.ConfigValue<Integer> quarryIdleConsumption;

    public static ForgeConfigSpec.ConfigValue<Integer> quarryDefaultModeConsumption;
    public static ForgeConfigSpec.ConfigValue<Integer> quarryEfficientModeConsumption;
    public static ForgeConfigSpec.ConfigValue<Integer> quarryFortuneModeConsumption;
    public static ForgeConfigSpec.ConfigValue<Integer> quarrySilkTouchModeConsumption;
    public static ForgeConfigSpec.ConfigValue<Integer> quarryVoidModeConsumption;

    public static ForgeConfigSpec.ConfigValue<Double> quarrySpeedOneModifier;
    public static ForgeConfigSpec.ConfigValue<Double> quarrySpeedTwoModifier;
    public static ForgeConfigSpec.ConfigValue<Double> quarrySpeedThreeModifier;
    public static ForgeConfigSpec.ConfigValue<Double> quarrySpeedFourModifier;
    public static ForgeConfigSpec.ConfigValue<Integer> quarryMineRadius;
    //endregion

    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

        init(commonBuilder);
        commonConfig = commonBuilder.build();
    }

    private static void init(ForgeConfigSpec.Builder commonBuilder) {
        commonBuilder.push("General");
        areaCardOverlayColorFirstCorner = commonBuilder.comment("What Color should the overlay at the first corner be [Format: #RRGGBB]").define("first_corner_overlay_color", "#116300");
        areaCardOverlayColorSecondCorner = commonBuilder.comment("What Color should the overlay at the second corner be [Format: #RRGGBB]").define("second_corner_overlay_color", "#630000");

        quarryIdleConsumption = commonBuilder.comment("BurnTick consumption of the quarry in idle mode per second").define("quarry_idle_consumption", 1);
        quarryDefaultModeConsumption = commonBuilder.comment("Default mode BurnTick consumption").define("quarry_mode_default_consumption", 50);
        quarryEfficientModeConsumption = commonBuilder.comment("Efficient mode BurnTick consumption").define("quarry_mode_efficient_consumption", 40);
        quarryFortuneModeConsumption = commonBuilder.comment("Fortune mode BurnTick consumption").define("quarry_mode_fortune_consumption", 100);
        quarrySilkTouchModeConsumption = commonBuilder.comment("Silk Touch mode BurnTick consumption").define("quarry_mode_silktouch_consumption", 100);
        quarryVoidModeConsumption = commonBuilder.comment("Void mode BurnTick consumption").define("quarry_mode_void_consumption", 50);

        quarrySpeedOneModifier = commonBuilder.comment("Speed 1 BurnTick consumption multiplier").define("quarry_speed_one_multiplier", 1.0);
        quarrySpeedTwoModifier = commonBuilder.comment("Speed 2 BurnTick consumption multiplier").define("quarry_speed_two_multiplier", 1.2);
        quarrySpeedThreeModifier = commonBuilder.comment("Speed 3 BurnTick consumption multiplier").define("quarry_speed_three_multiplier", 1.4);
        quarrySpeedFourModifier = commonBuilder.comment("Speed 4 BurnTick consumption multiplier").define("quarry_speed_four_multiplier", 1.6);

        quarryMineRadius = commonBuilder.comment("Radius where quarry can mine around it. Radius is square, like chunks").define("quarry_mine_radius", 32);
        commonBuilder.pop();
    }

    public static void loadConfigFile(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }

}
