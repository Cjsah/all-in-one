package net.cjsah.allinone.plan;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.cjsah.allinone.AllInOneMod;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

public class Plan {
    public static void onInitialize() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> dispatcher.register(
                CommandManager.literal("plan").then(CommandManager.literal("add").then(CommandManager.argument("plan", StringArgumentType.greedyString()).executes(context -> {
                    AllInOneMod.getState(context).addPlan(context, StringArgumentType.getString(context, "plan"));
                    return Command.SINGLE_SUCCESS;
                }))).then(CommandManager.literal("remove").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.argument("index", IntegerArgumentType.integer(0)).executes(context -> {
                    AllInOneMod.getState(context).removePlan(context, IntegerArgumentType.getInteger(context, "index"));
                    return Command.SINGLE_SUCCESS;
                }))).then(CommandManager.literal("modify").then(CommandManager.argument("index", IntegerArgumentType.integer(0)).then(CommandManager.argument("content", StringArgumentType.greedyString()).executes(context -> {
                    AllInOneMod.getState(context).modifyPlan(context, IntegerArgumentType.getInteger(context, "index"), StringArgumentType.getString(context, "content"));
                    return Command.SINGLE_SUCCESS;
                })))).then(CommandManager.literal("complete").then(CommandManager.argument("index", IntegerArgumentType.integer(0)).executes(context -> {
                    AllInOneMod.getState(context).completePlan(context, IntegerArgumentType.getInteger(context, "index"), true);
                    return Command.SINGLE_SUCCESS;
                }).then(CommandManager.literal("false").executes(context -> {
                    AllInOneMod.getState(context).completePlan(context, IntegerArgumentType.getInteger(context, "index"), false);
                    return Command.SINGLE_SUCCESS;
                })))).then(CommandManager.literal("list").then(CommandManager.literal("all").executes(context -> {
                    AllInOneMod.getState(context).listAllPlan(context);
                    return Command.SINGLE_SUCCESS;
                })).then(CommandManager.literal("completed").executes(context -> {
                    AllInOneMod.getState(context).listPlan(context, true);
                    return Command.SINGLE_SUCCESS;
                })).then(CommandManager.literal("unfinished").executes(context -> {
                    AllInOneMod.getState(context).listPlan(context, false);
                    return Command.SINGLE_SUCCESS;
                })))
        )));
    }
}
