package nekoqol.features.qol

import kotlinx.coroutines.NonCancellable.isActive
import nekoqol.NekoQOL
import nekoqol.utils.DiscordWebhook
import nekoqol.utils.Utils
import nekoqol.utils.Utils.modMessage
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*
import kotlin.concurrent.timerTask

class StrandedQOL {
    private var isActive = false
    var failSafeActive = false

    var thread: Thread? = null
    var lastUpdate = 0L

    var onWorldCooldown: Long = 0

    @SubscribeEvent
    fun onKeyPress(event: InputEvent.KeyInputEvent) {
        if(NekoQOL.keyBinds[11].isPressed) {
            if(!isActive) {
                modMessage("&fYou will now hold &bLEFT CLICK&f untill toggled")
                KeyBinding.setKeyBindState(NekoQOL.mc.gameSettings.keyBindAttack.keyCode, true)
                isActive = true
            } else {
                modMessage("&fYou will no longer hold &bLEFT CLICK&f")
                KeyBinding.setKeyBindState(NekoQOL.mc.gameSettings.keyBindAttack.keyCode, false)
                isActive = false
            }
        }
    }

    @SubscribeEvent
    fun onWorldCheck(event: WorldEvent.Load){
        if(isActive){
            modMessage("&bLeft Click Macro&f has been force toggled &c&lOFF&f due to a world change")
            KeyBinding.setKeyBindState(NekoQOL.mc.gameSettings.keyBindAttack.keyCode, false)
            isActive = false
            if(NekoQOL.nekoconfig.discordPost){
                DiscordWebhook(NekoQOL.nekoconfig.discordURL).setContent(Utils.getDiscordPing("Detected a world change. Disabling Macro while failsafes activate")).execute()
            }
        }
        onWorldCooldown = System.currentTimeMillis()
    }
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START || isActive && Utils.isPrivateIsland() && onWorldCooldown + 1500 <= System.currentTimeMillis()) return
        if (thread?.isAlive == true || lastUpdate + 2500 > System.currentTimeMillis()) return
        thread = Thread({
            if(failSafeActive){
                if(Utils.isInLobby()){
                    modMessage("&cFAILSAFE: &fPlayer is in &7⏣ &6Hypixel Lobby&f...\n&7Attempting to correct players' position...")
                    NekoQOL.mc.thePlayer.sendChatMessage("/play skyblock")
                }
                if(Utils.isInHub()){
                    modMessage("&cFAILSAFE: &fPlayer is in the &7⏣ &bSkyblock Village&f..\n&7Attempting to correct players' position...")
                    NekoQOL.mc.thePlayer.sendChatMessage("/warp home")
                }
                if(Utils.isInLimbo()){
                    modMessage("&cFAILSAFE: &fPlayer is in the &7⏣ &cLimbo&f..\n&7Attempting to correct players' position...")
                    NekoQOL.mc.thePlayer.sendChatMessage("/lobby")
                }
                Timer().schedule(timerTask {
                    if(Utils.isPrivateIsland()){
                        if(isActive) {
                            return@timerTask
                        }
                        modMessage("&cFAILSAFE: &fPlayer is in &7⏣ &aPrivate Island&f\n&7Attempting to start up S Shaped Macro")
                        DiscordWebhook(NekoQOL.nekoconfig.discordURL).setContent(Utils.getDiscordPing("Starting up **S Shaped** due to a player location correction")).execute()
                    }
                }, 10000)
            }
            lastUpdate = System.currentTimeMillis()
        }, "S Shaped Failsafe")
        thread!!.start()
    }
}