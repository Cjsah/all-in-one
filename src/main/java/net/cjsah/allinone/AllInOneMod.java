package net.cjsah.allinone;

import com.mojang.brigadier.context.CommandContext;
import net.cjsah.allinone.chain.ChainCommand;
import net.cjsah.allinone.cs.CS;
import net.cjsah.allinone.plan.Plan;
import net.cjsah.allinone.scoreboard.Criterion;
import net.cjsah.allinone.events.PlayerBlockBreakEvent;
import net.cjsah.allinone.state.IStateGetter;
import net.cjsah.allinone.state.StateOperate;
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class AllInOneMod implements ModInitializer {

    public static final String MODID = "allinone";

    @Override
    public void onInitialize() {
        Criterion.register();
        CS.onInitialize();
        Plan.onInitialize();
        ChainCommand.register();
        PlayerBlockBreakEvent.onInitialize();
    }

    public static StateOperate getState(CommandContext<ServerCommandSource> context) {
        return getState(context.getSource().getMinecraftServer());
    }

    public static StateOperate getState(MinecraftServer server) {
        return ((IStateGetter)server).getCppStateOperate();
    }

    public static void sendGlobalMessage(CommandContext<ServerCommandSource> context, String text) {
        ServerCommandSource source = context.getSource();
        if (source.getEntity() != null) {
            String name = source.getEntity().getEntityName();
            text = name + " " + text.substring(0, 1).toLowerCase() + text.substring(1);
        }
        source.getMinecraftServer().getPlayerManager().broadcastChatMessage(new LiteralText(text).formatted(Formatting.GREEN), MessageType.SYSTEM, Util.NIL_UUID);
    }

}
