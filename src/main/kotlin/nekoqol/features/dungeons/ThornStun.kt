package nekoqol.features.dungeons

import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import nekoqol.NekoQOL.Companion.config
import nekoqol.NekoQOL.Companion.mc
import nekoqol.utils.Utils.equalsOneOf
import nekoqol.utils.Utils.isFloor
import nekoqol.utils.Utils.itemID

class ThornStun {

    private var isClicking = false

    @SubscribeEvent
    fun onMouse(event: MouseEvent) {
        if (!config.afkThornStun || !isFloor(4) || event.button != 1) return
        event.isCanceled = isClicking
        if (mc.thePlayer.heldItem?.itemID?.equalsOneOf("TRIBAL_SPEAR", "BONE_BOOMERANG") == true || isClicking) {
            if (event.buttonstate) {
                isClicking = !isClicking
            }
        }
    }
}
