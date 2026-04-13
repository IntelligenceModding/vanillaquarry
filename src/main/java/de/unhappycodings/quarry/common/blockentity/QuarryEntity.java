package de.unhappycodings.quarry.common.blockentity;

import com.mojang.authlib.GameProfile;
import de.unhappycodings.quarry.Quarry;
import de.unhappycodings.quarry.common.block.QuarryBlock;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.container.QuarryContainer;
import de.unhappycodings.quarry.common.util.CalcUtil;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = Quarry.MOD_ID)
public class QuarryEntity extends BlockEntity implements MenuProvider {
    private final int SPEED_0 = 15;
    private final int SPEED_1 = 12;
    private final int SPEED_2 = 10; // 5
    private final int SPEED_3 = 8; // 2
    private final int SPEED_4 = 6; // 2
    private final int SPEED_5 = 4; // 2
    private final int SPEED_6 = 2; // 2

    public LootParams.Builder lootcontextBuilder;
    public List<BlockPos> blockStateList;
    private boolean isFortune = false;
    private boolean isSilktouch = false;
    private boolean isVoid = false;
    private FakePlayer fakePlayer;

    public Item[] filters = null;

    private String owner;
    private int burnTicks;
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

    public final ItemStackHandler inventory = new ItemStackHandler(14) {

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

    };

    private final IItemHandlerModifiable topHandler = new IItemHandlerModifiable() {
        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            inventory.setStackInSlot(slot, stack);
        }

