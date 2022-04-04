package nekoqol.features.qol

import nekoqol.NekoQOL
import nekoqol.NekoQOL.Companion.mc
import nekoqol.features.qol.Helper.to180
import nekoqol.utils.DiscordWebhook
import nekoqol.utils.Utils
import nekoqol.utils.Utils.modMessage
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.StringUtils
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.atan2
import kotlin.math.sqrt

class SpiralMacro {

    var isActive = false
    var failsafeActive = false

    var thread: Thread? = null
    var lastUpdate = 0L

    var onWorldCooldown: Long = 0

    fun startMacro() {
        failsafeActive = true
        isActive = true

        working = false

        if(mc.thePlayer.capabilities.isFlying){
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, true)
            Timer().schedule(timerTask {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false)
            }, 1000)
        }

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.keyCode, true)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, true)

        when(mc.thePlayer.horizontalFacing) {
            EnumFacing.EAST -> {
                mc.thePlayer.rotationYaw = -90f
                mc.thePlayer.rotationPitch = 0f
            }
            EnumFacing.WEST -> {
                mc.thePlayer.rotationYaw = 90f
                mc.thePlayer.rotationPitch = 0f
            }
            EnumFacing.NORTH -> {
                mc.thePlayer.rotationYaw = 180f
                mc.thePlayer.rotationPitch = 0f
            }
            EnumFacing.SOUTH -> {
                mc.thePlayer.rotationYaw = 0f
                mc.thePlayer.rotationPitch = 0f
            }
        }
    }

    fun stopMacro() {
        failsafeActive = false
        isActive = false

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.keyCode, false)
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false)
    }

    @SubscribeEvent
    fun onKeyPress(event: InputEvent.KeyInputEvent) {
        if(NekoQOL.keyBinds[1].isPressed) {
            if(!isActive) {
                Utils.modMessage("Starting macro...")
                startMacro()
            } else {
                Utils.modMessage("Stopping macro...")
                stopMacro()
            }
        }
    }

    @SubscribeEvent
    fun onMove(event: TickEvent.PlayerTickEvent) {
        if(!isActive) return

        checkBlocks()
    }

    private val blocksValid = arrayOf(
        Blocks.farmland,
        Blocks.soul_sand
    )

    private fun checkState(block: Block, callback: () -> Unit) {
        if(blocksValid.contains(block)) callback()
    }

    private fun checkBlocks() {
        if(working) return

        val dir = mc.thePlayer.horizontalFacing
        val pos = BlockPos(mc.thePlayer.position.x , mc.thePlayer.position.y , mc.thePlayer.position.z )

        if(Minecraft.getMinecraft().currentScreen is GuiInventory && isActive && failsafeActive){
            stopMacro()
            Utils.modMessage("&bSpiral Macro&f has been toggled &c&lOFF&f due to a GUI being opened")
        }

        val east = mc.theWorld.getBlockState(pos.immutable.add(0.0, 0.0, -1.0).east(1)).block
        val west = mc.theWorld.getBlockState(pos.immutable.add(-1.0, 0.0, 0.0).west(1)).block

        val south = mc.theWorld.getBlockState(pos.immutable.add(0.0, 0.0, 0.0).south(1)).block
        val north = mc.theWorld.getBlockState(pos.immutable.add(-1.0, 0.0, 0.0).north(1)).block

        when(dir) {
            EnumFacing.NORTH -> {
                checkState(west)  {
                    rotate(-90, pos.immutable.west(1))
                }
                return
            }

            EnumFacing.SOUTH -> {
                checkState(east) {
                    rotate(-90, pos.immutable.east(1))
                }
                return
            }

            EnumFacing.WEST -> {
                checkState(south)  {
                    rotate(-90, pos.immutable.south(1))
                }
                return
            }

            EnumFacing.EAST -> {
                checkState(north)  {
                    rotate(-90, pos.immutable.north(1))
                }
                return
            }
            else -> {}
        }
    }

    var working = false

    fun rotate(amount: Int, block: BlockPos) {
        var toTurn = amount
        var turned = 0

        var vec = Vec3(block.x.toDouble(), block.y.toDouble(), block.z.toDouble())

        var x = mc.thePlayer.posX
        var y = mc.thePlayer.posY
        var z = mc.thePlayer.posZ

        if(!working) {
            working = true
            Thread {
                try {
                    val diffX = vec.xCoord - x
                    val diffY = vec.yCoord - y
                    val diffZ = vec.zCoord - z

                    val dist = sqrt(diffX * diffX + diffZ * diffZ)

                    var pitch = -atan2(dist, diffY)
                    var yaw = atan2(diffZ, diffX)

                    pitch = Helper.to180(((pitch * 180.0) / Math.PI + 90.0) * -1.0 - mc.thePlayer.rotationPitch)
                    yaw = Helper.to180((yaw * 180.0) / Math.PI - 90.0 - mc.thePlayer.rotationYaw)

                    for (i in 0 until  50) {
                        mc.thePlayer.rotationYaw += (to180(-90.0) / 50.0).toFloat()
                        mc.thePlayer.rotationPitch = 0.0f
                        Thread.sleep(10)
                    }
                    timer(2000) {
                        working = false
                    }
                } catch (e : Exception) {
                    timer(2000) {
                        working = false
                    }
                    return@Thread
                }
            }.start()
        }
    }

    private fun timer(delay: Int, callback: () -> Unit) {
        Timer().schedule(timerTask {
            callback()
        }, delay.toLong())
    }

    @SubscribeEvent
    fun onWorldCheck(event: WorldEvent.Load){
        if(isActive){
            stopMacro()
            modMessage("&Spiral Macro&f has been force toggled &c&lOFF&f due to a world change")
            if(NekoQOL.nekoconfig.discordPost){
                DiscordWebhook(NekoQOL.nekoconfig.discordURL).setContent(Utils.getDiscordPing("Detected a world change. Disabling Macro while failsafes activate")).execute()
            }
        }
        onWorldCooldown = System.currentTimeMillis()
    }
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START || failsafeActive && Utils.isPrivateIsland() && onWorldCooldown + 1500 <= System.currentTimeMillis()) return
        if (thread?.isAlive == true || lastUpdate + 2500 > System.currentTimeMillis()) return
        thread = Thread({
            if(failsafeActive){
                if(Utils.isInLobby()){
                    modMessage("&cFAILSAFE: &fPlayer is in &7⏣ &6Hypixel Lobby&f...\n&7Attempting to correct players' position...")
                    mc.thePlayer.sendChatMessage("/play skyblock")
                }
                if(Utils.isInHub()){
                    modMessage("&cFAILSAFE: &fPlayer is in the &7⏣ &bSkyblock Village&f..\n&7Attempting to correct players' position...")
                    mc.thePlayer.sendChatMessage("/warp home")
                }
                if(Utils.isInLimbo()){
                    modMessage("&cFAILSAFE: &fPlayer is in the &7⏣ &cLimbo&f..\n&7Attempting to correct players' position...")
                    mc.thePlayer.sendChatMessage("/lobby")
                }
                Timer().schedule(timerTask {
                    if(Utils.isPrivateIsland()){
                        if(isActive) {
                            return@timerTask
                        }
                        modMessage("&cFAILSAFE: &fPlayer is in &7⏣ &aPrivate Island&f\n&7Attempting to start up Spiral Macro")
                        DiscordWebhook(NekoQOL.nekoconfig.discordURL).setContent(Utils.getDiscordPing("Starting up **S Shaped** due to a player location correction")).execute()
                        startMacro()
                    }
                }, 10000)
            }
            lastUpdate = System.currentTimeMillis()
        }, "Spiral Failsafe")
        thread!!.start()
    }

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if(!failsafeActive) return

        if(StringUtils.stripControlCodes(event.message.unformattedText).startsWith("[Important]")) {
            timer(1000) {
                stopMacro()
            }

            timer(1100) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.keyCode, true)
            }

            timer(1150) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.keyCode, false)
            }

            timer(2000) {
                mc.thePlayer.sendChatMessage("/setspawn")
            }

            timer(5000) {
                mc.thePlayer.sendChatMessage("/hub")
            }

            timer(60000) {
                mc.thePlayer.sendChatMessage("/is")
            }

            timer(1100) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, true)
            }

            timer(70000) {
                startMacro()
            }
        }
    }




}
object Helper {
    var working = false

