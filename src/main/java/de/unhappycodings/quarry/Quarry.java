package de.unhappycodings.quarry;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import de.unhappycodings.quarry.common.block.EnergyQuarryBlock;
import de.unhappycodings.quarry.common.block.QuarryBlock;
import de.unhappycodings.quarry.common.blockentity.EnergyQuarryEntity;
import de.unhappycodings.quarry.common.blockentity.QuarryEntity;
import de.unhappycodings.quarry.common.container.AreaCardContainer;
import de.unhappycodings.quarry.common.container.QuarryContainer;
import de.unhappycodings.quarry.common.event.ModEvents;
import de.unhappycodings.quarry.common.item.AreaCard;
import de.unhappycodings.quarry.common.item.EnergyQuarryItem;
import de.unhappycodings.quarry.common.item.QuarryItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Quarry implements ModInitializer {
    public static final String MOD_ID = "quarry";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Identifier COUNTER_UP = id("textures/gui/button/counter_plus.png");
    public static final Identifier COUNTER_DOWN = id("textures/gui/button/counter_minus.png");
    public static final Identifier POWER = id("textures/gui/button/power.png");
    public static final Identifier MODE = id("textures/gui/button/mode.png");
    public static final Identifier INFO = id("textures/gui/button/information.png");
    public static final Identifier LOCK = id("textures/gui/button/lock.png");
    public static final Identifier LOOP = id("textures/gui/button/loop.png");
    public static final Identifier FILTER = id("textures/gui/button/filter.png");
    public static final Identifier EJECT = id("textures/gui/button/eject.png");
    public static final Identifier DARK_MODE = id("textures/gui/button/dark_mode_switch.png");
    public static final Identifier RESET = id("textures/gui/button/reset.png");
    public static final Identifier BLANK = id("textures/gui/button/blank.png");
    public static final Identifier FIELD = id("textures/gui/button/field.png");
    public static final Identifier SKIP = id("textures/gui/button/skip.png");
    public static final Identifier REPLACE = id("textures/gui/button/replace.png");

    public static final Supplier<QuarryBlock> QUARRY_BLOCK = registerBlock("quarry_block", QuarryBlock::new, QuarryItem::new);
    public static final Supplier<EnergyQuarryBlock> ENERGY_QUARRY_BLOCK = registerBlock("energy_quarry_block", EnergyQuarryBlock::new, EnergyQuarryItem::new);
    public static final Supplier<AreaCard> AREA_CARD = registerItem("area_card", properties -> new AreaCard(properties.stacksTo(1)));

    public static final Supplier<BlockEntityType<QuarryEntity>> QUARRY_ENTITY = register("quarry_block", BuiltInRegistries.BLOCK_ENTITY_TYPE,
            () -> FabricBlockEntityTypeBuilder.create(QuarryEntity::new, QUARRY_BLOCK.get()).build());
    public static final Supplier<BlockEntityType<EnergyQuarryEntity>> ENERGY_QUARRY_ENTITY = register("energy_quarry_block", BuiltInRegistries.BLOCK_ENTITY_TYPE,
            () -> FabricBlockEntityTypeBuilder.create(EnergyQuarryEntity::new, ENERGY_QUARRY_BLOCK.get()).build());

    public static final Supplier<MenuType<QuarryContainer>> QUARRY_CONTAINER = register("quarry_container", BuiltInRegistries.MENU,
            () -> new ExtendedMenuType<>((windowId, inv, pos) -> new QuarryContainer(windowId, inv, pos, inv.player.level()), BlockPos.STREAM_CODEC));
    public static final Supplier<MenuType<AreaCardContainer>> AREA_CARD_CONTAINER = register("area_card_container", BuiltInRegistries.MENU,
            () -> new ExtendedMenuType<>((windowId, inv, pos) -> new AreaCardContainer(windowId, inv, pos, inv.player.level()), BlockPos.STREAM_CODEC));

    public static final Supplier<CreativeModeTab> QUARRY_TAB = register("quarry_tab", BuiltInRegistries.CREATIVE_MODE_TAB, () -> FabricCreativeModeTab.builder()
            .title(Component.translatable("itemGroup.quarry.items"))
            .icon(() -> new ItemStack(QUARRY_BLOCK.get()))
            .displayItems((parameters, output) -> {
                output.accept(QUARRY_BLOCK.get());
                output.accept(ENERGY_QUARRY_BLOCK.get());
                output.accept(AREA_CARD.get());
            })
            .build());

    public static final DataComponentType<Integer> LAST_BLOCK = registerComponent("lastblock", Codec.INT);
    public static final DataComponentType<Integer> CURRENT_Y = registerComponent("currenty", Codec.INT);
    public static final DataComponentType<CompoundTag> POS_1 = registerComponent("pos1", CompoundTag.CODEC);
    public static final DataComponentType<CompoundTag> POS_2 = registerComponent("pos2", CompoundTag.CODEC);
    public static final DataComponentType<CompoundTag> FILTERS = registerComponent("filters", CompoundTag.CODEC);
    public static final DataComponentType<Integer> SELECTION = registerComponent("selection", Codec.INT);

    public static DataComponentType<CompoundTag> get(String text) {
        if (text.contains("pos1")) {
            return POS_1;
        } else if (text.contains("pos2")) {
            return POS_2;
        }
        return null;
    }

    @Override
    public void onInitialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(output -> output.accept(AREA_CARD.get()));
        ModEvents.register();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private static <T extends Block> Supplier<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory, BiFunction<T, Item.Properties, BlockItem> blockItemFactory) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id(name));
        T block = Registry.register(BuiltInRegistries.BLOCK, blockKey, blockFactory.apply(QuarryBlock.createProperties().setId(blockKey)));
        registerItem(name, properties -> blockItemFactory.apply(block, properties));
        return () -> block;
    }

    private static <T extends Item> Supplier<T> registerItem(String name, Function<Item.Properties, T> itemFactory) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id(name));
        T item = Registry.register(BuiltInRegistries.ITEM, itemKey, itemFactory.apply(new Item.Properties().setId(itemKey)));
        return () -> item;
    }

    private static <T> Supplier<T> register(String name, Registry<? super T> registry, Supplier<T> valueSupplier) {
        T value = Registry.register(registry, id(name), valueSupplier.get());
        return () -> value;
    }

    private static <T> DataComponentType<T> registerComponent(String name, Codec<T> codec) {
        ResourceKey<DataComponentType<?>> key = ResourceKey.create(Registries.DATA_COMPONENT_TYPE, id(name));
        DataComponentType<T> type = DataComponentType.<T>builder().persistent(codec).networkSynchronized(ByteBufCodecs.fromCodec(codec)).build();
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, key, type);
    }
}
