package net.cjsah.allinone.chain;

import com.mojang.brigadier.Command;
import net.cjsah.allinone.player.IPlayerState;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class ChainCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> dispatcher.register(
                CommandManager.literal("chain").then(CommandManager.literal("start").executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player != null) ((IPlayerState)player).setChaining(true);
                    context.getSource().sendFeedback(new LiteralText("You started chain block. Please be careful not to damage the map!").formatted(Formatting.GREEN), false);
                    return Command.SINGLE_SUCCESS;
                })).then(CommandManager.literal("stop").executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player != null) ((IPlayerState)player).setChaining(false);
                    context.getSource().sendFeedback(new LiteralText("You stopped chain block.").formatted(Formatting.GREEN), false);
                    return Command.SINGLE_SUCCESS;
                }))
        )));
    }
}
