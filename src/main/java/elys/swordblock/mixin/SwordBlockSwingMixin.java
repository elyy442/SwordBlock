package elys.swordblock.mixin;

import elys.swordblock.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class SwordBlockSwingMixin {

    private boolean shouldCancelSwing() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return false;
        if ((LivingEntity) (Object) this != client.player) return false;
        if (client.getCurrentServerEntry() == null) return false;
        if (!client.getCurrentServerEntry().address.toLowerCase().contains("hypixel.net")) return false;
        if (!Config.swordBlockEnabled) return false;
        return client.player.getMainHandStack().isIn(ItemTags.SWORDS) && client.options.useKey.isPressed();
    }

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"), cancellable = true)
    private void cancelSwingHand(Hand hand, CallbackInfo ci) {
        if (shouldCancelSwing()) ci.cancel();
    }

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;Z)V", at = @At("HEAD"), cancellable = true)
    private void cancelSwingHandWithFlag(Hand hand, boolean fromServerInput, CallbackInfo ci) {
        if (shouldCancelSwing()) ci.cancel();
    }
}
