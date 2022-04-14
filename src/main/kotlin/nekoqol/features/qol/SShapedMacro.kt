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
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*
import kotlin.concurrent.timerTask

class SShapedMacro {
    var isActive = false
    var failSafeActive = false
    var thread: Thread? = null
    var lastUpdate = 0L
    var onWorldCooldown: Long = 0

    private fun startMacro() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
        isActive = true
        failSafeActive = true

        if(mc.thePlayer.capabilities.isFlying){
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, true)
            Timer().schedule(timerTask {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false)
            }, 1000)
        }

        if (lastDir == 1) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
        }
    }

    @SubscribeEvent
    fun onKeyPress(event: KeyInputEvent) {
        if(NekoQOL.keyBinds[0].isPressed) {
            if(!isActive) {
                modMessage("&cSystem is currently disabled")
                //startMacro()
            } else {
                modMessage("&cSystem is currently disabled")
                //stopMacro()
            }
        }
    }

    private fun stopMacro() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)

        isActive = false
        failSafeActive = false
        moving = false
    }

    @SubscribeEvent
    fun onMove(event: TickEvent.PlayerTickEvent) {
        if(!isActive) return

        checkBlocks()
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.PlayerTickEvent){
        if(!isActive) return

        mc.thePlayer.rotationPitch = nekoconfig.sShapedPitch
        when(nekoconfig.sShapedYaw) {
            0 -> mc.thePlayer.rotationYaw = 180f
            1 -> mc.thePlayer.rotationYaw = 0f
            2 -> mc.thePlayer.rotationYaw = -90f
            3 -> mc.thePlayer.rotationYaw = 90f
        }
    }

    private val ignoreBlocks = arrayOf(
        Blocks.air,
        Blocks.water,
        Blocks.flowing_water,
        Blocks.lava,
        Blocks.flowing_lava,
    )

    private var moving = false

    private fun checkState(block: Block, callback: () -> Unit) {
        if(ignoreBlocks.contains(block)) return

        moving = true

        timer(500) {
            moving = false
            if(!isActive) return@timer
            callback()
        }
    }

    private var lastDir = 0

    private fun checkBlocks() {

        if(moving) return

        val dir = mc.thePlayer.horizontalFacing
        val pos = BlockPos(mc.thePlayer.position.x , mc.thePlayer.position.y , mc.thePlayer.position.z )

        if(Minecraft.getMinecraft().currentScreen is GuiInventory && isActive && failSafeActive){
            stopMacro()
            modMessage("&bS Shaped Macro&f has been toggled &c&lOFF&f due to a GUI being opened")
        }
        mc.currentServerData.pingToServer


        val east = mc.theWorld.getBlockState(pos.immutable.add(0.0, 0.0, 0.0).east(1)).block
        val west = mc.theWorld.getBlockState(pos.immutable.add(0.0, 0.0, 0.0).west(1)).block

        val south = mc.theWorld.getBlockState(pos.immutable.add(0.0, 0.0, 0.0).south(1)).block
        val north = mc.theWorld.getBlockState(pos.immutable.add(0.0, 0.0, 0.0).north(1)).block

        when(dir) {
            EnumFacing.NORTH -> {
                checkState(east) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
                    lastDir = 1
                }
                checkState(west)  {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
                    lastDir = 2
                }
            }

            EnumFacing.SOUTH -> {
                checkState(east) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
                    lastDir = 1
                }
                checkState(west)  {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
                    lastDir = 2
                }

            }

            EnumFacing.WEST -> {
                checkState(south)  {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
                    lastDir = 1
                }
                checkState(north)  {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
                    lastDir = 2
                }
            }

            EnumFacing.EAST -> {
                checkState(south)  {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
                    lastDir = 1
                }
                checkState(north)  {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
                    lastDir = 2
                }
            }
            else -> {}
        }
    }

    private fun timer(delay: Int, callback: () -> Unit) {
        Timer().schedule(timerTask {
            if( !isActive ) return@timerTask
            callback()
        }, delay.toLong())
    }

    @SubscribeEvent
    fun onWorldCheck(event: WorldEvent.Load){
        if(isActive){
            stopMacro()
            modMessage("&bS Shaped Macro&f has been force toggled &c&lOFF&f due to a world change")
            if(nekoconfig.discordPost){
                DiscordWebhook(nekoconfig.discordURL).setContent(getDiscordPing("Detected a world change. Disabling Macro while failsafes activate")).execute()
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
                        DiscordWebhook(nekoconfig.discordURL).setContent(getDiscordPing("Starting up **S Shaped** due to a player location correction")).execute()
                        startMacro()
                    }
                }, 10000)
            }
            lastUpdate = System.currentTimeMillis()
        }, "S Shaped Failsafe")
        thread!!.start()
    }
}

