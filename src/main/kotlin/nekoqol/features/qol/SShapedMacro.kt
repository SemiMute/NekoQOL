package nekoqol.features.qol

import nekoqol.NekoQOL
import nekoqol.NekoQOL.Companion.mc
import nekoqol.NekoQOL.Companion.nekoconfig
import nekoqol.utils.DiscordWebhook
import nekoqol.utils.Utils.getDiscordPing
import nekoqol.utils.Utils.isInHub
import nekoqol.utils.Utils.isInLimbo
import nekoqol.utils.Utils.isInLobby
import nekoqol.utils.Utils.isPrivateIsland
import nekoqol.utils.Utils.modMessage
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent
import java.util.*
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask

var isActive = false
var failSafeActive = false;

var thread: Thread? = null
var lastUpdate = 0L

var onWorldCooldown: Long = 0

class SShapedMacro {
    fun startMacro() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
        isActive = true
        checkBlocks()
    }

    @SubscribeEvent
    fun onKeyPress(event: KeyInputEvent) {
        if(NekoQOL.keyBinds[0].isPressed) {
            if(!isActive) {
                modMessage("Starting macro...")
                startMacro()
            } else {
                modMessage("Stopping macro...")
                stopMacro()
            }
        }
    }

    fun stopMacro() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
        isActive = false
    }

    @SubscribeEvent
    fun onMove(event: WorldTickEvent) {
        if(!isActive) return

        checkBlocks()
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.PlayerTickEvent){
        if(isActive){
            mc.thePlayer.rotationPitch = nekoconfig.sShapedPitch
            if(nekoconfig.sShapedYaw == 0){
                mc.thePlayer.rotationYaw = 180F
            } else if(nekoconfig.sShapedYaw == 1){
                mc.thePlayer.rotationYaw = 0F
            } else if(nekoconfig.sShapedYaw == 2){
                mc.thePlayer.rotationYaw = -90F
            } else if(nekoconfig.sShapedYaw == 3){
                mc.thePlayer.rotationYaw = 90F
            }
        }
    }

    fun checkBlocks() {
        val dir = mc.thePlayer.horizontalFacing
        val pos = BlockPos(mc.thePlayer.position.x, mc.thePlayer.position.y, mc.thePlayer.position.z)

        when(dir) {
            EnumFacing.WEST -> {
                if (mc.theWorld.getBlockState(pos.immutable.south(1)).block != Blocks.air || mc.theWorld.getBlockState(pos).block != Blocks.air) {
                    //left
                    timer(1000) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
                    }
                }
                if (mc.theWorld.getBlockState(pos.immutable.north(1)).block != Blocks.air || mc.theWorld.getBlockState(pos).block != Blocks.air) {
                    //right
                    timer(1000) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
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
}

