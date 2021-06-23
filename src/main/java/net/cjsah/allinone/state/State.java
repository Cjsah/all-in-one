package net.cjsah.allinone.state;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.world.PersistentState;

public class State extends PersistentState {

    private final JsonObject data;

    protected State(JsonObject data) {
        this.data = data;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putBoolean("whitelist", data.get("whitelist").getAsBoolean());
        NbtList players = new NbtList();
        for (JsonElement player : this.data.get("players").getAsJsonArray()) {
            players.add(NbtString.of(player.getAsString()));
        }
        nbt.put("players", players);
        NbtList plans = new NbtList();
        for (JsonElement plan : this.data.get("plans").getAsJsonArray()) {
            NbtCompound compound = new NbtCompound();
            compound.putString("content", plan.getAsJsonObject().get("content").getAsString());
            compound.putBoolean("completed", plan.getAsJsonObject().get("completed").getAsBoolean());
            plans.add(compound);
        }
        nbt.put("plans", plans);
        return nbt;
    }

    protected void readNbt(NbtCompound nbt) {
        this.data.addProperty("whitelist", nbt.getBoolean("whitelist"));
        for (NbtElement player : nbt.getList("players", 8)) {
            this.data.get("players").getAsJsonArray().add(player.asString());
        }
        for (NbtElement plan : nbt.getList("plans", 10)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("content", ((NbtCompound)plan).getString("content"));
            jsonObject.addProperty("completed", ((NbtCompound)plan).getBoolean("completed"));
            this.data.get("plans").getAsJsonArray().add(jsonObject);
        }
    }
}
