package elys.swordblock.mixin;

import elys.swordblock.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class SwordBlockArmMixin {

    @Inject(method = "setAngles(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)V", at = @At("TAIL"))
    private void applySwordBlockArm(PlayerEntityRenderState state, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || state.id != client.player.getId()) return;
        if (!Config.swordBlockEnabled || Config.isInBedwars()) return;
        if (client.player.handSwinging) return;
        if (!client.player.getMainHandStack().isIn(ItemTags.SWORDS)) return;
        if (!client.options.useKey.isPressed()) return;

        PlayerEntityModel model = (PlayerEntityModel) (Object) this;
        model.rightArm.pitch = (float) Math.toRadians(Config.tpRotationX);
        model.rightArm.yaw   = (float) Math.toRadians(Config.tpRotationY);
        model.rightArm.roll  = (float) Math.toRadians(Config.tpRotationZ);
    }
}
