package net.cjsah.allinone.scoreboard;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.scoreboard.ScoreboardPlayerScore;

@SuppressWarnings("ConstantConditions")
public class PlayerBlockBreak {
    public static void onInitialize() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> player.getServer().getScoreboard().forEachScore(Criterion.MINED_COUNT, player.getEntityName(), ScoreboardPlayerScore::incrementScore));
    }
}
