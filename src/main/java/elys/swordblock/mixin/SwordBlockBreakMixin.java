package elys.swordblock.mixin;

import elys.swordblock.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class SwordBlockBreakMixin {

    private boolean isSwordBlocking() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return false;
        if (client.getCurrentServerEntry() == null) return false;
        if (!client.getCurrentServerEntry().address.toLowerCase().contains("hypixel.net")) return false;
        if (!Config.swordBlockEnabled) return false;
        return client.player.getMainHandStack().isIn(ItemTags.SWORDS) && client.options.useKey.isPressed();
    }

    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    private void cancelAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (isSwordBlocking()) cir.setReturnValue(false);
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true)
    private void cancelBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (isSwordBlocking()) cir.setReturnValue(false);
    }

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void cancelAttackEntity(PlayerEntity player, Entity entity, CallbackInfo ci) {
        if (isSwordBlocking()) ci.cancel();
    }
}
