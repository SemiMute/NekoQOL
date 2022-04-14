package nekoqol.features

import gg.essential.universal.UChat.chat
import nekoqol.NekoQOL
import nekoqol.NekoQOL.Companion.inSkyblock
import nekoqol.NekoQOL.Companion.mc
import nekoqol.utils.Utils.itemID
import nekoqol.utils.Utils.modMessage
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.StringUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import sun.audio.AudioPlayer.player
import java.util.*
import kotlin.concurrent.timerTask


class AutoSell {
    @SubscribeEvent
    fun onFullInventory(event: ClientChatReceivedEvent){
        if(NekoQOL.nekoconfig.autoSellToggle){
            if(NekoQOL.nekoconfig.autoSellChoice == 0){
                if (StringUtils.stripControlCodes(event.message.unformattedText).startsWith("Your inventory is full!")) {
                    modMessage("&cDEBUGGER: &7Detected full inventory!")
                    mc.thePlayer.sendChatMessage("/bz")
                    Timer().schedule(timerTask {
                        click(38, false, mc.currentServerData.pingToServer + 25)
                        chat("&cNEKOQOL DEBUGGER: &fClicked slot &b38&f in menu Bazaar")
                        modMessage("&cDEBUGGER: &fPing == ${mc.currentServerData.pingToServer}")
                    }, 750)
                    Timer().schedule(timerTask {
                        click(11, false, mc.currentServerData.pingToServer + 25)
                        chat("&cNEKOQOL DEBUGGER: &fClicked slot &b11&f in menu BAZAAR CONFIRM")
                        modMessage("&cDEBUGGER: &fPing == ${mc.currentServerData.pingToServer}")
                    }, 1500)
                    Timer().schedule(timerTask {
                        click(11, false, mc.currentServerData.pingToServer + 25)
                        chat("&cNEKOQOL DEBUGGER: &fClicked slot &b11&f in menu BAZAAR CONFIRM")
                        modMessage("&cDEBUGGER: &fPing == ${mc.currentServerData.pingToServer}")
                    }, 2100)
                    Timer().schedule(timerTask {
                        click(15, false, mc.currentServerData.pingToServer + 25)
                        chat("&cNEKOQOL DEBUGGER: &fAttempting to close Bazaar Menu")
                        modMessage("&cDEBUGGER: &fPing == ${mc.currentServerData.pingToServer}")
                    }, mc.currentServerData.pingToServer + 25)
                }
            }
            if(NekoQOL.nekoconfig.autoSellChoice == 1){
                if (StringUtils.stripControlCodes(event.message.unformattedText).startsWith("Your inventory is full!")) {
                    val fishItems = listOf(
                        "Dirt"
                    )
                    var i: Long = 1800
                    mc.thePlayer.sendChatMessage("/sbmenu")
                    Timer().schedule(timerTask {
                        click(22, false, 600)
                        chat("&cNEKOQOL DEBUGGER: &fClicked Trading Menu")
                        Timer().schedule(timerTask {
                            mc.thePlayer.inventory.mainInventory.forEachIndexed{ index, itemStack ->
                                modMessage("&cITEM THINGY ${itemStack.itemID}")
                                if(itemStack.itemID !== null){
                                    if(fishItems.contains(StringUtils.stripControlCodes(itemStack.displayName))){
                                        i += 600
                                        click(54 + -(index - (36 - index)), false, i)
                                        modMessage("&cDEBUGGER: &fClicked slot &f\"${index}\" to sell item")
                                    }
                                }
                            }
                        }, 2000)
                    }, 600)
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