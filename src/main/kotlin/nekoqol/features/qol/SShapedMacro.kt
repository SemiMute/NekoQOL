package nekoqol.features.qol

import gg.essential.universal.UChat
import nekoqol.NekoQOL
import nekoqol.NekoQOL.Companion.inSkyblock
import nekoqol.NekoQOL.Companion.keyBinds
import nekoqol.NekoQOL.Companion.mc
import nekoqol.NekoQOL.Companion.nameArray
import nekoqol.utils.ScoreboardUtils
import nekoqol.utils.Utils
import nekoqol.utils.Utils.equalsOneOf
import nekoqol.utils.Utils.isInHub
import nekoqol.utils.Utils.isInLimbo
import nekoqol.utils.Utils.isInLobby
import nekoqol.utils.Utils.isPrivateIsland
import nekoqol.utils.Utils.modMessage
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.util.Vec3i
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.random.Random

class SShapedMacro {
    private var thread: Thread? = null
    private var lastUpdate: Long = 0
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
                if(!isPrivateIsland()){
                    return UChat.chat("&dFrom ${nameArray[Random.nextInt(nameArray.size)]} &7What, you want to farm some air? Go to your (&7⏣ &aPrivate Island&7) to start the &bS Shaped Macro&7...")
                }
                isActive = true;
                failSafeActive = true
                //KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, true)
                modMessage("&bS Shaped Macro &fhas been toggled &a&lON&f!")

                //KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
            }
        }
    }
    var onWorldCooldown: Long = 0
    @SubscribeEvent
    fun onWorldCheck(event: WorldEvent.Load){
        onWorldCooldown = System.currentTimeMillis()
    }
    @SubscribeEvent
    fun onTick(event: TickEvent.WorldTickEvent) {
        if (event.phase != TickEvent.Phase.START || isPrivateIsland() && onWorldCooldown + 1500 <= System.currentTimeMillis()) return
        if (thread?.isAlive == true || lastUpdate + 2500 > System.currentTimeMillis()) return
        thread = Thread({
            if(isInLobby()){
                modMessage("&cFAILSAFE &c✧ &fPlayer is in &7⏣ &6Hypixel Lobby&f...\n&7Attempting to correct players' position...")
            }
            if(isInHub()){
                modMessage("&cFAILSAFE &c✧ &fPlayer is in the &7⏣ &bSkyblock Village&f..\n&7Attempting to correct players' position...")
            }
            lastUpdate = System.currentTimeMillis()
        }, "S Shaped Failsafe")
        thread!!.start()
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