package nekoqol.features.qol

import gg.essential.universal.UChat
import nekoqol.NekoQOL
import nekoqol.NekoQOL.Companion.inSkyblock
import nekoqol.NekoQOL.Companion.keyBinds
import nekoqol.NekoQOL.Companion.mc
import nekoqol.utils.ScoreboardUtils
import nekoqol.utils.Utils
import nekoqol.utils.Utils.equalsOneOf
import nekoqol.utils.Utils.isInHub
import nekoqol.utils.Utils.isInLimbo
import nekoqol.utils.Utils.isInLobby
import nekoqol.utils.Utils.modMessage
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.timerTask

class SShapedMacro {
    private val LobbyServers = listOf(
        "ptl",
        "Lobby:"
    )
    private val HubServer = listOf(
        "Village",
        "Forest",
    )

    val areaRegex = Regex("⏣ §r§b§l(?<area>[\\w]+): §r§7(?<loc>[\\w ]+)§r")
    private val privateIsland = "Private";
    var isActive = false
    var failSafeActive = false;
    var autoReconnect = false
    var shouldLeftClick = false

    @SubscribeEvent
    fun onKeyPress(event: InputEvent.KeyInputEvent) {
        if (keyBinds[0].isPressed) {
            if (isActive) {
                isActive = false
                failSafeActive = false
                //KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false)
                Utils.leftClick()
                modMessage("&bS Shaped Macro&f has been toggled &c&lOFF&f!")
            } else {
                isActive = true;
                failSafeActive = true
                //KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, true)
                modMessage("&bS Shaped Macro &fhas been toggled &a&lON&f!")

                //KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load){
        Timer().schedule(timerTask {
            var world = event.world
            if(world !== mc.theWorld){
                return@timerTask
            }
            failSafeActive = true
            if(failSafeActive){
                if(isInLimbo()){
                    UChat.chat("Your dumbass is in limbo. nice going mf")
                }
                if(isInLobby()){
                    UChat.chat("&cWow, congrats your in the lobby...")
                }
                if(isInHub()){
                    UChat.chat("&cWow, congrats your in the fucking skyblock hub you dumb shit...")
                }
            }
        }, 5 * 20 * 60)
    }


}