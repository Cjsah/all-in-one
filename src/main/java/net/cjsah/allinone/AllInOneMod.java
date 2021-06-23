package net.cjsah.allinone;

import net.cjsah.allinone.cs.CS;
import net.cjsah.allinone.plan.Plan;
import net.cjsah.allinone.scoreboard.Criterion;
import net.cjsah.allinone.scoreboard.PlayerBlockBreak;
import net.fabricmc.api.ModInitializer;

public class AllInOneMod implements ModInitializer {

    public static final String MODID = "allinone";

    @Override
    public void onInitialize() {
        Criterion.register();

        CS.onInitialize();
        Plan.onInitialize();
        PlayerBlockBreak.onInitialize();
    }
}
