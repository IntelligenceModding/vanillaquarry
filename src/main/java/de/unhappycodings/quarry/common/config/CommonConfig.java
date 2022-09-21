package de.unhappycodings.quarry.common.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class CommonConfig {

    public static ForgeConfigSpec commonConfig;

    //region General
    public static ForgeConfigSpec.IntValue areaCardOverlayColorFirstCorner;
    public static ForgeConfigSpec.IntValue areaCardOverlayColorSecondCorner;

    public static ForgeConfigSpec.IntValue quarryIdleConsumption;

    public static ForgeConfigSpec.IntValue quarryDefaultModeConsumption;
    public static ForgeConfigSpec.IntValue quarryEfficientModeConsumption;
    public static ForgeConfigSpec.IntValue quarryFortuneModeConsumption;
    public static ForgeConfigSpec.IntValue quarrySilkTouchModeConsumption;
    public static ForgeConfigSpec.IntValue quarryVoidModeConsumption;

    public static ForgeConfigSpec.DoubleValue quarrySpeedOneModifier;
    public static ForgeConfigSpec.DoubleValue quarrySpeedTwoModifier;
    public static ForgeConfigSpec.DoubleValue quarrySpeedThreeModifier;
    //endregion

    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

        init(commonBuilder);
        commonConfig = commonBuilder.build();
    }

    private static void init(ForgeConfigSpec.Builder commonBuilder) {
        commonBuilder.push("General");
        areaCardOverlayColorFirstCorner = commonBuilder.comment("What Color should the overlay at the first corner be [Format: #RRGGBB]")
                .defineInRange("first_corner_overlay_color",0x116300, 0, Integer.MAX_VALUE);
        areaCardOverlayColorSecondCorner = commonBuilder.comment("What Color should the overlay at the second corner be [Format: #RRGGBB]")
                .defineInRange("second_corner_overlay_color",0x630000, 0, Integer.MAX_VALUE);

        quarryIdleConsumption = commonBuilder.comment("BurnTick consumption of the quarry in idle mode per second")
                .defineInRange("quarry_idle_consumption", 1, 0, 1000);
        quarryDefaultModeConsumption = commonBuilder.comment("Default mode BurnTick consumption")
                .defineInRange("quarry_mode_default_consumption", 100, 0, 1000);
        quarryEfficientModeConsumption = commonBuilder.comment("Efficient mode BurnTick consumption")
                .defineInRange("quarry_mode_efficient_consumption", 80, 0, 1000);
        quarryFortuneModeConsumption = commonBuilder.comment("Fortune mode BurnTick consumption")
                .defineInRange("quarry_mode_fortune_consumption", 200, 0, 1000);
        quarrySilkTouchModeConsumption = commonBuilder.comment("Silk Touch mode BurnTick consumption")
                .defineInRange("quarry_mode_silktouch_consumption", 200, 0, 1000);
        quarryVoidModeConsumption = commonBuilder.comment("Void mode BurnTick consumption")
                .defineInRange("quarry_mode_void_consumption", 100, 0, 1000);

        quarrySpeedOneModifier = commonBuilder.comment("Speed 1 BurnTick consumption multiplier")
                .defineInRange("quarry_speed_one_multiplier", 1.0, 0.0, 5.0);
        quarrySpeedTwoModifier = commonBuilder.comment("Speed 2 BurnTick consumption multiplier")
                .defineInRange("quarry_speed_two_multiplier", 1.25, 0.0, 5.0);
        quarrySpeedThreeModifier = commonBuilder.comment("Speed 3 BurnTick consumption multiplier")
                .defineInRange("quarry_speed_three_multiplier", 1.5, 0.0, 5.0);
        commonBuilder.pop();
    }

    public static void loadConfigFile(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }

}
