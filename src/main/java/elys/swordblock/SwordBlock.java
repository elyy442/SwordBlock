package elys.swordblock;

import elys.swordblock.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

public class SwordBlock implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		Config.load();

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("swordblock")
				.then(ClientCommandManager.literal("on").executes(context -> {
					Config.swordBlockEnabled = true;
					Config.save();
					context.getSource().sendFeedback(Text.literal("§7[§bSwordBlock§7] §aEnabled"));
					return 1;
				}))
				.then(ClientCommandManager.literal("off").executes(context -> {
					Config.swordBlockEnabled = false;
					Config.save();
					context.getSource().sendFeedback(Text.literal("§7[§bSwordBlock§7] §cDisabled"));
					return 1;
				}))
				.executes(context -> {
					String status = Config.swordBlockEnabled ? "§aON" : "§cOFF";
					context.getSource().sendFeedback(Text.literal("§7[§bSwordBlock§7] Status: " + status));
					context.getSource().sendFeedback(Text.literal("§7Use §f/swordblock on §7or §f/swordblock off"));
					return 1;
				})
			);
		});
	}
}
