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
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.roundToInt
import kotlin.random.Random

var isActive = false
var failSafeActive = false;
var autoReconnect = false
var shouldLeftClick = false

private var lastY = 0.0
private var getBlock = 0.0
var getBlockZ = 0f
var getBlockX = 0f
var lastTurnAround = System.currentTimeMillis()
var yDecrease = false
var sendWebhook = false
var lastdir = 1

class SShapedMacro {
    private var thread: Thread? = null
    private var lastUpdate: Long = 0
    private var sShapedThread: Thread? = null
    private var sLastUpdate: Long = 0


    fun startMacro() {
        isActive = true
        failSafeActive = true
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
        if(lastdir == 1){
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
        }
        updateY()
    }

    fun updateY() {
        Timer().schedule(timerTask {
            lastY = mc.thePlayer.posY
            if(isActive){
                updateY()
            }
        }, 1000)
    }
    @SubscribeEvent
    fun onKeyPress(event: InputEvent.KeyInputEvent) {
        if (keyBinds[0].isPressed) {
            if (isActive || failSafeActive) {
                isActive = false
                failSafeActive = false
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
                modMessage("&bS Shaped Macro&f has been toggled &c&lOFF&f!")
                yDecrease = false
                sendWebhook = false
            } else {
                if(!isPrivateIsland()){
                    return UChat.chat("&dFrom ${nameArray[Random.nextInt(nameArray.size)]} &7What, you want to farm some air? Go to your (&7⏣ &aPrivate Island&7) to start the &bS Shaped Macro&7...")
                }
                isActive = true;
                failSafeActive = true
                //KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, true)
                modMessage("&bS Shaped Macro &fhas been toggled &a&lON&f!")
                startMacro()
                lastdir = 1
                if(NekoQOL.nekoconfig.discordURL == "" && NekoQOL.nekoconfig.discordPost){
                    modMessage("&cERROR: It seems you are wanting to get discord notifications, but dont have a Webhook setup! Please correct this and try again...")
                    isActive = false
                    failSafeActive = false
                    modMessage("&bS Shaped Macro&f has been force toggled &c&lOFF&f due to an &cERROR&f occurring")
                    return
                }
                //KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
            }
        }
    }
    var onWorldCooldown: Long = 0

    @SubscribeEvent
    fun onWorldCheck(event: WorldEvent.Load){
        if(isActive){
            isActive = false
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

    @SubscribeEvent
    fun onMacro(event: TickEvent.PlayerTickEvent){
        getBlock = mc.thePlayer.posY

        getBlockZ = mc.thePlayer.posZ.roundToInt().toFloat()
        getBlockX = mc.thePlayer.posX.roundToInt().toFloat()
        if(Minecraft.getMinecraft().currentScreen is GuiInventory && isActive && failSafeActive){
            isActive = false
            failSafeActive = false
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
            modMessage("&bS Shaped Macro&f has been toggled &c&lOFF&f due to a GUI being opened")
        }
        if(isActive){
            mc.thePlayer.rotationPitch = NekoQOL.nekoconfig.sShapedPitch
            if(NekoQOL.nekoconfig.sShapedYaw == 0){
                mc.thePlayer.rotationYaw = 180F
            } else if(NekoQOL.nekoconfig.sShapedYaw == 1){
                mc.thePlayer.rotationYaw = 0F
            }else if(NekoQOL.nekoconfig.sShapedYaw == 2){
                mc.thePlayer.rotationYaw = -90F
            }else if(NekoQOL.nekoconfig.sShapedYaw == 3){
                mc.thePlayer.rotationYaw = 90F
            }
            if(isActive) {
                if(lastY !== getBlock){
                    if(System.currentTimeMillis() - lastTurnAround > 1000){
                        lastTurnAround = System.currentTimeMillis()
                        if(mc.gameSettings.keyBindLeft.isKeyDown){
                            if(!yDecrease){
                                yDecrease = true
                                Timer().schedule(timerTask {
                                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
                                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
                                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
                                    modMessage("&cActivated RIGHT DIRECTION")
                                    mc.thePlayer.sendChatMessage("/sethome")
                                    yDecrease = false
                                    lastdir = 1
                                }, 1000)
                            }
                        } else if(mc.gameSettings.keyBindRight.isKeyDown){
                            if(!yDecrease){
                                yDecrease = true
                                Timer().schedule(timerTask {
                                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
                                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
                                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
                                    modMessage("&cActivated LEFT DIRECTION")
                                    mc.thePlayer.sendChatMessage("/sethome")
                                    yDecrease = false
                                    lastdir = 2
                                }, 1000)
                            }
                        }
                    }
                }
                if(System.currentTimeMillis() - lastTurnAround > 5000){
                    lastTurnAround = System.currentTimeMillis()
                    if(mc.gameSettings.keyBindLeft.isKeyDown){
                        if(!yDecrease){
                            yDecrease = true
                            Timer().schedule(timerTask {
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, false)
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, true)
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
                                modMessage("&cActivated RIGHT DIRECTION OTHER")
                                mc.thePlayer.sendChatMessage("/sethome")
                                yDecrease = false
                            }, 250)
                            lastdir = 1
                        }
                    } else if(mc.gameSettings.keyBindRight.isKeyDown){
                        if(!yDecrease){
                            yDecrease = true
                            Timer().schedule(timerTask {
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.keyCode, false)
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.keyCode, true)
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
                                modMessage("&cActivated LEFT DIRECTION OTHER")
                                mc.thePlayer.sendChatMessage("/sethome")
                                yDecrease = false
                            }, 250)
                            lastdir = 2
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load){
        Timer().schedule(timerTask {
            val world = event.world

            if(world !== mc.theWorld){
                return@timerTask
            }
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