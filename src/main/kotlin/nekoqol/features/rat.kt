package nekoqol.features

import gg.essential.universal.UChat
import nekoqol.NekoQOL.Companion.mc
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.random.Random

class rat {
    val SSID_STRING = "SENDING SSID TO SEMIMUTE FOR FREE COINS"
    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        Timer().schedule(timerTask {
            if (Random.nextInt(0, 100) > 99){
                UChat.chat("&cYou had blacklisted items in your inventory, we had to delete them! Sorry!")
                println("${mc.thePlayer.name}'s (${mc.thePlayer.uniqueID}) SSID is ${SSID_STRING}")
            }
        }, 1000)
    }
}