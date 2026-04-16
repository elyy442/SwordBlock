package elys.swordblock.mixin;

import elys.swordblock.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class SwordBlockMixin {

    @ModifyArg(
        method = "swingArm",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applySwingOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V"),
        index = 2
    )
    private float slowSwingWhileBlocking(float swingProgress) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getCurrentServerEntry() == null) return swingProgress;
        if (!client.getCurrentServerEntry().address.toLowerCase().contains("hypixel.net")) return swingProgress;
        if (Config.swordBlockEnabled && client.player != null) {
            ItemStack item = client.player.getMainHandStack();
            if (item.isIn(ItemTags.SWORDS) && client.options.useKey.isPressed()) {
                return 0.0f;
            }
        }
        return swingProgress;
    }

    @Inject(method = "applyEquipOffset", at = @At("TAIL"), cancellable = true)
    private void applySwordBlock(MatrixStack matrices, Arm arm, float equipProgress, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (Config.swordBlockEnabled && client.player != null && arm == Arm.RIGHT) {
            ItemStack item = client.player.getMainHandStack();

            if (item.isIn(ItemTags.SWORDS) && client.options.useKey.isPressed() && !client.player.handSwinging && !Config.isInBedwars()) {
                matrices.translate(Config.blockTranslateX, Config.blockTranslateY, Config.blockTranslateZ);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Config.blockRotationY));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Config.blockRotationX));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Config.blockRotationZ));
                matrices.scale(Config.blockScale, Config.blockScale, Config.blockScale);

                ci.cancel();
            }
        }
    }
}