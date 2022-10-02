package de.unhappycodings.quarry.common.blockentity;

import com.mojang.authlib.GameProfile;
import de.unhappycodings.quarry.common.blocks.QuarryBlock;
import de.unhappycodings.quarry.common.config.CommonConfig;
import de.unhappycodings.quarry.common.container.QuarryContainer;
import de.unhappycodings.quarry.common.item.ModItems;
import de.unhappycodings.quarry.common.util.CalcUtil;
import de.unhappycodings.quarry.common.util.NbtUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuarryBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, MenuProvider {
    private static final int SPEED_0 = 15;
    private static final int SPEED_1 = 10;
    private static final int SPEED_2 = 0; // 5
    private final LazyOptional<? extends IItemHandler>[] itemHandler = SidedInvWrapper.create(this, Direction.values());
    public LootContext.Builder lootcontextBuilder;
    public List<BlockPos> blockStateList;
    public NonNullList<ItemStack> items;
    private int speedModifier = 0;
    private boolean isFortune = false;

    private String owner;
    private int burnTicks;
    private int ticks;
    private int speed;
    private int mode;
    private int eject;
    private boolean filter;
    private boolean loop;
    private boolean locked;
    private int burnTime;
    private int totalBurnTime;

    public QuarryBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.QUARRY_BLOCK.get(), pPos, pBlockState);
        items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && !isRemoved() && side != null) {
            return itemHandler[side.get3DDataValue()].cast();
        }
        return super.getCapability(cap, side);
    }

    @SuppressWarnings("ConstantConditions")
    public void tick() {
        if (level.isClientSide) return;
        QuarryBlockEntity entity = this;
        Level level = getLevel();
        BlockState state = getBlockState();
        BlockState above = level.getBlockState(getBlockPos().above());
        BlockState below = level.getBlockState(getBlockPos().below());
        // Eject / Pull functionality
        boolean in = false;
        boolean out = false;
        if (level.getGameTime() % 4 == 0)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        switch (getEject()) {
            case 1 -> in = true;
            case 2 -> out = true;
            case 3 -> {
                in = true;
                out = true;
            }
        }
        LazyOptional<IItemHandler> quarryCapability = this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (quarryCapability.isPresent()) {
            IItemHandler quarryHandler = quarryCapability.resolve().get();
            if (above.hasBlockEntity()) {
                BlockEntity tileAbove = level.getBlockEntity(getBlockPos().above());
                LazyOptional<IItemHandler> capabilityAbove = tileAbove.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                if (in && capabilityAbove.isPresent()) {
                    IItemHandler handlerAbove = capabilityAbove.resolve().get();
                    for (int i = 0; i < handlerAbove.getSlots(); i++) {
                        ItemStack stack = handlerAbove.getStackInSlot(i);
                        if (QuarryContainer.burnables.contains(stack.getItem())) {
                            int slot = hasInputSpace(new ItemStack(stack.getItem(), 1));
                            if (slot != -1) {
                                if (slot != 99) {
                                    quarryHandler.insertItem(slot, new ItemStack(stack.getItem(), 1), false);
                                    handlerAbove.extractItem(i, 1, false);
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (below.hasBlockEntity()) {
                BlockEntity tileBelow = level.getBlockEntity(getBlockPos().below());
                LazyOptional<IItemHandler> capabilityBelow = tileBelow.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                if (out && capabilityBelow.isPresent()) {
                    IItemHandler handlerBelow = capabilityBelow.resolve().get();
                    boolean doBreak = false;
                    for (int i = 6; i <= 11; i++) {
                        ItemStack stack = quarryHandler.getStackInSlot(i);
                        for (int e = 0; e < handlerBelow.getSlots(); e++) {
                            ItemStack slotStack = handlerBelow.getStackInSlot(e);
                            if (!stack.is(Items.AIR)) {
                                if (slotStack.isEmpty() || new ItemStack(stack.getItem(), 1).is(slotStack.getItem())) {
                                    if ((slotStack.getCount() + 1) <= stack.getMaxStackSize()) {
                                        handlerBelow.insertItem(e, new ItemStack(stack.getItem(), 1), false);
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

        // Refueling stuff
        List<ItemStack> input = new ArrayList<>();
        for (int i = 0; i <= 5; i++)
            input.add(getItem(i));
        if (burnTime <= 1001) {
            for (int i = 0; i < input.size(); i++) {
                if (ForgeHooks.getBurnTime(input.get(i), null) > 0) {
                    totalBurnTime = burnTime + ForgeHooks.getBurnTime(input.get(i), null);
                    burnTime += totalBurnTime;
                    removeItem(i, 1);
                    break;
                }

            }
        }

        if (burnTime > 0 != state.getValue(QuarryBlock.POWERED))
            level.setBlockAndUpdate(getBlockPos(), state.setValue(QuarryBlock.POWERED, burnTime > 0));
        if (!state.getValue(QuarryBlock.POWERED) && state.getValue(QuarryBlock.ACTIVE))
            level.setBlockAndUpdate(getBlockPos(), state.setValue(QuarryBlock.ACTIVE, burnTime > 0));
        if (!state.getValue(QuarryBlock.ACTIVE) && state.getValue(QuarryBlock.WORKING))
            level.setBlockAndUpdate(getBlockPos(), state.setValue(QuarryBlock.POWERED, burnTime > 0).setValue(QuarryBlock.WORKING, false));

        if (burnTime >= CommonConfig.quarryIdleConsumption.get() && burnTicks >= 20 && !state.getValue(QuarryBlock.WORKING)) {
            burnTime -= CommonConfig.quarryIdleConsumption.get();
            burnTicks = 0;
        }
        burnTicks++;
        if (state.getValue(QuarryBlock.ACTIVE)) {
            ItemStack areaCardItem = getItem(12);
            if (areaCardItem.is(ModItems.AREA_CARD.get())) {
                CompoundTag itemTag = NbtUtil.getNbtTag(areaCardItem);
                if (itemTag.contains("pos1") && itemTag.contains("pos2")) {
                    // Get Speed and set to variables
                    boolean speedy = speed == 0 && !(ticks + speedModifier >= SPEED_0);
                    if (speed == 1 && !(ticks + speedModifier >= SPEED_1)) speedy = true;
                    if (speed == 2 && !(ticks + speedModifier >= SPEED_2)) speedy = true;
                    if (speedy) {
                        ticks++;
                        return;
                    }
                    // Get Mode to variables to work with in-code easilier!
                    float fuelModifier = CalcUtil.getNeededTicks(mode, speed);
                    boolean isSilktouch;
                    boolean isVoid;
                    switch (entity.getMode()) {
                        case 1 -> {
                            speedModifier = -5;
                            isFortune = false;
                            isSilktouch = false;
                            isVoid = false;
                        } // Efficient
                        case 2 -> {
                            speedModifier = 0;
                            isFortune = true;
                            isSilktouch = false;
                            isVoid = false;
                        } // Fortune
                        case 3 -> {
                            speedModifier = 0;
                            isFortune = false;
                            isSilktouch = true;
                            isVoid = false;
                        } // Silktouch
                        case 4 -> {
                            speedModifier = 0;
                            isFortune = false;
                            isSilktouch = false;
                            isVoid = true;
                        } // Void
                        default -> {
                            speedModifier = 0;
                            isFortune = false;
                            isSilktouch = false;
                            isVoid = false;
                        }
                    }
                    if (blockStateList == null || blockStateList.isEmpty()) refreshPositions(areaCardItem);
                    if (blockStateList.size() > 0 && burnTime > fuelModifier) {
                        if (areaCardItem.is(ModItems.AREA_CARD.get()))
                            level.setBlockAndUpdate(getBlockPos(), state.setValue(QuarryBlock.WORKING, true));
                        if (!itemTag.contains("lastBlock")) itemTag.putInt("lastBlock", 0);

                        // Item Data Reset And Machine Turn Off
                        int blockIndex = itemTag.getInt("lastBlock");
                        if (blockIndex > blockStateList.size() - 1) {
                            level.setBlockAndUpdate(getBlockPos(), state.setValue(QuarryBlock.WORKING, false).setValue(QuarryBlock.ACTIVE, false));
                            itemTag.putInt("lastBlock", 0);
                            if (getLoop())
                                level.setBlockAndUpdate(getBlockPos(), state.setValue(QuarryBlock.ACTIVE, true));
                            return;
                        }

                        // Checking for Invalid Blocks
                        BlockPos currentBlock = blockStateList.get(blockIndex);
                        if (isInNearSquare(this.getBlockPos(), currentBlock)) {
                            itemTag.putInt("lastBlock", blockIndex + 1);
                            itemTag.putInt("currentY", currentBlock.getY());
                            return;
                        }
                        if (level.getBlockState(currentBlock).getBlock() == Blocks.AIR) {
                            itemTag.putInt("lastBlock", blockIndex + 1);
                            burnTime -= fuelModifier;
                        } else {
                            FakePlayer player = FakePlayerFactory.get((ServerLevel) level, new GameProfile(UUID.fromString("6e483f02-30db-4454-b612-3a167614b576"), "VanillaQuarry Quarry"));
                            // Block Drops Looping with Inventory-Space Checking and Block Breaking
                            List<ItemStack> drops = level.getBlockState(currentBlock).getDrops(getBuilder(level, currentBlock, isSilktouch));
                            if (drops.isEmpty()) {
                                if (allowedToBreak(level.getBlockState(currentBlock), level, currentBlock, player)) {
                                    setChanged();
                                    level.playSound(player, currentBlock.getX() + 0.5, currentBlock.getY() + 0.5, currentBlock.getZ() + 0.5, level.getBlockState(currentBlock).getSoundType().getBreakSound(), SoundSource.BLOCKS, 1f, 1f);
                                    level.setBlockAndUpdate(currentBlock, Blocks.AIR.defaultBlockState());
                                }
                                itemTag.putInt("lastBlock", blockIndex + 1);
                                itemTag.putInt("currentY", currentBlock.getY());
                                burnTime -= fuelModifier;
                                return;
                            }
                            boolean broken = false;
                            for (ItemStack drop : drops) {
                                if (isVoid) {
                                    itemTag.putInt("lastBlock", blockIndex + 1);
                                    itemTag.putInt("currentY", currentBlock.getY());
                                    burnTime -= fuelModifier;
                                    broken = true;
                                    break;
                                }
                                setChanged();
                                int index = hasOutputSpace(drop);
                                if (index != 0) {
                                    CompoundTag filtersTag = itemTag.getCompound("Filters");
                                    boolean filtered = false;
                                    Item[] stacks = {Items.COBBLESTONE, Items.STONE, Items.GRAVEL, Items.DIRT, Items.SAND, Items.RED_SAND, Items.NETHERRACK};
                                    if (getFilter()) {
                                        for (int i = 0; i <= 6; i++) {
                                            if (filtersTag.getBoolean(String.valueOf(i))) {
                                                if (drop.is(stacks[i])) {
                                                    filtered = true;
                                                }
                                            }
                                        }
                                    }
                                    if (allowedToBreak(level.getBlockState(currentBlock), level, currentBlock, player)) {
                                        if (!filtered)
                                            setItem(index, new ItemStack(drop.getItem(), getItem(index).getCount() + drop.getCount()));
                                        burnTime -= fuelModifier;
                                        broken = true;
                                    }
                                    itemTag.putInt("lastBlock", blockIndex + 1);
                                    itemTag.putInt("currentY", currentBlock.getY());
                                    break;
                                }
                            }
                            if (broken) {
                                level.playSound(player, currentBlock.getX() + 0.5, currentBlock.getY() + 0.5, currentBlock.getZ() + 0.5, level.getBlockState(currentBlock).getSoundType().getBreakSound(), SoundSource.BLOCKS, 1f, 1f);
                                level.setBlock(currentBlock, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                            }
                        }
                    } else {
                        // Machine turns off after use
                        level.setBlock(getBlockPos(), state.setValue(QuarryBlock.ACTIVE, false).setValue(QuarryBlock.WORKING, false), 3);
                    }

                    ticks = 0;
                }
            }
        }
    }

    public boolean isInNearSquare(BlockPos origin, BlockPos target) {
        BlockPos pos1 = origin.offset(-1, -1, -1);
        BlockPos pos2 = origin.offset(1, 1, 1);
        return CalcUtil.getBlockStates(pos1, pos2, level).contains(target);
    }

    public int hasInputSpace(ItemStack itemStack) {
        for (int i = 0; i <= 5; i++) {
            ItemStack current = getItem(i);
            if (itemStack.is(Items.AIR)) return 99;
            if (current.isEmpty()) return i;
            if (current.getItem() == itemStack.getItem()) {
                if (current.getCount() + itemStack.getCount() <= current.getMaxStackSize()) return i;
            }
        }
        return -1;
    }

    public int hasOutputSpace(ItemStack itemStack) {
        for (int i = 6; i <= 11; i++) {
            ItemStack current = getItem(i);
            if (itemStack.is(Items.AIR)) return 99;
            if (current.isEmpty()) return i;
            if (current.getItem() == itemStack.getItem()) {
                if (current.getCount() + itemStack.getCount() <= current.getMaxStackSize()) return i;
            }
        }
        return 0;
    }

    private boolean allowedToBreak(BlockState state, Level world, BlockPos pos, Player player) {
        if (!state.getBlock().canEntityDestroy(state, world, pos, player) || state.getDestroySpeed(level, pos) == -1)
            return false;
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    public void refreshPositions(ItemStack itemStack) {
        CompoundTag pos1 = (CompoundTag) itemStack.getOrCreateTag().get("pos1");
        CompoundTag pos2 = (CompoundTag) itemStack.getOrCreateTag().get("pos2");
        if (pos1 == null || pos2 == null) return;
        BlockPos blockPos1 = NbtUtil.getPos(pos1);
        BlockPos blockPos2 = NbtUtil.getPos(pos2);
        blockStateList = CalcUtil.getBlockStates(blockPos2, blockPos1, level);
    }

    public LootContext.Builder getBuilder(Level level, BlockPos pos, boolean isSilktouch) {
        ItemStack stack = new ItemStack(Items.STICK);
        if (isSilktouch)
            stack.enchant(Enchantments.SILK_TOUCH, 1);
        if (isFortune)
            stack.enchant(Enchantments.BLOCK_FORTUNE, 3);
        lootcontextBuilder = (new LootContext.Builder((ServerLevel) level)).withRandom(level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, stack).withOptionalParameter(LootContextParams.BLOCK_ENTITY, this);
        return lootcontextBuilder;
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

    @Override
    public Level getLevel() {
        return super.getLevel();
    }

    @Override
    public void setLevel(@NotNull Level pLevel) {
        super.setLevel(pLevel);
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

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        super.getUpdateTag();
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("BurnTime", getBurnTime());
        nbt.putInt("TotalBurnTime", getTotalBurnTime());
        nbt.putInt("Speed", getSpeed());
        nbt.putInt("Mode", getMode());
        nbt.putInt("Eject", getEject());
        nbt.putString("Owner", getOwner());
        nbt.putBoolean("Locked", getLocked());
        nbt.putBoolean("Filter", getFilter());
        nbt.putBoolean("Loop", getLoop());
        return nbt;
    }

    @Override
    public void handleUpdateTag(final CompoundTag tag) {
        setBurnTime(tag.getInt("BurnTime"));
        setTotalBurnTime(tag.getInt("TotalBurnTime"));
        setSpeed(tag.getInt("Speed"));
        setMode(tag.getInt("Mode"));
        setEject(tag.getInt("Eject"));
        setOwner(tag.getString("Owner"));
        setLocked(tag.getBoolean("Locked"));
        setFilter(tag.getBoolean("Filter"));
        setLoop(tag.getBoolean("Loop"));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (getLevel().isClientSide && net.getDirection() == PacketFlow.CLIENTBOUND) handleUpdateTag(pkt.getTag());
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("Speed", this.speed);
        nbt.putInt("Mode", this.mode);
        nbt.putInt("Eject", this.eject);
        nbt.putInt("BurnTime", this.burnTime);
        nbt.putInt("TotalBurnTime", this.totalBurnTime);
        nbt.putString("Owner", getOwner());
        nbt.putBoolean("Locked", getLocked());
        nbt.putBoolean("Filter", getFilter());
        nbt.putBoolean("Loop", getLoop());
        ContainerHelper.saveAllItems(nbt, this.items, true);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.items.clear();
        ContainerHelper.loadAllItems(nbt, this.items);
        this.speed = nbt.getInt("Speed");
        this.mode = nbt.getInt("Mode");
        this.eject = nbt.getInt("Eject");
        this.burnTime = nbt.getInt("BurnTime");
        this.totalBurnTime = nbt.getInt("TotalBurnTime");
        this.owner = nbt.getString("Owner");
        this.locked = nbt.getBoolean("Locked");
        this.filter = nbt.getBoolean("Filter");
        this.loop = nbt.getBoolean("Loop");
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction pSide) {
        if (pSide == Direction.DOWN) return new int[]{6, 7, 8, 9, 10, 11};
        return new int[]{0, 1, 2, 3, 4, 5};
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, @NotNull ItemStack pItemStack, @Nullable Direction pDirection) {
        if (!(pDirection == Direction.UP)) return false;
        return ForgeHooks.getBurnTime(pItemStack, null) > 0;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, @NotNull ItemStack pStack, @NotNull Direction pDirection) {
        return pDirection == Direction.DOWN;
    }

    @NotNull
    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("block.quarry.quarry_block");
    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pInventory) {
        return new QuarryContainer(pContainerId, pInventory, getBlockPos(), getLevel());
    }

    @Override
    public int getContainerSize() {
        return 13;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : items) {
            if (itemStack.isEmpty()) return true;
        }
        return false;
    }

    @NotNull
    @Override
    public ItemStack getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return ItemStack.EMPTY;
        }
        return items.get(index);
    }

    @NotNull
    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(items, index, count);
    }

    @NotNull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(items, index);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public void clearContent() {
        items.clear();
    }
}
