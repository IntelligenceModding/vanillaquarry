package de.unhappycodings.quarry.common.blockentity;

import com.mojang.authlib.GameProfile;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.block.QuarryBlock;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.container.QuarryContainer;
import de.unhappycodings.quarry.common.util.CalcUtil;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.ProblemReporter;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.IntPredicate;

@EventBusSubscriber(modid = Quarry.MOD_ID)
public class QuarryEntity extends BlockEntity implements MenuProvider {
    public final SimpleContainer inventory = new SimpleContainer(14) {

        @Override
        public void setChanged() {
            QuarryEntity.this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

    };
    private final int SPEED_0 = 15;
    private final int SPEED_1 = 12;
    private final int SPEED_2 = 10; // 5
    private final int SPEED_3 = 8; // 2
    private final int SPEED_4 = 6; // 2
    private final int SPEED_5 = 4; // 2
    private final int SPEED_6 = 2; // 2
    protected final ResourceHandler<ItemResource> topResourceHandler = new ItemContainerResourceAdapter(slot -> true, this::canInsertFuel);
    protected final ResourceHandler<ItemResource> downResourceHandler = new ItemContainerResourceAdapter(slot -> slot >= 6 && slot <= 11, (slot, stack) -> false);
    protected final ResourceHandler<ItemResource> rightResourceHandler = new ItemContainerResourceAdapter(slot -> true, (slot, stack) -> slot == 13 && stack.getItem() instanceof BlockItem);
    public LootParams.Builder lootcontextBuilder;
    public List<BlockPos> blockStateList;
    public Item[] filters = null;
    private boolean isFortune = false;
    private boolean isSilktouch = false;
    private boolean isVoid = false;
    private FakePlayer fakePlayer;
    private String owner;
    protected int burnTicks;
    private int ticks;
    private int speed;
    private int mode;
    private int eject;
    private boolean filter;
    private boolean loop;
    private boolean locked;
    private boolean skip;
    private boolean replace;
    private int burnTime;
    private int totalBurnTime;
    public boolean outOfRange;
    public boolean inventoryFull;
    public boolean skippingAir;

    public QuarryEntity(BlockPos pos, BlockState blockState) {
        this(Quarry.QUARRY_ENTITY.get(), pos, blockState);
    }

    protected QuarryEntity(BlockEntityType<? extends QuarryEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @SubscribeEvent  // on the mod event bus
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, Quarry.QUARRY_ENTITY.get(), QuarryEntity::getSidedResourceHandler);
        event.registerBlockEntity(Capabilities.Item.BLOCK, Quarry.FE_QUARRY_ENTITY.get(), QuarryEntity::getSidedResourceHandler);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, Quarry.FE_QUARRY_ENTITY.get(), FEQuarryEntity::getEnergyHandler);
    }

    public void tick(Level level, BlockPos pos, BlockState state, QuarryEntity blockEntity) {
        if (level.isClientSide()) return;

        // Fakeplayer handling
        initFakePlayer(level);

        // Eject / Pull functionality
        if (getEject() > 0 && level.getGameTime() % 2 == 0) handleEjectPull(pos, state, blockEntity);

        // Power handling
        handlePower(pos, state);

        // Filter Updating
        ItemStack cardSlot = getItem(12, level, pos);
        handleFilterUpdate(cardSlot, pos);

        // Core Logic
        if (!state.getValue(QuarryBlock.ACTIVE)) return;
        fakePlayer.tick();

        if (state.getValue(QuarryBlock.WORKING))
            level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.WORKING, false));
        if (!cardSlot.is(Quarry.AREA_CARD.get()) || (!cardSlot.has(Quarry.POS_1) && !cardSlot.has(Quarry.POS_2))) {
            filters = null;
            return;
        }

        // Get Mode to variables to work with in-code easier!
        updateModeModifiers();

        if (blockStateList == null || blockStateList.isEmpty()) refreshPositions(cardSlot, level);
        float fuelModifier = CalcUtil.getNeededTicks(mode, speed, isEnergyPowered());

        if (!blockStateList.isEmpty() && hasPowerFor(fuelModifier)) {
            if (!cardSlot.has(Quarry.LAST_BLOCK)) cardSlot.set(Quarry.LAST_BLOCK, 0);

            // If card end reached -> Item data reset and machine turn Off
            int blockIndex = cardSlot.get(Quarry.LAST_BLOCK);
            if (blockIndex > blockStateList.size() - 1) {
                level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.WORKING, false).setValue(QuarryBlock.ACTIVE, false));
                cardSlot.set(Quarry.LAST_BLOCK, 0);
                if (getLoop()) level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.ACTIVE, true));
                return;
            }

            // Skip current tick if quarry's speed is lower
            if (handleSpeedCalculations()) {
                ticks++;
                return;
            }

            // Check if block is within protection radius of quarry (1 block square) and skip if
            BlockPos currentBlock = blockStateList.get(blockIndex);
            if (isOutOfRangeOrInProtection(currentBlock, pos, cardSlot, blockIndex)) {
                outOfRange = true;
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                return;
            } else {
                outOfRange = false;
            }

            // Check if current block is air
            BlockState currentBlockState = level.getBlockState(currentBlock);
            if (currentBlockState.getBlock() == Blocks.AIR) {
                skippingAir = true;
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                handleAirBlock(blockIndex, cardSlot, currentBlock, fuelModifier);

            } else {
                skippingAir = false;

                // Block drops looping with inventory-space checking and block breaking
                List<ItemStack> drops = currentBlockState.getDrops(getBuilder(level, currentBlock, isSilktouch, isFortune));
                if (handleDropsEmptyCheck(drops, currentBlockState, currentBlock, pos, state, blockIndex, fuelModifier, cardSlot))
                    return;

                // Checks if block can be theoretically "broken" (Checking inventory space, void mode, chunk protection and filters)
                if (handleBlockAndDropCheck(drops, cardSlot, blockIndex, currentBlock, fuelModifier, pos, state, currentBlockState))
                    handleBlockBreak(pos, state, level, currentBlock, currentBlockState); // Breaking block and replacing nearby fluid sources if enabled

            }
        } else {
            // Machine turns off after use
            level.setBlock(pos, state.setValue(QuarryBlock.ACTIVE, false).setValue(QuarryBlock.WORKING, false), 3);
        }

        ticks = 0;
    }

    private void initFakePlayer(Level level) {
        if (fakePlayer != null) return;
        if (!(level instanceof ServerLevel sl)) return;

        fakePlayer = FakePlayerFactory.get(sl, new GameProfile(UUID.fromString("6e483f02-30db-4454-b612-3a167614b576"), "vanillaquarry"));
    }

    private BlockState getRightBlock(Level level, BlockPos pos, BlockState state) {
        return switch (state.getValue(QuarryBlock.FACING)) {
            case NORTH -> level.getBlockState(pos.west());
            case EAST -> level.getBlockState(pos.north());
            case SOUTH -> level.getBlockState(pos.east());
            default -> level.getBlockState(pos.south());
        };
    }

    private void handleEjectPull(BlockPos pos, BlockState state, QuarryEntity blockEntity) {
        final long gameTime = level.getGameTime();
        BlockState above = level.getBlockState(pos.above());
        BlockState below = level.getBlockState(pos.below());
        BlockState right = getRightBlock(level, pos, state);

        boolean in = false;
        boolean out = false;
        if (gameTime % 4 == 0) level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);

        switch (getEject()) {
            case 1 -> in = true;
            case 2 -> out = true;
            case 3 -> {
                in = true;
                out = true;
            }
        }
        if (above.hasBlockEntity()) exportImportAbove(blockEntity.topResourceHandler, in, level, pos);
        if (below.hasBlockEntity()) exportImportBelow(blockEntity.downResourceHandler, out, level, pos);
        if (right.hasBlockEntity()) exportImportRightSide(blockEntity.rightResourceHandler, in, level, pos);

    }

    private void handleFilterUpdate(ItemStack cardSlot, BlockPos pos) {
        if (cardSlot.is(Quarry.AREA_CARD.get())) {
            if (filters == null) {
                filters = new Item[27];
                updateFilters(cardSlot, filters, level);
            }
        }
    }

    protected void handlePower(BlockPos pos, BlockState state) {
        handleRefueling(pos, state);
    }

    private void handleRefueling(BlockPos pos, BlockState state) {
        List<ItemStack> input = new ArrayList<>();
        for (int i = 0; i <= 5; i++)
            input.add(getItem(i, level, pos));
        if (!input.isEmpty() && burnTime <= 1001) {
            refuelQuarry(input, level, pos);
        }

        if (burnTime > 0 != state.getValue(QuarryBlock.POWERED))
            level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.POWERED, burnTime > 0));
        if (!state.getValue(QuarryBlock.POWERED) && state.getValue(QuarryBlock.ACTIVE))
            level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.ACTIVE, burnTime > 0));
        if (!state.getValue(QuarryBlock.ACTIVE) && state.getValue(QuarryBlock.WORKING))
            level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.POWERED, burnTime > 0).setValue(QuarryBlock.WORKING, false));

        if (burnTime >= CommonConfig.quarryIdleConsumption.get() && burnTicks >= 20 && !state.getValue(QuarryBlock.ACTIVE)) {
            burnTime -= CommonConfig.quarryIdleConsumption.get();
            burnTicks = 0;
        }

        burnTicks++;
    }

    private boolean handleDropsEmptyCheck(List<ItemStack> drops, BlockState currentBlockState, BlockPos currentBlock, BlockPos pos, BlockState state, int blockIndex, float fuelModifier, ItemStack cardSlot) {
        if (drops.isEmpty()) {
            if (allowedToBreak(currentBlockState, level, currentBlock, fakePlayer)) {
                setChanged(level, pos, state);
                level.playSound(fakePlayer, currentBlock.getX() + 0.5, currentBlock.getY() + 0.5, currentBlock.getZ() + 0.5, currentBlockState.getSoundType(level, currentBlock, fakePlayer).getBreakSound(), SoundSource.BLOCKS, 1f, 1f);
                level.setBlock(currentBlock, Blocks.AIR.defaultBlockState(), 3);
            }
            updateCardNbt(cardSlot, blockIndex + 1, currentBlock.getY());
            consumePower((int) fuelModifier);
            return true;
        }
        return false;
    }

    private void handleBlockBreak(BlockPos pos, BlockState state, Level level, BlockPos currentBlock, BlockState currentBlockState) {
        breakBlock(currentBlock, currentBlockState, level, pos);

        // Enable indicator light | Will reset next tick
        level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.WORKING, true));

        // Nearly fluids check -> Replace with cobblestone | If option enabled
        if (getReplace()) tryReplaceFluidSources(currentBlock, level);
    }

    private void handleAirBlock(int blockIndex, ItemStack cardSlot, BlockPos currentBlock, float fuelModifier) {
        int tempIndex = blockIndex;
        int index = 0;
        for (int i = 0; i <= 32; i++) {

            if (blockStateList.size() - 1 >= tempIndex + i && level.getBlockState(blockStateList.get(tempIndex + i)).getBlock() == Blocks.AIR) {
                index++;
            } else {
                break;
            }
        }

        if (index > 0) updateCardNbt(cardSlot, blockIndex + index, currentBlock.getY());

        consumePower((int) fuelModifier);
    }

    private boolean isOutOfRangeOrInProtection(BlockPos currentBlock, BlockPos pos, ItemStack cardSlot, int blockIndex) {
        int distanceX = currentBlock.getX() - pos.getX();
        int distanceY = currentBlock.getY() - pos.getY();
        int distanceZ = currentBlock.getZ() - pos.getZ();

        int maxRadius = CommonConfig.quarryMineRadius.get();
        if (((distanceX > maxRadius || distanceX < -maxRadius) || (distanceY > maxRadius || distanceY < -maxRadius) || (distanceZ > maxRadius || distanceZ < -maxRadius)) || isInNearSquare(pos, currentBlock, level)) {
            updateCardNbt(cardSlot, blockIndex + 1, currentBlock.getY());
            return true;
        }
        return false;
    }

    private boolean handleBlockAndDropCheck(List<ItemStack> drops, ItemStack cardSlot, int blockIndex, BlockPos currentBlock, float fuelModifier, BlockPos pos, BlockState state, BlockState currentBlockState) {
        boolean blockBroken = false;
        for (ItemStack drop : drops) {
            if (isVoid) {
                updateCardNbt(cardSlot, blockIndex + 1, currentBlock.getY());
                consumePower((int) fuelModifier);
                blockBroken = true;
                inventoryFull = false;
                break;
            }
            setChanged(level, pos, state);
            int index = hasOutputSpace(drop, level, pos);
            if (index != 0) {
                inventoryFull = false;
                boolean filtered = false;
                if (getFilter()) {
                    for (Item item : filters) {
                        if (drop.is(item)) {
                            filtered = true;
                            break;
                        }
                    }
                }

                if (allowedToBreak(currentBlockState, level, currentBlock, fakePlayer)) {
                    if (!filtered) insertItem(index, new ItemStack(drop.getItem(), drop.getCount()), level, pos);
                    consumePower((int) fuelModifier);
                    blockBroken = true;
                }
                updateCardNbt(cardSlot, blockIndex + 1, currentBlock.getY());
                break;
            } else {
                if (!inventoryFull) {
                    inventoryFull = true;
                    level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                }
            }
        }
        return blockBroken;
    }

    private boolean handleSpeedCalculations() {
        int modifiedTick = ticks + (getMode() == 1 ? (int) Math.ceil((float) getTicksForSpeed(speed) * 0.2f) * -1 : 0); // Remove 20% speed if efficient mode
        return switch (speed) {
            case 0 -> modifiedTick < SPEED_0 - 1;
            case 1 -> modifiedTick < SPEED_1 - 1;
            case 2 -> modifiedTick < SPEED_2 - 1;
            case 3 -> modifiedTick < SPEED_3 - 1;
            case 4 -> modifiedTick < SPEED_4 - 1;
            case 5 -> modifiedTick < SPEED_5 - 1;
            case 6 -> modifiedTick < SPEED_6 - 1;
            default -> true;
        };
    }

    public int getTicksForSpeed(int speed) {
        return switch (speed) {
            case 0 -> SPEED_0;
            case 1 -> SPEED_1;
            case 2 -> SPEED_2;
            case 3 -> SPEED_3;
            case 4 -> SPEED_4;
            case 5 -> SPEED_5;
            case 6 -> SPEED_6;
            default -> 0;
        };
    }

    public ItemStack getItem(int slot, Level level, BlockPos pos) {

        return inventory.getItem(slot);
    }

    public ItemStack insertItem(int slot, ItemStack stack, Level level, BlockPos pos) {

        return insertIntoSlot(slot, stack);
    }

    public ItemStack removeItem(int slot, int amount, Level level, BlockPos pos) {

        return inventory.removeItem(slot, amount);
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    private ItemStack insertIntoSlot(int slot, ItemStack stack) {
        if (stack.isEmpty() || slot < 0 || slot >= inventory.getContainerSize()) return stack;

        ItemStack current = inventory.getItem(slot);
        int limit = inventory.getMaxStackSize(stack);
        if (current.isEmpty()) {
            int inserted = Math.min(stack.getCount(), limit);
            inventory.setItem(slot, stack.copyWithCount(inserted));
            ItemStack remainder = stack.copy();
            remainder.shrink(inserted);
            return remainder;
        }

        if (!ItemStack.isSameItemSameComponents(current, stack)) return stack;

        int inserted = Math.min(stack.getCount(), limit - current.getCount());
        if (inserted <= 0) return stack;

        current.grow(inserted);
        inventory.setChanged();
        ItemStack remainder = stack.copy();
        remainder.shrink(inserted);
        return remainder;
    }

    private boolean canInsertFuel(int slot, ItemStack stack) {
        return usesFuelItems() && slot >= 0 && slot <= 5 && level != null && stack.getBurnTime(RecipeType.SMELTING, level.fuelValues()) > 0;
    }

    private void exportImportRightSide(ResourceHandler<ItemResource> quarryHandler, boolean input, Level level, BlockPos pos) {
        if (!input) return;

        Direction quarrySide = getRightSide(level.getBlockState(pos));
        BlockPos sourcePos = pos.relative(quarrySide);
        ResourceHandler<ItemResource> sourceHandler = level.getCapability(Capabilities.Item.BLOCK, sourcePos, quarrySide.getOpposite());
        moveFirst(sourceHandler, quarryHandler);
    }

    private void exportImportAbove(ResourceHandler<ItemResource> quarryHandler, boolean input, Level level, BlockPos pos) {
        if (!input) return;

        ResourceHandler<ItemResource> sourceHandler = level.getCapability(Capabilities.Item.BLOCK, pos.above(), Direction.DOWN);
        moveFirst(sourceHandler, quarryHandler);
    }

    private void exportImportBelow(ResourceHandler<ItemResource> quarryHandler, boolean output, Level level, BlockPos pos) {
        if (!output) return;

        ResourceHandler<ItemResource> targetHandler = level.getCapability(Capabilities.Item.BLOCK, pos.below(), Direction.UP);
        moveFirst(quarryHandler, targetHandler);
    }

    private void moveFirst(ResourceHandler<ItemResource> source, ResourceHandler<ItemResource> target) {
        if (source == null || target == null) return;

        try (Transaction transaction = Transaction.openRoot()) {
            if (ResourceHandlerUtil.moveFirstStacking(source, target, resource -> !resource.isEmpty(), 1, transaction) != null) {
                transaction.commit();
            }
        }
    }

    public void updateFilters(ItemStack cardSlot, Item[] filters, Level level) {
        CompoundTag currentTag = cardSlot.get(Quarry.FILTERS);

        for (int i = 0; i < 27; i++) {
            if (currentTag != null && currentTag.contains(i + "")) {
                filters[i] = currentTag.read(i + "", ItemStack.CODEC).orElse(ItemStack.EMPTY).getItem();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void refuelQuarry(List<ItemStack> input, Level level, BlockPos pos) {
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).getBurnTime(RecipeType.SMELTING, level.fuelValues()) > 0) {
                Item stack = input.get(i).getItem();
                ItemStackTemplate craftingRemainder = stack.getCraftingRemainder();
                if (craftingRemainder != null) {
                    ItemStack remainStack = craftingRemainder.create();
                    if (!remainStack.isEmpty()) {
                        Item remainItem = remainStack.getItem();
                        int output = hasOutputSpace(new ItemStack(remainItem, 1), level, pos);
                        if (output == 0) return;
                        if (output != 99) insertItem(output, new ItemStack(remainItem, 1), level, pos);
                    }
                }
                totalBurnTime = burnTime + input.get(i).getBurnTime(RecipeType.SMELTING, level.fuelValues());
                burnTime = totalBurnTime;
                removeItem(i, 1, level, pos);
                break;
            }
        }
    }

    private void updateModeModifiers() {
        switch (mode) {
            case 1 -> {
                isFortune = false;
                isSilktouch = false;
                isVoid = false;
            } // Efficient
            case 2 -> {
                isFortune = true;
                isSilktouch = false;
                isVoid = false;
            } // Fortune
            case 3 -> {
                isFortune = false;
                isSilktouch = true;
                isVoid = false;
            } // Silktouch
            case 4 -> {
                isFortune = false;
                isSilktouch = false;
                isVoid = true;
            } // Void
            default -> {
                isFortune = false;
                isSilktouch = false;
                isVoid = false;
            } // Default
        }
    }

    private void updateCardNbt(ItemStack card, int blockIndex, int currentBlock) {

        card.set(Quarry.LAST_BLOCK, blockIndex);
        card.set(Quarry.CURRENT_Y, currentBlock);
    }

    private void breakBlock(BlockPos currentBlock, BlockState currentBlockState, Level level, BlockPos pos) {
        if (level == null) return;

        level.playSound(fakePlayer, currentBlock.getX() + 0.5, currentBlock.getY() + 0.5, currentBlock.getZ() + 0.5, currentBlockState.getSoundType(level, currentBlock, fakePlayer).getBreakSound(), SoundSource.BLOCKS, 1f, 1f);
        if (getItem(13, level, pos).getItem() instanceof BlockItem blockItem) {

            removeItem(13, 1, level, pos);
            level.playSound(fakePlayer, currentBlock.getX() + 0.5, currentBlock.getY() + 0.5, currentBlock.getZ() + 0.5, blockItem.getBlock().defaultBlockState().getSoundType(level, currentBlock, fakePlayer).getBreakSound(), SoundSource.BLOCKS, 1f, 1f);
            level.setBlock(currentBlock, blockItem.getBlock().defaultBlockState(), Block.UPDATE_ALL);
        } else {
            level.levelEvent(fakePlayer, 2001, currentBlock, Block.getId(currentBlockState));
            level.setBlock(currentBlock, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    private void tryReplaceFluidSources(BlockPos currentBlock, Level level) {
        if (level == null) return;

        BlockPos[] positions = {currentBlock.north(), currentBlock.east(), currentBlock.south(), currentBlock.west(), currentBlock.above(), currentBlock.below()};
        for (BlockPos pos : positions) {
            if (level.getBlockState(pos).getFluidState().isSource() && !level.getBlockState(pos).hasProperty(BlockStateProperties.WATERLOGGED)) {
                level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
                level.playSound(fakePlayer, currentBlock.getX() + 0.5, currentBlock.getY() + 0.5, currentBlock.getZ() + 0.5, Blocks.COBBLESTONE.defaultBlockState().getSoundType(level, currentBlock, fakePlayer).getBreakSound(), SoundSource.BLOCKS, 1f, 1f);
            }
        }
    }

    public boolean isInNearSquare(BlockPos origin, BlockPos target, Level level) {
        BlockPos pos1 = origin.offset(-1, -1, -1);
        BlockPos pos2 = origin.offset(1, 1, 1);
        return CalcUtil.getBlockStates(pos1, pos2, level).contains(target);
    }

    public int hasInputSpace(ItemStack itemStack, Level level, BlockPos pos) {
        for (int i = 0; i <= 5; i++) {
            ItemStack current = getItem(i, level, pos);
            if (itemStack.is(Items.AIR)) return 0;
            if (current.isEmpty()) return i;
            if (current.getItem() == itemStack.getItem()) {
                if (current.getCount() + itemStack.getCount() <= current.getMaxStackSize()) return i;
            }
        }
        return -1;
    }

    public int hasOutputSpace(ItemStack itemStack, Level level, BlockPos pos) {
        for (int i = 6; i <= 11; i++) {
            ItemStack current = getItem(i, level, pos);
            if (itemStack.is(Items.AIR)) return 0;
            if (current.isEmpty()) return i;
            if (current.getItem() == itemStack.getItem()) {
                if (current.getCount() + itemStack.getCount() <= current.getMaxStackSize()) return i;
            }
        }
        return 0;
    }

    private boolean allowedToBreak(BlockState state, Level level, BlockPos pos, Player player) {
        if (level == null) return false;

        if (!state.getBlock().canEntityDestroy(state, level, pos, player) || state.getDestroySpeed(level, pos) == -1)
            return false;
        BreakBlockEvent event = new BreakBlockEvent(level, pos, state, player);
        NeoForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        saveAdditional(output);
        return output.buildResult();
    }

    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        loadAdditional(TagValueInput.create(ProblemReporter.DISCARDING, lookupProvider, tag));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        writeData(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        readData(input);
    }

    private void writeData(ValueOutput output) {
        output.putInt("Speed", speed);
        output.putInt("Mode", mode);
        output.putInt("Eject", eject);
        output.putInt("BurnTime", burnTime);
        output.putInt("TotalBurnTime", totalBurnTime);
        output.putString("Owner", getOwner());
        output.putBoolean("Locked", getLocked());
        output.putBoolean("Filter", getFilter());
        output.putBoolean("Loop", getLoop());
        output.putBoolean("Skip", getSkip());
        output.putBoolean("Replace", getReplace());
        output.putBoolean("OutOfRange", outOfRange);
        output.putBoolean("InventoryFull", inventoryFull);
        output.putBoolean("SkippingAir", skippingAir);
        ContainerHelper.saveAllItems(output.child("Items"), inventory.getItems());
    }

    private void readData(ValueInput input) {
        speed = input.getIntOr("Speed", 0);
        mode = input.getIntOr("Mode", 0);
        eject = input.getIntOr("Eject", 0);
        burnTime = input.getIntOr("BurnTime", 0);
        totalBurnTime = input.getIntOr("TotalBurnTime", 0);
        owner = input.getStringOr("Owner", "undefined");
        locked = input.getBooleanOr("Locked", false);
        filter = input.getBooleanOr("Filter", false);
        loop = input.getBooleanOr("Loop", false);
        skip = input.getBooleanOr("Skip", false);
        replace = input.getBooleanOr("Replace", false);
        outOfRange = input.getBooleanOr("OutOfRange", false);
        inventoryFull = input.getBooleanOr("InventoryFull", false);
        skippingAir = input.getBooleanOr("SkippingAir", false);
        inventory.clearContent();
        ContainerHelper.loadAllItems(input.childOrEmpty("Items"), inventory.getItems());
    }

    public LootParams.Builder getBuilder(Level level, BlockPos pos, boolean isSilktouch, boolean isFortune) {
        ItemStack stack = new ItemStack(Items.STICK);
        if (isSilktouch) stack.enchant(getEnchantment(level, Enchantments.SILK_TOUCH), 1);
        if (isFortune) stack.enchant(getEnchantment(level, Enchantments.FORTUNE), 3);

        lootcontextBuilder = (new LootParams.Builder((ServerLevel) level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, stack).withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));
        return lootcontextBuilder;
    }

    public Holder<Enchantment> getEnchantment(Level level, ResourceKey<Enchantment> key) {
        return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
    }

    protected ResourceHandler<ItemResource> getSidedResourceHandler(Direction side) {
        if (side == Direction.UP) return topResourceHandler;
        if (side == Direction.DOWN) return downResourceHandler;
        if (side == getRightSide(getBlockState())) return rightResourceHandler;
        return null;
    }

    protected Direction getRightSide(BlockState state) {
        return switch (state.getValue(QuarryBlock.FACING)) {
            case NORTH -> Direction.WEST;
            case EAST -> Direction.NORTH;
            case SOUTH -> Direction.EAST;
            default -> Direction.SOUTH;
        };
    }

    public void resetPositions() {
        blockStateList = null;
        filters = null;
    }

    public void refreshPositions(ItemStack itemStack, Level level) {
        BlockPos pos1 = NbtUtil.getPos(itemStack.get(Quarry.POS_1));
        BlockPos pos2 = NbtUtil.getPos(itemStack.get(Quarry.POS_2));
        if (pos1 == null || pos2 == null) return;

        blockStateList = CalcUtil.getBlockStates(pos2, pos1, level);
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public int getTotalBurnTime() {
        return totalBurnTime;
    }

    public void setTotalBurnTime(int totalBurnTime) {
        this.totalBurnTime = totalBurnTime;
    }

    public boolean isEnergyPowered() {
        return false;
    }

    public int getEnergyStored() {
        return 0;
    }

    public int getEnergyCapacity() {
        return 0;
    }

    public long getStoredFuelTime() {
        if (level == null) return getBurnTime();

        long total = getBurnTime();
        for (int i = 0; i <= 5; i++) {
            ItemStack itemStack = getItem(i, level, getBlockPos());
            for (int stackIndex = 0; stackIndex < itemStack.getCount(); stackIndex++) {
                total += itemStack.getBurnTime(RecipeType.SMELTING, level.fuelValues());
            }
        }
        return total;
    }

    protected boolean usesFuelItems() {
        return true;
    }

    protected boolean hasPowerFor(float amount) {
        return burnTime > amount;
    }

    protected void consumePower(int amount) {
        burnTime = Math.max(0, burnTime - amount);
    }

    public String getOwner() {
        return owner == null ? "undefined" : owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean getLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getEject() {
        return eject;
    }

    public void setEject(int eject) {
        this.eject = eject;
    }

    public boolean getFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public boolean getLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean getSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean getReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Quarry");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new QuarryContainer(containerId, playerInventory, getBlockPos(), level);
    }

    private class ItemContainerResourceAdapter implements ResourceHandler<ItemResource> {
        private final IntPredicate canExtract;
        private final BiPredicate<Integer, ItemStack> canInsert;
        private final SnapshotJournal<List<ItemStack>> journal = new SnapshotJournal<>() {
            @Override
            protected List<ItemStack> createSnapshot() {
                List<ItemStack> snapshot = new ArrayList<>(inventory.getContainerSize());
                for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                    snapshot.add(inventory.getItem(slot).copy());
                }
                return snapshot;
            }

            @Override
            protected void revertToSnapshot(List<ItemStack> snapshot) {
                for (int slot = 0; slot < snapshot.size(); slot++) {
                    inventory.setItem(slot, snapshot.get(slot).copy());
                }
            }
        };

        private ItemContainerResourceAdapter(IntPredicate canExtract, BiPredicate<Integer, ItemStack> canInsert) {
            this.canExtract = canExtract;
            this.canInsert = canInsert;
        }

        @Override
        public int size() {
            return inventory.getContainerSize();
        }

        @Override
        public ItemResource getResource(int slot) {
            ItemStack stack = inventory.getItem(slot);
            return stack.isEmpty() ? ItemResource.EMPTY : ItemResource.of(stack);
        }

        @Override
        public long getAmountAsLong(int slot) {
            return inventory.getItem(slot).getCount();
        }

        @Override
        public long getCapacityAsLong(int slot, ItemResource resource) {
            if (!resource.isEmpty() && !isValid(slot, resource)) return 0;

            if (resource.isEmpty()) return inventory.getMaxStackSize();
            return inventory.getMaxStackSize(resource.toStack());
        }

        @Override
        public boolean isValid(int slot, ItemResource resource) {
            return !resource.isEmpty() && slot >= 0 && slot < inventory.getContainerSize() && canInsert.test(slot, resource.toStack());
        }

        @Override
        public int insert(int slot, ItemResource resource, int amount, TransactionContext transaction) {
            if (resource.isEmpty() || amount <= 0 || !isValid(slot, resource)) return 0;

            return runInTransaction(transaction, () -> {
                ItemStack stack = resource.toStack(amount);
                ItemStack remainder = insertIntoSlot(slot, stack);
                return amount - remainder.getCount();
            });
        }

        @Override
        public int extract(int slot, ItemResource resource, int amount, TransactionContext transaction) {
            if (resource.isEmpty() || amount <= 0) return 0;
            if (slot < 0 || slot >= inventory.getContainerSize() || !canExtract.test(slot)) return 0;

            ItemStack current = inventory.getItem(slot);
            if (current.isEmpty() || !resource.matches(current)) return 0;

            return runInTransaction(transaction, () -> inventory.removeItem(slot, amount).getCount());
        }

        private int runInTransaction(TransactionContext transaction, IntSupplier action) {
            if (transaction == null) {
                return action.getAsInt();
            }

            journal.updateSnapshots(transaction);
            return action.getAsInt();
        }
    }

    private interface IntSupplier {
        int getAsInt();
    }

}
