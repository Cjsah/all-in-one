package net.cjsah.allinone.state;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.List;

public class StateOperate {
    private final JsonObject data = new JsonObject();
    private final List<Runnable> updateListeners = Lists.newArrayList();

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

}
