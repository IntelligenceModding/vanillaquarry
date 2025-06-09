package de.unhappycodings.quarry;

import com.mojang.logging.LogUtils;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.common.blockentity.ModBlockEntities;
import de.unhappycodings.quarry.common.blocks.ModBlocks;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.container.ContainerTypes;
import de.unhappycodings.quarry.common.item.ModItems;
import de.unhappycodings.quarry.common.network.PacketHandler;
import de.unhappycodings.quarry.common.registration.ModCreativeTabs;
import de.unhappycodings.quarry.common.registration.Registration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

@Mod("quarry")
public class Quarry {
    public static final String MOD_ID = "quarry";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation COUNTER_UP = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/counter_plus.png");
    public static final ResourceLocation COUNTER_DOWN = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/counter_minus.png");
    public static final ResourceLocation POWER = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/power.png");
    public static final ResourceLocation MODE = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/mode.png");
    public static final ResourceLocation INFO = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/information.png");
    public static final ResourceLocation LOCK = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/lock.png");
    public static final ResourceLocation LOOP = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/loop.png");
    public static final ResourceLocation FILTER = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/filter.png");
    public static final ResourceLocation EJECT = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/eject.png");

    public static final ResourceLocation DARK_MODE = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/dark_mode_switch.png");

    public static final ResourceLocation BLANK = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/blank.png");
    public static final ResourceLocation SAVE = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/save.png");
    public static final ResourceLocation FIELD = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/field.png");
    public static final ResourceLocation SELECTOR = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/selector_on.png");
    public static final ResourceLocation SELECTOR_OFF = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/selector_off.png");
    public static final ResourceLocation SKIP = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/skip.png");
    public static final ResourceLocation REPLACE = new ResourceLocation(Quarry.MOD_ID, "textures/gui/button/replace.png");

    public Quarry() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        LOGGER.info("[" + MOD_ID + "] Initialization");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.commonConfig);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.clientConfig);

        Registration.register();
        ModItems.register();
        ModBlocks.register();
        ContainerTypes.register();
        ModCreativeTabs.register();
        ModBlockEntities.BLOCK_ENTITIES.register(bus);

        CommonConfig.loadConfigFile(CommonConfig.commonConfig, FMLPaths.CONFIGDIR.get().resolve("quarry-common.toml").toString());
        ClientConfig.loadConfigFile(ClientConfig.clientConfig, FMLPaths.CONFIGDIR.get().resolve("quarry-client.toml").toString());
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::onCommonSetup);
    }

    public static ResourceLocation getRL(String resource) {
        return new ResourceLocation(MOD_ID, resource);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::init);
    }

}
