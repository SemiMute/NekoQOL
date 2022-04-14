package nekoqol.features

import nekoqol.NekoQOL
import nekoqol.NekoQOL.Companion.mc
import nekoqol.utils.Utils.modMessage
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import java.util.*
import kotlin.concurrent.timerTask

class AutoArmor {
    @SubscribeEvent
    fun onKeyPress(event: InputEvent.KeyInputEvent) {
        if(NekoQOL.keyBinds[2].isPressed) {
            mc.thePlayer.sendChatMessage("/wardrobe")
            Timer().schedule(timerTask {
                click(36, false, mc.currentServerData.pingToServer + 25)
                modMessage("&fAuto selected armor slot &b#1&f with &bAuto Armor")
                click(49, false, mc.currentServerData.pingToServer + 25)
            }, 300)
        }
    }
    fun click(slot: Int, shift: Boolean, delay: Long) {
        mc.playerController.windowClick(
            mc.thePlayer.openContainer.windowId, slot, 0, if (shift) 1 else 0, mc.thePlayer
        )
    }

    @SubscribeEvent
    fun onCombatTag(event: LivingHurtEvent){
        if(event.entityLiving is EntityPlayer){
            modMessage("&cDEBUGGER: &fPlayer has taken damage from something")
        }
    }
}