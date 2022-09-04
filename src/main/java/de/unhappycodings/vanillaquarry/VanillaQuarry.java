package de.unhappycodings.vanillaquarry;

import com.mojang.logging.LogUtils;
import de.unhappycodings.vanillaquarry.common.ItemCreativeTab;
import de.unhappycodings.vanillaquarry.common.blockentity.ModBlockEntities;
import de.unhappycodings.vanillaquarry.common.blocks.ModBlocks;
import de.unhappycodings.vanillaquarry.common.config.CommonConfig;
import de.unhappycodings.vanillaquarry.common.item.ModItems;
import de.unhappycodings.vanillaquarry.common.network.PacketHandler;
import de.unhappycodings.vanillaquarry.common.registration.Registration;
import de.unhappycodings.vanillaquarry.common.setup.ContainerTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.util.Random;

@Mod("vanillaquarry")
public class VanillaQuarry {

    public static final String MOD_ID = "vanillaquarry";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation COUNTER_UP = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/counter_plus.png");
    public static final ResourceLocation COUNTER_UP_DARK = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/counter_plus_dark.png");
    public static final ResourceLocation COUNTER_DOWN = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/counter_minus.png");
    public static final ResourceLocation COUNTER_DOWN_DARK  = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/counter_minus_dark.png");
    public static final ResourceLocation POWER = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/power.png");
    public static final ResourceLocation POWER_DARK = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/power_dark.png");
    public static final ResourceLocation MODE = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/mode.png");
    public static final ResourceLocation MODE_DARK = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/mode_dark.png");
    public static final ResourceLocation INFO = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/information.png");

    public static final ResourceLocation WHITE_MODE = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/dark_mode_switch_off.png");
    public static final ResourceLocation DARK_MODE = new ResourceLocation(VanillaQuarry.MOD_ID, "textures/gui/button/dark_mode_switch_on.png");

    public static final CreativeModeTab creativeTab = new ItemCreativeTab();

    public VanillaQuarry() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        LOGGER.info("[" + MOD_ID + "] Initialization");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.commonConfig);

        Registration.register();
        ModItems.register();
        ModBlocks.register();
        ContainerTypes.register();
        ModBlockEntities.BLOCK_ENTITIES.register(bus);

        CommonConfig.loadConfigFile(CommonConfig.commonConfig, FMLPaths.CONFIGDIR.get().resolve("vanillaquarry-common.toml").toString());
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::onCommonSetup);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::init);
    }

}
