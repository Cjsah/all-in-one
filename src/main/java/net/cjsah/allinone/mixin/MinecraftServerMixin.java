package net.cjsah.allinone.mixin;

import net.cjsah.allinone.AllInOneMod;
import net.cjsah.allinone.state.IStateGetter;
import net.cjsah.allinone.state.StateOperate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IStateGetter {

    private final StateOperate operate = new StateOperate();

    @Override
    public StateOperate getCppStateOperate() {
        return this.operate;
    }

    private void initState(PersistentStateManager persistentStateManager) {
        persistentStateManager.getOrCreate(this.getCppStateOperate()::stateFromNbt, this.getCppStateOperate()::initState, AllInOneMod.MODID);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;initScoreboard(Lnet/minecraft/world/PersistentStateManager;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, method = "createWorlds")
    private void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo info, ServerWorldProperties serverWorldProperties, GeneratorOptions generatorOptions, boolean bl, long l, long m, List<Spawner> list, SimpleRegistry<DimensionOptions> simpleRegistry, ChunkGenerator chunkGenerator2, DimensionOptions dimensionOptions, DimensionType dimensionType2, ServerWorld serverWorld, PersistentStateManager persistentStateManager) {
        this.initState(persistentStateManager);
    }
}
