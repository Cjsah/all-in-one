package net.cjsah.allinone.plan;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

public class Plan {
    public static void onInitialize() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> dispatcher.register(
                CommandManager.literal("plan").then(CommandManager.literal("add").then(CommandManager.argument("plan", StringArgumentType.greedyString()).executes(context -> {

            return Command.SINGLE_SUCCESS;
        }))).then(CommandManager.literal("remove").then(CommandManager.argument("index", IntegerArgumentType.integer(0)).executes(context -> {

            return Command.SINGLE_SUCCESS;
        }))).then(CommandManager.literal("list").executes(context -> {

            return Command.SINGLE_SUCCESS;
        }).then(CommandManager.argument("completed"))))));
    }
}
