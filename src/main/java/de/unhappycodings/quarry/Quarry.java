package de.unhappycodings.quarry;

import com.mojang.logging.LogUtils;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.common.ItemCreativeTab;
import de.unhappycodings.quarry.common.blockentity.ModBlockEntities;
import de.unhappycodings.quarry.common.blocks.ModBlocks;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.container.ContainerTypes;
import de.unhappycodings.quarry.common.item.ModItems;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.registration.Registration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

@Mod(Quarry.MOD_ID)
public class Quarry {

    public static final String MOD_ID = "quarry";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation COUNTER_UP = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/counter_plus.png");
    public static final ResourceLocation COUNTER_UP_DARK = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/counter_plus_dark.png");
    public static final ResourceLocation COUNTER_DOWN = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/counter_minus.png");
    public static final ResourceLocation COUNTER_DOWN_DARK = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/counter_minus_dark.png");
    public static final ResourceLocation POWER = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/power.png");
    public static final ResourceLocation POWER_DARK = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/power_dark.png");
    public static final ResourceLocation MODE = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/mode.png");
    public static final ResourceLocation MODE_DARK = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/mode_dark.png");
    public static final ResourceLocation INFO = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/information.png");
    public static final ResourceLocation LOCK = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/lock.png");
    public static final ResourceLocation LOCK_OPEN = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/lock_open.png");
    public static final ResourceLocation LOOP = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/loop_on.png");
    public static final ResourceLocation LOOP_OFF = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/loop_off.png");
    public static final ResourceLocation FILTER = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/filter_on.png");
    public static final ResourceLocation FILTER_OFF = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/filter_off.png");
    public static final ResourceLocation EJECT_OFF = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/eject_off.png");
    public static final ResourceLocation EJECT_ALL = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/eject_all.png");
    public static final ResourceLocation EJECT_IN = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/eject_in.png");
    public static final ResourceLocation EJECT_OUT = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/eject_out.png");

    public static final ResourceLocation WHITE_MODE = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/dark_mode_switch_off.png");
    public static final ResourceLocation DARK_MODE = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/dark_mode_switch_on.png");

    public static final CreativeModeTab creativeTab = new ItemCreativeTab();

    public Quarry() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        LOGGER.info("[" + MOD_ID + "] Initialization");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.commonConfig);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.clientConfig);

        Registration.register();
        ModItems.register();
        ModBlocks.register();
        ContainerTypes.register();
        ModBlockEntities.BLOCK_ENTITIES.register(bus);

        CommonConfig.loadConfigFile(CommonConfig.commonConfig, FMLPaths.CONFIGDIR.get().resolve("quarry-common.toml").toString());
        ClientConfig.loadConfigFile(ClientConfig.clientConfig, FMLPaths.CONFIGDIR.get().resolve("quarry-client.toml").toString());
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::onCommonSetup);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::init);
    }

}
