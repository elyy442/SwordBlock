package elys.swordblock.mixin;

import elys.swordblock.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class SwordBlockMixin {

    @Unique private float swordblock_swingProgress = 0.0f;

    @Inject(
        method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
        at = @At("HEAD")
    )
    private void captureSwingProgress(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, CallbackInfo ci) {
        swordblock_swingProgress = swingProgress;
    }

    @Inject(method = "applyEquipOffset", at = @At("TAIL"), cancellable = true)
    private void applySwordBlock(MatrixStack matrices, Arm arm, float equipProgress, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!Config.swordBlockEnabled || client.player == null || arm != Arm.RIGHT) return;

        ItemStack item = client.player.getMainHandStack();
        if (item.isIn(ItemTags.SWORDS) && client.options.useKey.isPressed()
                && (swordblock_swingProgress == 0.0f || swordblock_swingProgress > 0.9f) && !Config.isInBedwars()) {
            matrices.translate(Config.blockTranslateX, Config.blockTranslateY, Config.blockTranslateZ);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Config.blockRotationY));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Config.blockRotationX));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Config.blockRotationZ));
            matrices.scale(Config.blockScale, Config.blockScale, Config.blockScale);
            ci.cancel();
        }
    }
}
