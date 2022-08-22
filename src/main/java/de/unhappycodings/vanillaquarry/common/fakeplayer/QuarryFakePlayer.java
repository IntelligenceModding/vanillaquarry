package de.unhappycodings.vanillaquarry.common.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

public class QuarryFakePlayer extends FakePlayer {

    public QuarryFakePlayer(ServerLevel level) {
        super(level, new GameProfile(UUID.fromString("6e483f02-30db-4454-b612-3a167614b576"), "VanillaQuarry Quarry"));
    }

    public void digBlock(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!level.isEmptyBlock(pos) && !(state.getDestroySpeed(level, pos) <= -1)) {
            level.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
            gameMode.handleBlockBreakAction(pos, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, null, -65);
            gameMode.destroyBlock(pos);
        }
    }

}
