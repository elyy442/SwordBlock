package elys.swordblock;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class UpdateChecker {

    private static final String MODRINTH_SLUG = "swordblock";
    private static final String MODRINTH_URL  = "https://modrinth.com/mod/" + MODRINTH_SLUG;

    private static boolean checked = false;

    public static void check() {
        if (checked) return;
        checked = true;

        String currentVersion = FabricLoader.getInstance()
            .getModContainer("swordblock")
            .map(c -> c.getMetadata().getVersion().getFriendlyString())
            .orElse("unknown");

        Thread thread = new Thread(() -> {
            try {
                HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.modrinth.com/v2/project/" + MODRINTH_SLUG + "/version"))
                    .header("User-Agent", "swordblock-mod/" + currentVersion)
                    .timeout(Duration.ofSeconds(5))
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) return;

                String body = response.body();
                String marker = "\"version_number\":\"";
                int idx = body.indexOf(marker);
                if (idx == -1) return;

                int start = idx + marker.length();
                int end   = body.indexOf('"', start);
                if (end == -1) return;

                String latestVersion = body.substring(start, end);
                if (latestVersion.equals(currentVersion)) return;

                MinecraftClient mc = MinecraftClient.getInstance();
                mc.execute(() -> {
                    if (mc.player == null) return;
                    mc.player.sendMessage(
                        Text.literal("§7[§bSwordBlock§7] §eA new update is available! (")
                            .append(Text.literal("§b§nDownload on Modrinth")
                                .setStyle(Style.EMPTY
                                    .withClickEvent(new ClickEvent.OpenUrl(URI.create(MODRINTH_URL)))
                                    .withUnderline(true)))
                            .append(Text.literal("§e)")),
                        false
                    );
                });
            } catch (Exception ignored) {}
        }, "swordblock-update-checker");
        thread.setDaemon(true);
        thread.start();
    }
}