        @Override
        public int getSlots() {
            return inventory.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inventory.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot >= 0 && slot <= 5 && isItemValid(slot, stack)) {
                return inventory.insertItem(slot, stack, simulate);
            }
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return inventory.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return inventory.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot >= 0 && slot <= 5) {
                return stack.getBurnTime(RecipeType.SMELTING) > 0;
            }

            return inventory.isItemValid(slot, stack);
        }
    };

    private final IItemHandlerModifiable downHandler = new IItemHandlerModifiable() {
        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            inventory.setStackInSlot(slot, stack);
        }

        @Override
        public int getSlots() {
            return inventory.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inventory.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return inventory.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot >= 6 && slot <= 11) {
                return inventory.extractItem(slot, amount, simulate);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return inventory.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot >= 6 && slot <= 11) {
                return stack.getBurnTime(RecipeType.SMELTING) > 0;
            }

            return inventory.isItemValid(slot, stack);
        }
    };

    private final IItemHandlerModifiable rightHandler = new IItemHandlerModifiable() {
        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            inventory.setStackInSlot(slot, stack);
        }

        @Override
        public int getSlots() {
            return inventory.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inventory.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == 13 && stack.getItem() instanceof BlockItem) {
                return inventory.insertItem(slot, stack, simulate);
            }
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return inventory.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return inventory.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 13 && stack.getItem() instanceof BlockItem;
        }
    };

    public QuarryEntity(BlockPos pos, BlockState blockState) {
        super(Quarry.QUARRY_ENTITY.get(), pos, blockState);
    }

    public void tick(Level level, BlockPos pos, BlockState state, QuarryEntity blockEntity) {

        if (fakePlayer == null) fakePlayer = FakePlayerFactory.get((ServerLevel) level, new GameProfile(UUID.fromString("6e483f02-30db-4454-b612-3a167614b576"), "vanillaquarry"));
        if (level.isClientSide) return;
        fakePlayer.tick();
        BlockState above = level.getBlockState(pos.above());
        BlockState below = level.getBlockState(pos.below());
        BlockState right = switch (state.getValue(QuarryBlock.FACING)) {
            case NORTH -> level.getBlockState(pos.west());
            case EAST -> level.getBlockState(pos.north());
            case SOUTH -> level.getBlockState(pos.east());
            default -> level.getBlockState(pos.south());
        };

        // Eject / Pull functionality
        if (getEject() > 0 && level.getGameTime() % 2 == 0) {
            boolean in = false;
            boolean out = false;
            if (level.getGameTime() % 4 == 0)
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            switch (getEject()) {
                case 1 -> in = true;
                case 2 -> out = true;
                case 3 -> {
                    in = true;
                    out = true;
                }
            }
            IItemHandler quarryCapability = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            if (quarryCapability != null) {
                if (right.hasBlockEntity()) exportImportRightSide(blockEntity.rightHandler, in, level, pos);
                if (above.hasBlockEntity()) exportImportAbove(blockEntity.topHandler, in, level, pos);
                if (below.hasBlockEntity()) exportImportBelow(blockEntity.downHandler, out, level, pos);
            }
        }

        // Refueling stuff
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
        ItemStack cardSlot = getItem(12, level, pos);
        if (cardSlot.is(Quarry.AREA_CARD.get())) {
            if (filters == null) {
                filters = new Item[27];
                updateFilters(cardSlot, filters, level);
            }
        }
        if (state.getValue(QuarryBlock.ACTIVE)) {
            level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.WORKING, false));
            if (cardSlot.is(Quarry.AREA_CARD.get())) {
                if (cardSlot.has(Quarry.POS_1) && cardSlot.has(Quarry.POS_2)) {
                    // Get Speed and set to variables
                    int speedModifier = 0;
                    level.tickRateManager().setFrozen(false);
                    boolean skipNext = speed == 0 && !(ticks + speedModifier >= SPEED_0);
                    if (speed == 1 && !(ticks + speedModifier >= SPEED_1 && ticks % 4 == 0)) skipNext = true;
                    if (speed == 2 && !(ticks + speedModifier >= SPEED_2)) skipNext = true;
                    if (speed == 3 && !(ticks + speedModifier >= SPEED_3)) skipNext = true;
                    if (speed == 4 && !(ticks + speedModifier >= SPEED_5)) skipNext = true;
                    if (speed == 5 && !(ticks + speedModifier >= SPEED_4)) skipNext = true;
                    if (speed == 6 && !(ticks + speedModifier >= SPEED_6)) skipNext = true;

                    // Get Mode to variables to work with in-code easier!
                    updateModeModifiers();

                    if (blockStateList == null || blockStateList.isEmpty()) refreshPositions(cardSlot, level);
                    float fuelModifier = CalcUtil.getNeededTicks(mode, speed);
                    if (!blockStateList.isEmpty() && burnTime > fuelModifier) {
                        if (!cardSlot.has(Quarry.LAST_BLOCK)) cardSlot.set(Quarry.LAST_BLOCK, 0);

                        // Item Data Reset And Machine Turn Off
                        int blockIndex = cardSlot.get(Quarry.LAST_BLOCK);
                        if (blockIndex > blockStateList.size() - 1) {
                            level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.WORKING, false).setValue(QuarryBlock.ACTIVE, false));
                            cardSlot.set(Quarry.LAST_BLOCK, 0);
                            if (getLoop())
                                level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.ACTIVE, true));
                            return;
                        }

                        // Checking for Invalid Blocks
                        BlockPos currentBlock = blockStateList.get(blockIndex);
                        BlockState currentBlockState = level.getBlockState(currentBlock);

                        // Skip out of range AND Skip in 1 block radius around quarry
                        int distanceX = currentBlock.getX() - pos.getX();
                        int distanceY = currentBlock.getY() - pos.getY();
                        int distanceZ = currentBlock.getZ() - pos.getZ();

                        int maxRadius = CommonConfig.quarryMineRadius.get();
                        if (((distanceX > maxRadius || distanceX < -maxRadius) || (distanceY > maxRadius || distanceY < -maxRadius) || (distanceZ > maxRadius || distanceZ < -maxRadius)) || isInNearSquare(pos, currentBlock, level)) {
                            updateCardNbt(cardSlot, blockIndex + 1, currentBlock.getY());
                            return;
                        }

                        // Checking for Speed Delay and Air Skipping
                        if (skipNext) {
                            ticks++;
                            return;
                        }

                        if (currentBlockState.getBlock() == Blocks.AIR) {
                            int tempIndex = blockIndex;
                            int index = 0;
                            for (int i = 0; i <= 32; i++) {

                                if (blockStateList.size() - 1 >= tempIndex + i && level.getBlockState(blockStateList.get(tempIndex + i)).getBlock() == Blocks.AIR) {
                                    //level.setBlockAndUpdate(blockStateList.get(tempIndex + i), Blocks.RED_WOOL.defaultBlockState()); FOR TESTING
                                    index++;
                                } else {
                                    break;
                                }
                            }

                            if (index > 0)
                                updateCardNbt(cardSlot, blockIndex + index, currentBlock.getY());

                            burnTime -= (int) fuelModifier;
                        } else {
                            // Block Drops Looping with Inventory-Space Checking and Block Breaking
                            List<ItemStack> drops = currentBlockState.getDrops(getBuilder(level, currentBlock, isSilktouch, isFortune));
                            if (drops.isEmpty()) {
                                if (allowedToBreak(currentBlockState, level, currentBlock, fakePlayer)) {
                                    setChanged(level, pos, state);
                                    level.playSound(fakePlayer, currentBlock.getX() + 0.5, currentBlock.getY() + 0.5, currentBlock.getZ() + 0.5, currentBlockState.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1f, 1f);
                                    level.setBlock(currentBlock, Blocks.AIR.defaultBlockState(), 3);
                                }
                                updateCardNbt(cardSlot, blockIndex + 1, currentBlock.getY());
                                burnTime -= (int) fuelModifier;
                                return;
                            }
                            boolean broken = false;
                            for (ItemStack drop : drops) {
                                if (isVoid) {
                                    updateCardNbt(cardSlot, blockIndex + 1, currentBlock.getY());
                                    burnTime -= (int) fuelModifier;
                                    broken = true;
                                    break;
                                }
                                setChanged(level, pos, state);
                                int index = hasOutputSpace(drop, level, pos);
                                if (index != 0) {
                                    boolean filtered = false;
                                    if (getFilter()) {
                                        for (Item item : filters) {
                                            if (drop.is(item))
                                                filtered = true;
                                        }
                                    }

                                    if (allowedToBreak(currentBlockState, level, currentBlock, fakePlayer)) {
                                        if (!filtered)
                                            insertItem(index, new ItemStack(drop.getItem(), drop.getCount()), level, pos);
                                        burnTime -= (int) fuelModifier;
                                        broken = true;
                                    }
                                    updateCardNbt(cardSlot, blockIndex + 1, currentBlock.getY());
                                    break;
                                }
                            }
                            if (broken) {
                                breakBlock(currentBlock, currentBlockState, level, pos);

                                // Enable Indicator light | Will reset next Tick
                                level.setBlockAndUpdate(pos, state.setValue(QuarryBlock.WORKING, true));

                                // Nearly fluids check -> Replace with cobblestone | If option enabled
                                if (getReplace()) {
                                    tryReplaceFluidSources(currentBlock, level);
                                }
                            }
                        }
                    } else {
                        // Machine turns off after use
                        level.setBlock(pos, state.setValue(QuarryBlock.ACTIVE, false).setValue(QuarryBlock.WORKING, false), 3);
                    }

                    ticks = 0;
                }
            } else {
                filters = null;
            }
        }

    }

    public ItemStack getItem(int slot, Level level, BlockPos pos) {

        IItemHandler capability = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);

        if (capability != null) {
            return capability.getStackInSlot(slot);
        }

        return ItemStack.EMPTY;
    }

    public ItemStack insertItem(int slot, ItemStack stack, Level level, BlockPos pos) {

        IItemHandler capability = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);

        if (capability != null) {
            return capability.insertItem(slot, stack, false);
        }

        return ItemStack.EMPTY;
    }

    public ItemStack removeItem(int slot, int amount, Level level, BlockPos pos) {

        IItemHandler capability = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);

        if (capability != null) {
            return capability.extractItem(slot, amount, false);
        }

        return ItemStack.EMPTY;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @SubscribeEvent  // on the mod event bus
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Quarry.QUARRY_ENTITY.get(), (object, context) -> {

            if (context == Direction.DOWN) return object.downHandler;
            if (context == Direction.UP) return object.topHandler;
            switch (object.getBlockState().getValue(QuarryBlock.FACING)) {
                case NORTH -> {
                    if (context == Direction.WEST) return object.rightHandler;
                }
                case EAST -> {
                    if (context == Direction.NORTH) return object.rightHandler;
                }
                case SOUTH -> {
                    if (context == Direction.EAST) return object.rightHandler;
                }
                default -> {
                    if (context == Direction.SOUTH) return object.rightHandler;
                }
            }
            if (context == null)
                return object.inventory;
            return null;
        });

    }

    private void exportImportRightSide(IItemHandler quarryHandler, boolean input, Level level, BlockPos pos) {
        if (level == null) return;

        BlockEntity tileRight = switch (level.getBlockState(pos).getValue(QuarryBlock.FACING)) {
            case NORTH -> level.getBlockEntity(pos.west());
            case EAST -> level.getBlockEntity(pos.north());
            case SOUTH -> level.getBlockEntity(pos.east());
            default -> level.getBlockEntity(pos.south());
        };

        if (tileRight != null) {
            IItemHandler capabilityRight = level.getCapability(Capabilities.ItemHandler.BLOCK, tileRight.getBlockPos(), Direction.DOWN);
            if (input && capabilityRight != null) {
                for (int i = 0; i < capabilityRight.getSlots(); i++) {
                    ItemStack stack = capabilityRight.getStackInSlot(i);
                    if (!(stack.getItem() instanceof BlockItem)) continue;
                    if (quarryHandler.getStackInSlot(13).is(stack.getItem()) || quarryHandler.getStackInSlot(13).is(Items.AIR)) {
                        if (quarryHandler.getStackInSlot(13).getCount() < quarryHandler.getStackInSlot(13).getMaxStackSize()) {
                            quarryHandler.insertItem(13, new ItemStack(stack.getItem(), 1), false);
                            capabilityRight.extractItem(i, 1, false);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void exportImportAbove(IItemHandler quarryHandler, boolean input, Level level, BlockPos pos) {
        if (level == null) return;

        BlockEntity tileAbove = level.getBlockEntity(pos.above());
        if (tileAbove != null) {
            IItemHandler capabilityAbove = level.getCapability(Capabilities.ItemHandler.BLOCK, tileAbove.getBlockPos(), Direction.DOWN);
            if (input && capabilityAbove != null) {
                for (int i = 0; i < capabilityAbove.getSlots(); i++) {
                    ItemStack stack = capabilityAbove.getStackInSlot(i);
                    if (QuarryContainer.burnables.contains(stack.getItem())) {
                        int slot = hasInputSpace(new ItemStack(stack.getItem(), 1), level, pos);
                        if (slot != -1 && slot != 99) {
                            quarryHandler.insertItem(slot, new ItemStack(stack.getItem(), 1), false);
                            capabilityAbove.extractItem(i, 1, false);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void exportImportBelow(IItemHandler quarryHandler, boolean output, Level level, BlockPos pos) {
        if (level == null) return;

        BlockEntity tileBelow = level.getBlockEntity(pos.below());
        if (tileBelow != null) {
            IItemHandler capabilityBelow = level.getCapability(Capabilities.ItemHandler.BLOCK, tileBelow.getBlockPos(), Direction.UP);
            if (output && capabilityBelow != null) {
                boolean doBreak = false;
                for (int i = 6; i <= 11; i++) {
                    ItemStack stack = quarryHandler.getStackInSlot(i);
                    for (int e = 0; e < capabilityBelow.getSlots(); e++) {
                        ItemStack slotStack = capabilityBelow.getStackInSlot(e);
                        if (!stack.is(Items.AIR)) {
                            if (slotStack.isEmpty() || new ItemStack(stack.getItem(), 1).is(slotStack.getItem())) {
                                if ((slotStack.getCount() + 1) <= stack.getMaxStackSize()) {
                                    capabilityBelow.insertItem(e, new ItemStack(stack.getItem(), 1), false);
                                    quarryHandler.extractItem(i, 1, false);
                                    doBreak = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (doBreak) break;
                }
            }
        }
    }

    public void updateFilters(ItemStack cardSlot, Item[] filters, Level level) {
        CompoundTag currentTag = cardSlot.get(Quarry.FILTERS);

        for (int i = 0; i < 27; i++) {
            if (currentTag != null && currentTag.contains(i + "")) {
                filters[i] = ItemStack.parse(level.registryAccess(), currentTag.getCompound(i + "")).get().getItem();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void refuelQuarry(List<ItemStack> input, Level level, BlockPos pos) {
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).getBurnTime(RecipeType.SMELTING) > 0) {
                Item stack = input.get(i).getItem();
                if (stack.hasCraftingRemainingItem()) {
                    Item remainItem = stack.getCraftingRemainingItem();
                    int output = hasOutputSpace(new ItemStack(remainItem, 1), level, pos);
                    if (output == 0) return;
                    if (output != 99)
                        insertItem(output, new ItemStack(remainItem, 1), level, pos);
                }
                totalBurnTime = burnTime + input.get(i).getBurnTime(RecipeType.SMELTING);
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

        level.playSound(fakePlayer, currentBlock.getX() + 0.5, currentBlock.getY() + 0.5, currentBlock.getZ() + 0.5, currentBlockState.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1f, 1f);
        if (getItem(13, level, pos).getItem() instanceof BlockItem blockItem) {

            removeItem(13, 1, level, pos);
            level.playSound(fakePlayer, currentBlock.getX() + 0.5, currentBlock.getY() + 0.5, currentBlock.getZ() + 0.5, blockItem.getBlock().defaultBlockState().getSoundType().getBreakSound(), SoundSource.BLOCKS, 1f, 1f);
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
                level.playSound(fakePlayer, currentBlock.getX() + 0.5, currentBlock.getY() + 0.5, currentBlock.getZ() + 0.5, Blocks.COBBLESTONE.defaultBlockState().getSoundType().getBreakSound(), SoundSource.BLOCKS, 1f, 1f);
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
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, state, player);
        NeoForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // Optionally: Run some custom logic when the packet is received.
    // The super/default implementation forwards to #loadAdditional.
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        super.onDataPacket(connection, packet, registries);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        nbt.putInt("BurnTime", getBurnTime());
        nbt.putInt("TotalBurnTime", getTotalBurnTime());
        nbt.putInt("Speed", getSpeed());
        nbt.putInt("Mode", getMode());
        nbt.putInt("Eject", getEject());
        nbt.putString("Owner", getOwner());
        nbt.putBoolean("Locked", getLocked());
        nbt.putBoolean("Filter", getFilter());
        nbt.putBoolean("Loop", getLoop());
        nbt.putBoolean("Skip", getSkip());
        nbt.putBoolean("Replace", getReplace());
        nbt.put("Items", this.inventory.serializeNBT(registries));
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        setBurnTime(tag.getInt("BurnTime"));
        setTotalBurnTime(tag.getInt("TotalBurnTime"));
        setSpeed(tag.getInt("Speed"));
        setMode(tag.getInt("Mode"));
        setEject(tag.getInt("Eject"));
        setOwner(tag.getString("Owner"));
        setLocked(tag.getBoolean("Locked"));
        setFilter(tag.getBoolean("Filter"));
        setLoop(tag.getBoolean("Loop"));
        setSkip(tag.getBoolean("Skip"));
        setReplace(tag.getBoolean("Replace"));
        this.inventory.deserializeNBT(lookupProvider, tag.getCompound("Items"));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putInt("Speed", speed);
        nbt.putInt("Mode", mode);
        nbt.putInt("Eject", eject);
        nbt.putInt("BurnTime", burnTime);
        nbt.putInt("TotalBurnTime", totalBurnTime);
        nbt.putString("Owner", getOwner());
        nbt.putBoolean("Locked", getLocked());
        nbt.putBoolean("Filter", getFilter());
        nbt.putBoolean("Loop", getLoop());
        nbt.putBoolean("Skip", getSkip());
        nbt.putBoolean("Replace", getReplace());
        nbt.put("Items", this.inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        speed = nbt.getInt("Speed");
        mode = nbt.getInt("Mode");
        eject = nbt.getInt("Eject");
        burnTime = nbt.getInt("BurnTime");
        totalBurnTime = nbt.getInt("TotalBurnTime");
        owner = nbt.getString("Owner");
        locked = nbt.getBoolean("Locked");
        filter = nbt.getBoolean("Filter");
        loop = nbt.getBoolean("Loop");
        skip = nbt.getBoolean("Skip");
        replace = nbt.getBoolean("Replace");
        this.inventory.deserializeNBT(registries, nbt.getCompound("Items"));
    }

    public LootParams.Builder getBuilder(Level level, BlockPos pos, boolean isSilktouch, boolean isFortune) {
        ItemStack stack = new ItemStack(Items.STICK);
        if (isSilktouch) stack.enchant(getEnchantment(level, Enchantments.SILK_TOUCH), 1);
        if (isFortune) stack.enchant(getEnchantment(level, Enchantments.FORTUNE), 3);

        lootcontextBuilder = (new LootParams.Builder((ServerLevel) level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, stack).withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));
        return lootcontextBuilder;
    }

    public Holder<Enchantment> getEnchantment(Level level, ResourceKey<Enchantment> key) {
        return level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(key);
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

}