    fun facePos(blockPos: BlockPos, pitch: Boolean = false) {
        facePos(Vec3(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()), pitch)
    }

    fun facePos(vec: Vec3, pitchF: Boolean = false) {
        if(mc.currentScreen == null || mc.currentScreen is GuiIngameMenu || mc.currentScreen is GuiChat) {
            if(!working) {
                Thread {
                    try {
                        working = true
                        val diffX = vec.xCoord - mc.thePlayer.posX
                        val diffY = vec.yCoord - mc.thePlayer.posY
                        val diffZ = vec.zCoord - mc.thePlayer.posZ

                        val dist = sqrt(diffX * diffX + diffZ * diffZ)

                        var pitch = -atan2(dist, diffY)
                        var yaw = atan2(diffZ, diffX)

                        pitch = to180(((pitch * 180.0) / Math.PI + 90.0) * - 1.0 - mc.thePlayer.rotationPitch)
                        yaw = to180((yaw * 180.0) / Math.PI - 90.0 - mc.thePlayer.rotationYaw)

                        var p = 0.0f
                        if(pitchF){
                            p = 0.0f
                        } else {
                            p = (pitch / 50.0).toFloat()
                        }

                        for (i in 0 until  50) {
                            mc.thePlayer.rotationYaw += (yaw / 50.0).toFloat()
                            mc.thePlayer.rotationPitch = (pitch / 50.0).toFloat()
                            Thread.sleep(10)
                        }
                        working = false
                    } catch (e : Exception) {
                        return@Thread
                    }
                }.start()
            }
        }
    }

    fun to180(a: Double): Double {
        var angle = a
        angle %= 360.0
        while(angle > 180.0) {
            angle -= 360.0
        }
        while(angle < -180.0) {
            angle += 360.0
        }
        return angle
    }

}