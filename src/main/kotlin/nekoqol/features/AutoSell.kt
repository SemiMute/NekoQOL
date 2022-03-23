package nekoqol.features

import gg.essential.universal.UChat.chat
import nekoqol.NekoQOL
import nekoqol.NekoQOL.Companion.inSkyblock
import nekoqol.NekoQOL.Companion.mc
import nekoqol.utils.Utils.modMessage
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
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
        if(NekoQOL.nekoconfig.autoSellToggle){
            if(NekoQOL.nekoconfig.autoSellChoice == 0){
                if (StringUtils.stripControlCodes(event.message.unformattedText).startsWith("Your inventory is full!")) {
                    modMessage("&cDEBUGGER: &7Detected full inventory!")
                    mc.thePlayer.sendChatMessage("/bz")
                    Timer().schedule(timerTask {
                        click(38, false, 600)
                        chat("&cNEKOQOL DEBUGGER: &fClicked slot &b38&f in menu Bazaar")
                    }, 750)
                    Timer().schedule(timerTask {
                        click(11, false, 600)
                        chat("&cNEKOQOL DEBUGGER: &fClicked slot &b11&f in menu BAZAAR CONFIRM")
                    }, 1500)
                    Timer().schedule(timerTask {
                        click(11, false, 600)
                        chat("&cNEKOQOL DEBUGGER: &fClicked slot &b11&f in menu BAZAAR CONFIRM")
                    }, 2100)
                    Timer().schedule(timerTask {
                        click(15, false, 600)
                        chat("&cNEKOQOL DEBUGGER: &fAttempting to close Bazaar Menu")
                    }, 2800)
                }
            }
        }
        // Minecraft::class.java.getDeclaredMethod("field_216699_f")
    }
    fun click(slot: Int, shift: Boolean, delay: Long) {
        mc.playerController.windowClick(
            mc.thePlayer.openContainer.windowId, slot, 0, if (shift) 1 else 0, mc.thePlayer
        )
    }
}