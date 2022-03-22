package nekoqol.features

import gg.essential.universal.UChat
import gg.essential.universal.UChat.chat
import nekoqol.NekoQOL.Companion.inSkyblock
import nekoqol.NekoQOL.Companion.mc
import nekoqol.utils.Utils.modMessage
import net.minecraft.client.Minecraft
import net.minecraft.util.StringUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import sun.audio.AudioPlayer.player
import java.util.*
import kotlin.concurrent.timerTask


class AutoSell {
    @SubscribeEvent
    fun onFullInventory(event: ClientChatReceivedEvent){
        if(!inSkyblock) return;
        if (StringUtils.stripControlCodes(event.message.unformattedText).startsWith("Your inventory is full!")) {
            modMessage("&cDEBUGGER: &7Detected full inventory!")
            //mc.thePlayer.sendChatMessage("/sbmenu")
            Timer().schedule(timerTask {
                click(22, false, 750)
                UChat.chat("&cNEKOQOL DEBUGGER: &fClicked slot &b22&f in menu SBMENU")
            }, 750)
            //UChat.chat()
        }
        // Minecraft::class.java.getDeclaredMethod("field_216699_f")
    }
    fun click(slot: Int, shift: Boolean, delay: Long) {
        mc.playerController.windowClick(
            mc.thePlayer.openContainer.windowId, slot, 0, if (shift) 1 else 0, mc.thePlayer
        )
    }
}