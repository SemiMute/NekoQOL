package nekoqol.features

import net.minecraft.block.material.Material
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import nekoqol.NekoQOL.Companion.config
import nekoqol.NekoQOL.Companion.inSkyblock

class NoWaterFOV {
    @SubscribeEvent
    fun onFOV(event: EntityViewRenderEvent.FOVModifier) {
        if (!config.antiWaterFOV || !inSkyblock || event.block.material != Material.water) return
        event.fov = event.fov * 70F / 60F
    }
}
