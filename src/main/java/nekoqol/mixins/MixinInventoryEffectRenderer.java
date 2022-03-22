package nekoqol.mixins;

import net.minecraft.client.renderer.InventoryEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import nekoqol.NekoQOL;

@Mixin(InventoryEffectRenderer.class)
public class MixinInventoryEffectRenderer {
    @ModifyVariable(method = "updateActivePotionEffects", at = @At(value = "STORE"))
    public boolean updateActivePotionEffects(boolean hasVisibleEffect) {
        if (NekoQOL.Companion.getConfig().getHidePotionEffects() && NekoQOL.Companion.getInSkyblock()) {
            return false;
        } else return hasVisibleEffect;
    }
}
