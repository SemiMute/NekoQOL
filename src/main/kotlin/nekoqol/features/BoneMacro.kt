package nekoqol.features

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import nekoqol.NekoQOL.Companion.config
import nekoqol.NekoQOL.Companion.inSkyblock
import nekoqol.NekoQOL.Companion.keyBinds
import nekoqol.NekoQOL.Companion.mc
import nekoqol.utils.Utils.rightClick

class BoneMacro {
    @SubscribeEvent
    fun onKey(event: KeyInputEvent?) {
        if (!inSkyblock || !keyBinds[1].isPressed) return
        Thread {
            for (i in 0..8) {
                val item = mc.thePlayer.inventory.getStackInSlot(i) ?: continue
                if (item.displayName.contains("Bonemerang")) {
                    mc.thePlayer.inventory.currentItem = i
                    rightClick()
                    Thread.sleep(config.boneThrowDelay.toLong())
                }
            }
        }.start()
    }
}
