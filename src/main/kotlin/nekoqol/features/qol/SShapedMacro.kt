package nekoqol.features.qol

import gg.essential.universal.UChat
import nekoqol.NekoQOL
import nekoqol.NekoQOL.Companion.keyBinds
import nekoqol.NekoQOL.Companion.mc
import nekoqol.NekoQOL.Companion.nameArray
import nekoqol.utils.DiscordWebhook
import nekoqol.utils.Utils.getDiscordPing
import nekoqol.utils.Utils.isInHub
import nekoqol.utils.Utils.isInLimbo
import nekoqol.utils.Utils.isInLobby
import nekoqol.utils.Utils.isPrivateIsland
import nekoqol.utils.Utils.modMessage
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent
import scala.annotation.switch
import java.util.*
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask
import kotlin.math.roundToInt
import kotlin.random.Random

var isActive = false
var failSafeActive = false;

var thread: Thread? = null
var lastUpdate = 0L

var onWorldCooldown: Long = 0

fun startMacro() {
    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
    isActive = true
}

fun stopMacro() {
    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
    isActive = false
}

@SubscribeEvent
fun onMove(event: WorldTickEvent) {
    if(mc.thePlayer.lastTickPosX == mc.thePlayer.posX) return
    if(mc.thePlayer.lastTickPosY == mc.thePlayer.posY) return
    if(mc.thePlayer.lastTickPosZ == mc.thePlayer.posZ) return

    checkBlocks()
}

fun checkBlocks() {
    val dir = mc.thePlayer.horizontalFacing
    val pos = mc.thePlayer.position

    when(dir) {
        EnumFacing.NORTH -> {
            var pos2 = pos.add(-1, 0, 0)

            if(mc.theWorld.getBlockState(pos2).block != Blocks.air){
                //left
                timer(1000) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
                }
            } else if(mc.theWorld.getBlockState(pos2.add(2,0,0)).block.isCollidable) {
                //right
                timer(1000) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
                }
            }
        }
        EnumFacing.SOUTH -> {
            var pos2 = pos.add(1, 0, 0)

            if(mc.theWorld.getBlockState(pos2).block != Blocks.air){
                //left
                timer(1000) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
                }
            } else if(mc.theWorld.getBlockState(pos2.add(-2,0,0)) != Blocks.air) {
                //right
                timer(1000) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
                }
            }
        }
        EnumFacing.EAST ->  {
            var pos2 = pos.add(0, 0, -1)

            if(mc.theWorld.getBlockState(pos2).block != Blocks.air){
                //left
                timer(1000) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
                }
            } else if(mc.theWorld.getBlockState(pos2.add(0,0,2)).block != Blocks.air) {
                //right
                timer(1000) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
                }
            }
        }
        EnumFacing.WEST ->  {
            var pos2 = pos.add(0, 0, 1)

            if(mc.theWorld.getBlockState(pos2).block != Blocks.air){
                //left
                timer(1000) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
                }
            } else if(mc.theWorld.getBlockState(pos2.add(0,0,-2)).block != Blocks.air) {
                //right
                timer(1000) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
                }
            }
        }
    }
}

fun timer(delay: Int, callback: () -> Unit) {
    Timer().schedule(timerTask {
        callback()
    }, delay.toLong())
}

@SubscribeEvent
fun onWorldCheck(event: WorldEvent.Load){
    if(isActive){
        stopMacro()
        modMessage("&bS Shaped Macro&f has been force toggled &c&lOFF&f due to a world change")
        if(NekoQOL.nekoconfig.discordPost){
            DiscordWebhook(NekoQOL.nekoconfig.discordURL).setContent(getDiscordPing("Detected a world change. Disabling Macro while failsafes activate")).execute()
        }
    }
    onWorldCooldown = System.currentTimeMillis()
}
@SubscribeEvent
fun onTick(event: TickEvent.ClientTickEvent) {
    if (event.phase != TickEvent.Phase.START || failSafeActive && isPrivateIsland() && onWorldCooldown + 1500 <= System.currentTimeMillis()) return
    if (thread?.isAlive == true || lastUpdate + 2500 > System.currentTimeMillis()) return
    thread = Thread({
        if(failSafeActive){
            if(isInLobby()){
                modMessage("&cFAILSAFE: &fPlayer is in &7⏣ &6Hypixel Lobby&f...\n&7Attempting to correct players' position...")
                mc.thePlayer.sendChatMessage("/play skyblock")
            }
            if(isInHub()){
                modMessage("&cFAILSAFE: &fPlayer is in the &7⏣ &bSkyblock Village&f..\n&7Attempting to correct players' position...")
                mc.thePlayer.sendChatMessage("/warp home")
            }
            if(isInLimbo()){
                modMessage("&cFAILSAFE: &fPlayer is in the &7⏣ &cLimbo&f..\n&7Attempting to correct players' position...")
                mc.thePlayer.sendChatMessage("/lobby")
            }
            Timer().schedule(timerTask {
                if(isPrivateIsland()){
                    if(isActive) {
                        return@timerTask
                    }
                    modMessage("&cFAILSAFE: &fPlayer is in &7⏣ &aPrivate Island&f\n&7Attempting to start up S Shaped Macro")
                    DiscordWebhook(NekoQOL.nekoconfig.discordURL).setContent(getDiscordPing("Starting up **S Shaped** due to a player location correction")).execute()
                    startMacro()
                }
            }, 10000)
        }
        lastUpdate = System.currentTimeMillis()
    }, "S Shaped Failsafe")
    thread!!.start()
}