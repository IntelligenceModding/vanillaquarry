package de.unhappycodings.quarry;

import com.mojang.serialization.Codec;
import de.unhappycodings.quarry.client.config.ClientConfig;
import de.unhappycodings.quarry.common.block.FEQuarryBlock;
import de.unhappycodings.quarry.common.block.QuarryBlock;
import de.unhappycodings.quarry.common.blockentity.FEQuarryEntity;
import de.unhappycodings.quarry.common.blockentity.QuarryEntity;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.config.ServerConfig;
import de.unhappycodings.quarry.common.container.AreaCardContainer;
import de.unhappycodings.quarry.common.container.QuarryContainer;
import de.unhappycodings.quarry.common.item.AreaCard;
import de.unhappycodings.quarry.common.item.FEQuarryItem;
import de.unhappycodings.quarry.common.item.QuarryItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(Quarry.MOD_ID)
public class Quarry {
    public static final String MOD_ID = "quarry";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Identifier COUNTER_UP = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/counter_plus.png");
    public static final Identifier COUNTER_DOWN = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/counter_minus.png");
    public static final Identifier POWER = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/power.png");
    public static final Identifier MODE = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/mode.png");
    public static final Identifier INFO = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/information.png");
    public static final Identifier LOCK = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/lock.png");
    public static final Identifier LOOP = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/loop.png");
    public static final Identifier FILTER = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/filter.png");
    public static final Identifier EJECT = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/eject.png");

    public static final Identifier DARK_MODE = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/dark_mode_switch.png");
    public static final Identifier RESET = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/reset.png");

    public static final Identifier BLANK = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/blank.png");
    public static final Identifier FIELD = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/field.png");
    public static final Identifier SKIP = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/skip.png");
    public static final Identifier REPLACE = Identifier.tryBuild(Quarry.MOD_ID, "textures/gui/button/replace.png");

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Quarry.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Quarry.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Quarry.MOD_ID);

    public static final DeferredBlock<QuarryBlock> QUARRY_BLOCK = register("quarry_block", QuarryBlock::new, QuarryItem::new);
    public static final DeferredBlock<FEQuarryBlock> FE_QUARRY_BLOCK = register("fe_quarry_block", FEQuarryBlock::new, FEQuarryItem::new);
    public static final Supplier<BlockEntityType<QuarryEntity>> QUARRY_ENTITY = BLOCK_ENTITY_TYPES.register("quarry_block", () -> new BlockEntityType<>(QuarryEntity::new, QUARRY_BLOCK.get()));
    public static final Supplier<BlockEntityType<FEQuarryEntity>> FE_QUARRY_ENTITY = BLOCK_ENTITY_TYPES.register("fe_quarry_block", () -> new BlockEntityType<>(FEQuarryEntity::new, FE_QUARRY_BLOCK.get()));
    public static final DeferredItem<AreaCard> AREA_CARD = ITEMS.registerItem("area_card", AreaCard::new);

    public static final Supplier<MenuType<QuarryContainer>> QUARRY_CONTAINER = CONTAINER_TYPES.register("quarry_container", () -> IMenuTypeExtension.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level level = inv.player.level();
        return new QuarryContainer(windowId, inv, pos, level);
    }));

    public static final Supplier<MenuType<AreaCardContainer>> AREA_CARD_CONTAINER = CONTAINER_TYPES.register("area_card_container", () -> IMenuTypeExtension.create((windowId, inv, data) -> {
        BlockPos pos = inv.player.getOnPos();
        Level level = inv.player.level();
        return new AreaCardContainer(windowId, inv, pos, level);
    }));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> QUARRY_TAB = CREATIVE_MODE_TABS.register("quarry_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.quarry.items")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(QUARRY_BLOCK::toStack)
            .displayItems((parameters, output) -> {
                output.accept(QUARRY_BLOCK.get());
                output.accept(FE_QUARRY_BLOCK.get());
                output.accept(AREA_CARD.get());
            }).build());

    public static final Supplier<DataComponentType<Integer>> LAST_BLOCK =
            Quarry.DATA_COMPONENTS.register("lastblock", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .build()
            );
    public static final Supplier<DataComponentType<Integer>> CURRENT_Y =
            Quarry.DATA_COMPONENTS.register("currenty", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .build()
            );
    public static final Supplier<DataComponentType<CompoundTag>> POS_1 =
            Quarry.DATA_COMPONENTS.register("pos1", () ->
                    DataComponentType.<CompoundTag>builder()
                            .persistent(CompoundTag.CODEC)
                            .build()
            );
    public static final Supplier<DataComponentType<CompoundTag>> POS_2 =
            Quarry.DATA_COMPONENTS.register("pos2", () ->
                    DataComponentType.<CompoundTag>builder()
                            .persistent(CompoundTag.CODEC)
                            .build()
            );
    public static final Supplier<DataComponentType<CompoundTag>> FILTERS =
            Quarry.DATA_COMPONENTS.register("filters", () ->
                    DataComponentType.<CompoundTag>builder()
                            .persistent(CompoundTag.CODEC)
                            .build()
            );
    public static final Supplier<DataComponentType<Integer>> SELECTION =
            Quarry.DATA_COMPONENTS.register("selection", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .build()
            );

    public static Supplier<DataComponentType<CompoundTag>> get(String text) {
        if (text.contains("pos1")) {
            return POS_1;
        } else if (text.contains("pos2")) {
            return POS_2;
        }
        return null;
    }

    public Quarry(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        CONTAINER_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.clientConfig);
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.commonConfig);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.serverConfig);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(AREA_CARD);
        }
    }

    private static <T extends Block> DeferredBlock<T> register(String name, Function<BlockBehaviour.Properties, T> block, BiFunction<T, Item.Properties, BlockItem> blockItem) {
        DeferredBlock<T> registryObject = BLOCKS.registerBlock(name, block);
        ITEMS.registerItem(name, properties -> blockItem.apply(registryObject.get(), properties));
        return registryObject;
    }

}
