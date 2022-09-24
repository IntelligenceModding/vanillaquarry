package de.unhappycodings.quarry.common.network.toserver;

import de.unhappycodings.quarry.common.item.AreaCardItem;
import de.unhappycodings.quarry.common.network.base.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class AreaCardItemPacket implements IPacket {
    private final UUID player;
    private final ItemStack stack;

    public AreaCardItemPacket(UUID player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }

    public static AreaCardItemPacket decode(FriendlyByteBuf buffer) {
        return new AreaCardItemPacket(buffer.readUUID(), buffer.readItem());
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        ItemStack oldStack = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if (oldStack.getItem() instanceof AreaCardItem) {
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(player);
        buffer.writeItemStack(stack, false);
    }
}
