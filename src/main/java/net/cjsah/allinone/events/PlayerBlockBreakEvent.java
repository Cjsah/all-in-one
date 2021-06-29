package net.cjsah.allinone.events;

import net.cjsah.allinone.chain.ChainProcessor;
import net.cjsah.allinone.player.IPlayerState;
import net.cjsah.allinone.scoreboard.Criterion;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.scoreboard.ScoreboardPlayerScore;

@SuppressWarnings("ConstantConditions")
public class PlayerBlockBreakEvent {
    public static void onInitialize() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            player.getServer().getScoreboard().forEachScore(Criterion.MINED_COUNT, player.getEntityName(), ScoreboardPlayerScore::incrementScore);
//            if (player.isSneaking()) Chain.tryChain(world, player, pos, state);
            if (!world.isClient && ((IPlayerState)player).isChaining() && player.isSneaking() && player.canHarvest(state)) new ChainProcessor(world, player, pos, state.getBlock()).start();
        });
    }
}
