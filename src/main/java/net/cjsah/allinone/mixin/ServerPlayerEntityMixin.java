package net.cjsah.allinone.mixin;

import com.mojang.authlib.GameProfile;
import net.cjsah.allinone.player.IPlayerState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements IPlayerState {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    private boolean chaining = false;

    @Override
    public void setChaining(boolean bl) {
        this.chaining = bl;
    }

    @Override
    public boolean isChaining() {
        return this.chaining;
    }

}
