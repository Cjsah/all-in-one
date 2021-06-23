package net.cjsah.allinone.cs;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cjsah.allinone.state.IStateGetter;
import net.cjsah.allinone.state.StateOperate;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;

public class CS {
    public static void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                CommandManager.literal("c").requires(CS::hasPermission)
                        .executes(commandContext -> {
                            changeMode(commandContext, GameMode.SPECTATOR, true);
                            return Command.SINGLE_SUCCESS;
                        })
        ));

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                CommandManager.literal("s").requires(CS::hasPermission)
                        .executes(context -> {
                            changeMode(context, GameMode.SURVIVAL, false);
                            return Command.SINGLE_SUCCESS;
                        })
        ));
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("cs").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("add").then(CommandManager.argument("target", EntityArgumentType.player()).executes(context -> {
                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
                    context.getSource().sendFeedback(new LiteralText(String.format(getStateOperate(context.getSource()).addPlayer(player), player.getEntityName())), false);
                    return Command.SINGLE_SUCCESS;
                }))).then(CommandManager.literal("remove").then(CommandManager.argument("target", EntityArgumentType.player()).executes(context -> {
                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
                    context.getSource().sendFeedback(new LiteralText(String.format(getStateOperate(context.getSource()).removePlayer(player), player.getEntityName())), false);
                    return Command.SINGLE_SUCCESS;
                }))).then(CommandManager.literal("whitelist").then(CommandManager.argument("boolean", BoolArgumentType.bool()).executes(context -> {
                    boolean bl = BoolArgumentType.getBool(context, "boolean");
                    context.getSource().sendFeedback(new LiteralText(String.format(getStateOperate(context.getSource()).needWhiteList(bl), bl)), false);
                    return Command.SINGLE_SUCCESS;
                }))).then(CommandManager.literal("list").executes(context -> {
                    getStateOperate(context.getSource()).listPlayers(context);
                    return Command.SINGLE_SUCCESS;
                }))
        ));
    }

    private static void changeMode(CommandContext<ServerCommandSource> context, GameMode mode, boolean effect) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            if (player.interactionManager.getGameMode() != mode) {
                player.changeGameMode(mode);
                if (effect) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 2147483647, 0, true, false));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 2147483647, 0, true, false));
                }else {
                    player.removeStatusEffect(StatusEffects.NIGHT_VISION);
                    player.removeStatusEffect(StatusEffects.CONDUIT_POWER);
                }
            }
        } catch (CommandSyntaxException ignore) {}
    }

    private static boolean hasPermission(ServerCommandSource source) {
        return source.hasPermissionLevel(2) || !getStateOperate(source).hasWhiteList() || getStateOperate(source).containsPlayer(source.getName());
    }

    private static StateOperate getStateOperate(ServerCommandSource source) {
        return ((IStateGetter)source.getMinecraftServer()).getCppStateOperate();
    }

}
