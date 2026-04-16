package elys.swordblock.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;

import java.io.File;
import java.nio.file.Files;

public class Config {

    private static final File CONFIG_FILE = new File(
        FabricLoader.getInstance().getConfigDir().toFile(), "SwordBlock/swordblock-config.json"
    );
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static boolean swordBlockEnabled = true;

    public static float blockRotationX  = -76.0f;
    public static float blockRotationY  = 120.0f;
    public static float blockRotationZ  = -45.0f;
    public static float blockScale      = 1.23f;
    public static float blockTranslateX = -0.045f;
    public static float blockTranslateY = -0.035f;
    public static float blockTranslateZ = 0.0f;

    public static float tpRotationX = -45.0f;
    public static float tpRotationY = -30.0f;
    public static float tpRotationZ = 0.0f;

    public static boolean isInBedwars() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null || client.getCurrentServerEntry() == null) return false;
        if (!client.getCurrentServerEntry().address.toLowerCase().contains("hypixel.net")) return false;
        ScoreboardObjective sidebar = client.world.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        if (sidebar == null) return false;
        String title = sidebar.getDisplayName().getString().replaceAll("§.", "").toLowerCase();
        return title.contains("bed wars") || title.contains("bedwars");
    }

    public static void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            JsonObject json = new JsonObject();
            json.addProperty("enabled",      swordBlockEnabled);
            json.addProperty("rotationX",    blockRotationX);
            json.addProperty("rotationY",    blockRotationY);
            json.addProperty("rotationZ",    blockRotationZ);
            json.addProperty("scale",        blockScale);
            json.addProperty("translateX",   blockTranslateX);
            json.addProperty("translateY",   blockTranslateY);
            json.addProperty("translateZ",   blockTranslateZ);
            json.addProperty("tpRotationX",  tpRotationX);
            json.addProperty("tpRotationY",  tpRotationY);
            json.addProperty("tpRotationZ",  tpRotationZ);
            Files.writeString(CONFIG_FILE.toPath(), GSON.toJson(json));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        try {
            JsonObject json = JsonParser.parseString(Files.readString(CONFIG_FILE.toPath())).getAsJsonObject();
            if (json.has("enabled"))     swordBlockEnabled = json.get("enabled").getAsBoolean();
            if (json.has("rotationX"))   blockRotationX    = json.get("rotationX").getAsFloat();
            if (json.has("rotationY"))   blockRotationY    = json.get("rotationY").getAsFloat();
            if (json.has("rotationZ"))   blockRotationZ    = json.get("rotationZ").getAsFloat();
            if (json.has("scale"))       blockScale        = json.get("scale").getAsFloat();
            if (json.has("translateX"))  blockTranslateX   = json.get("translateX").getAsFloat();
            if (json.has("translateY"))  blockTranslateY   = json.get("translateY").getAsFloat();
            if (json.has("translateZ"))  blockTranslateZ   = json.get("translateZ").getAsFloat();
            if (json.has("tpRotationX")) tpRotationX       = json.get("tpRotationX").getAsFloat();
            if (json.has("tpRotationY")) tpRotationY       = json.get("tpRotationY").getAsFloat();
            if (json.has("tpRotationZ")) tpRotationZ       = json.get("tpRotationZ").getAsFloat();
        } catch (Exception e) {
            e.printStackTrace();
            save();
        }
    }
}
