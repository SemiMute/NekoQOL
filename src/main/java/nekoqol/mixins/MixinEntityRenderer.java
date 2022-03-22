package nekoqol.mixins;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import nekoqol.NekoQOL;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @ModifyVariable(method = "setupCameraTransform", at = @At(value = "STORE"), ordinal = 2)
    private float noNausea(float f1) {
        return NekoQOL.Companion.getConfig().getAntiPortal() && NekoQOL.Companion.getInSkyblock() ? 0F : f1;
    }
}
