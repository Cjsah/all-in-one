package net.cjsah.allinone.state;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import net.cjsah.allinone.AllInOneMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.List;

public class StateOperate {
    private final JsonObject data = new JsonObject();
    private final List<Runnable> updateListeners = Lists.newArrayList();
    private static final Dynamic2CommandExceptionType INDEX_TOO_SMALL = new Dynamic2CommandExceptionType((size, found) -> new LiteralMessage("Index must not be less than " + size + ", found " + found));

    public StateOperate() {
        this.data.addProperty("whitelist", false);
        this.data.add("players", new JsonArray());
        this.data.add("plans", new JsonArray());
    }

    private void addUpdateListener(Runnable listener) {
        this.updateListeners.add(listener);
    }

    protected void runUpdateListeners() {
        for (Runnable runnable : this.updateListeners) {
            runnable.run();
        }
    }

    public State stateFromNbt(NbtCompound nbt) {
        State state = new State(this.data);
        this.addUpdateListener(state::markDirty);
        state.readNbt(nbt);
        return state;
    }

    public State initState() {
        State state = new State(this.data);
        this.addUpdateListener(state::markDirty);
        this.runUpdateListeners();
        return state;
    }

    public boolean hasWhiteList() {
        return this.data.get("whitelist").getAsBoolean();
    }

    public String needWhiteList(boolean bl) {
        if (this.data.get("whitelist").getAsBoolean() == bl) return "The Whitelist is already %b!";
        this.data.addProperty("whitelist", bl);
        this.runUpdateListeners();
        return "Set Whitelist %b completed!";
    }

    public String addPlayer(ServerPlayerEntity player) {
        String name = player.getEntityName();
        if (this.containsPlayer(name)) return "%s is already on the whitelist!";
        this.data.get("players").getAsJsonArray().add(name);
        this.runUpdateListeners();
        return "Add %s to whitelist completed";
    }

    public String removePlayer(ServerPlayerEntity player) {
        String name = player.getEntityName();
        if (!this.containsPlayer(name)) return "%s is not in the whitelist!";
        JsonArray array = this.data.get("players").getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getAsString().equals(name)) {
                array.remove(i);
                break;
            }
        }
        this.runUpdateListeners();
        return "Removed %s from whitelist";
    }

    public void listPlayers(CommandContext<ServerCommandSource> context) {
        JsonArray array = this.data.get("players").getAsJsonArray();
        if (array.size() == 0) context.getSource().sendFeedback(new LiteralText("Empty list"), false);
        else {
            context.getSource().sendFeedback(new LiteralText("Whitelist :"), false);
            for (JsonElement player : array) {
                context.getSource().sendFeedback(new LiteralText(" - " + player.getAsString()), false);
            }
        }
    }

    public boolean containsPlayer(String name) {
        for (JsonElement player : this.data.get("players").getAsJsonArray()) {
            if (player.getAsString().equals(name)) return true;
        }
        return false;
    }

    public void addPlan(CommandContext<ServerCommandSource> context, String content) {
        JsonObject plan = new JsonObject();
        plan.addProperty("content", content);
        plan.addProperty("completed", false);
        this.data.get("plans").getAsJsonArray().add(plan);
        this.runUpdateListeners();
        AllInOneMod.sendGlobalMessage(context, "Created a plan: " + content);
    }

    public void removePlan(CommandContext<ServerCommandSource> context, int index) {
        this.data.get("plans").getAsJsonArray().remove(index);
        this.runUpdateListeners();
        AllInOneMod.sendGlobalMessage(context, "Removed plan " + index);
    }

    public void modifyPlan(CommandContext<ServerCommandSource> context, int index, String content) throws CommandSyntaxException {
        JsonArray plans = this.data.get("plans").getAsJsonArray();
        if (plans.size() <= index) throw INDEX_TOO_SMALL.create(plans.size(), index);
        plans.get(index).getAsJsonObject().addProperty("content", content);
        this.runUpdateListeners();
        AllInOneMod.sendGlobalMessage(context, "Modified plan " + index + " to " + content);
    }

    public void completePlan(CommandContext<ServerCommandSource> context, int index, boolean complete) throws CommandSyntaxException {
        JsonArray plans = this.data.get("plans").getAsJsonArray();
        if (plans.size() <= index) throw INDEX_TOO_SMALL.create(plans.size(), index);
        plans.get(index).getAsJsonObject().addProperty("completed", complete);
        this.runUpdateListeners();
        AllInOneMod.sendGlobalMessage(context,  (complete ? "Completed" : "Recovered") + " a plan " + index);
    }

    public void listAllPlan(CommandContext<ServerCommandSource> context) {
        JsonArray plans = this.data.get("plans").getAsJsonArray();
        context.getSource().sendFeedback(new LiteralText("Total " + plans.size() + " plans:").formatted(Formatting.GOLD), false);
        for (int index = 0; index < plans.size(); index++) {
            JsonObject plan = plans.get(index).getAsJsonObject();
            MutableText text = new LiteralText(String.format(" - Index: %d, %s", index, plan.get("content").getAsString())).formatted(Formatting.GOLD);
            if (plan.get("completed").getAsBoolean()) text.formatted(Formatting.STRIKETHROUGH);
            context.getSource().sendFeedback(text, false);
        }
    }

    public void listPlan(CommandContext<ServerCommandSource> context, boolean completed) {
        JsonArray plans = this.data.get("plans").getAsJsonArray();
        context.getSource().sendFeedback(new LiteralText("Total " + (completed ? "completed" : "unfinished") + " plans:").formatted(Formatting.GOLD), false);
        for (int index = 0; index < plans.size(); index++) {
            JsonObject plan = plans.get(index).getAsJsonObject();
            if (plan.get("completed").getAsBoolean() == completed) {
                MutableText text = new LiteralText(String.format(" - Index: %d, %s", index, plan.get("content").getAsString())).formatted(Formatting.GOLD);
                context.getSource().sendFeedback(text, false);
            }
        }

    }
}
