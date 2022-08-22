package de.unhappycodings.vanillaquarry.common.blockentity;

import com.mojang.authlib.GameProfile;
import de.unhappycodings.vanillaquarry.VanillaQuarry;
import de.unhappycodings.vanillaquarry.common.blocks.QuarryBlock;
import de.unhappycodings.vanillaquarry.common.container.QuarryContainer;
import de.unhappycodings.vanillaquarry.common.fakeplayer.QuarryFakePlayer;
import de.unhappycodings.vanillaquarry.common.item.AreaCardItem;
import de.unhappycodings.vanillaquarry.common.item.ModItems;
import de.unhappycodings.vanillaquarry.common.network.PacketHandler;
import de.unhappycodings.vanillaquarry.common.network.toserver.QuarryClientSpeedPacket;
import de.unhappycodings.vanillaquarry.common.util.CalcUtil;
import de.unhappycodings.vanillaquarry.common.util.NbtUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuarryBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, MenuProvider {

    public static QuarryFakePlayer fakePlayer;
    protected NonNullList<ItemStack> items;
    int ticks;
    int speed;
    int mode;

    private static final int SPEED_0 = 10;

    public QuarryBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.QUARRY_BLOCK.get(), pPos, pBlockState);
        items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    public void tick() {
        QuarryBlockEntity entity = this;
        Level level = getLevel();
        BlockState state = getBlockState();
        List<ItemStack> input = new ArrayList<>();
        for (int i = 0; i <= 5; i++)
            input.add(getItem(i));
        if (hasFuel(input) != state.getValue(QuarryBlock.POWERED))
            level.setBlockAndUpdate(getBlockPos(), state.setValue(QuarryBlock.POWERED, hasFuel(input)));
        if (!state.getValue(QuarryBlock.POWERED) && state.getValue(QuarryBlock.ACTIVE))
            level.setBlockAndUpdate(getBlockPos(), state.setValue(QuarryBlock.ACTIVE, hasFuel(input)));

        if (state.getValue(QuarryBlock.ACTIVE)) {
            ItemStack areaCardItem = getItem(12);

            if (areaCardItem.is(ModItems.AREA_CARD.get()) && areaCardItem.getOrCreateTag().contains("pos1") && areaCardItem.getOrCreateTag().contains("pos2")) {
                if (entity.getSpeed() == 0) {
                    if (!(ticks >= SPEED_0)) {ticks++; return;}
                    ticks = 0;
                    if (entity.getMode() == 0) {
                        CompoundTag pos1 = (CompoundTag) areaCardItem.getOrCreateTag().get("pos1");
                        CompoundTag pos2 = (CompoundTag) areaCardItem.getOrCreateTag().get("pos2");
                        BlockPos blockPos1 = new BlockPos(pos1.getInt("x"), pos1.getInt("y"), pos1.getInt("z"));
                        BlockPos blockPos2 = new BlockPos(pos2.getInt("x"), pos2.getInt("y"), pos2.getInt("z"));
                        int blocksCount = CalcUtil.getBlocks(blockPos1, blockPos2, level).size();

                        level.setBlockAndUpdate(getBlockPos(), state.setValue(QuarryBlock.WORKING, true));
                        if (blocksCount > 0) {
                            if (!areaCardItem.getOrCreateTag().contains("lastBlock")) areaCardItem.getOrCreateTag().putInt("lastBlock", 0);
                            int blockIndex = areaCardItem.getOrCreateTag().getInt("lastBlock");
                            BlockPos currentBlock = CalcUtil.getBlockStates(blockPos2, blockPos1, level).get(0);

                            
                            getPlayer(level).digBlock(currentBlock);
                            areaCardItem.getOrCreateTag().putInt("lastBlock", blockIndex + 1);
                            setItem(12, areaCardItem);
                        } else {
                            level.setBlockAndUpdate(getBlockPos(), state.setValue(QuarryBlock.ACTIVE, false));
                            level.setBlockAndUpdate(getBlockPos(), state.setValue(QuarryBlock.WORKING, false));
                        }
                    }
                }
            }
        }
    }

    public static QuarryFakePlayer getPlayer(Level level) {
        if (fakePlayer == null) fakePlayer = new QuarryFakePlayer((ServerLevel) level);
        return fakePlayer;
    }

    public boolean hasFuel(List<ItemStack> stackList) {
        for (ItemStack stack : stackList) {
            if (ForgeHooks.getBurnTime(stack, null) > 0)
                return true;
        }
        return false;
    };

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    @Override
    public Level getLevel() {
        return super.getLevel();
    }

    @Override
    public void setLevel(@NotNull Level pLevel) {
        super.setLevel(pLevel);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return ContainerHelper.saveAllItems(super.getUpdateTag(), items);
    }

    @Override
    public void handleUpdateTag(final CompoundTag tag) {
        super.handleUpdateTag(tag);
        ContainerHelper.loadAllItems(tag, items);
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("speed", this.speed);
        nbt.putInt("mode", this.mode);
        ContainerHelper.saveAllItems(nbt, this.items, true);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.items.clear();
        ContainerHelper.loadAllItems(nbt, this.items);
        this.speed = nbt.getInt("speed");
        this.mode = nbt.getInt("mode");
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction pSide) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, @NotNull ItemStack pItemStack, @Nullable Direction pDirection) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, @NotNull ItemStack pStack, @NotNull Direction pDirection) {
        return true;
    }

    @NotNull
    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("block.vanillaquarry.quarry_block");
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
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D,
                    (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public void clearContent() {
        items.clear();
    }
}
