package de.unhappycodings.vanillaquarry.client.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class ClientConfig {
    public static ForgeConfigSpec clientConfig;

    //region General
    public static ForgeConfigSpec.ConfigValue<Boolean> enableQuarryDarkmode;
    public static ForgeConfigSpec.ConfigValue<Boolean> enableEnableQuarryDarkmodeButton;
    public static ForgeConfigSpec.ConfigValue<Boolean> enableAreaCardCornerRendering;
    //endregion

    static {
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();

        init(clientBuilder);
        clientConfig = clientBuilder.build();
    }

    private static void init(ForgeConfigSpec.Builder clientBuilder) {
        clientBuilder.push("General");
        enableQuarryDarkmode = clientBuilder.comment("Should the quarry gui screen be rendered in Dark Mode.")
                .define("enable_quarry_darkmode", false);
        enableEnableQuarryDarkmodeButton = clientBuilder.comment("Enable dark mode button.")
                .define("enable_quarry_darkmode_button", true);
        enableAreaCardCornerRendering = clientBuilder.comment("Render the with Area Card selected corners in world.")
                .define("enable_area_card_corner_rendering", true);
        clientBuilder.pop();
    }

    public static void loadConfigFile(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }
}
