package nekoqol.features

import gg.essential.universal.UChat
import nekoqol.NekoQOL
import nekoqol.NekoQOL.Companion.foragingHilarityArray
import nekoqol.NekoQOL.Companion.nameArray
import nekoqol.NekoQOL.Companion.puzzleFailHilarityArray
import nekoqol.NekoQOL.Companion.skillLevelUpHilarityArray
import nekoqol.utils.ScoreboardUtils
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.util.StringUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.random.Random

class Hilarity {
    private val foragingLocations = listOf(
        "Birch",
        "Spruce",
        "Dark",
        "Jungle",
        "Savanna",
    )

    @SubscribeEvent
    fun onChatEvent(event: ClientChatReceivedEvent) {
        if(NekoQOL.nekoconfig.hilarityDungeons){
            // FAIL A PUZZLE HILARITY
            onChatString(event, "PUZZLE FAIL!"){
                UChat.chat("&dFrom &c${nameArray[Random.nextInt(nameArray.size)]} &7${puzzleFailHilarityArray[Random.nextInt(
                    puzzleFailHilarityArray.size)]}")
            }
        }
        if(NekoQOL.nekoconfig.hilaritySkyblock){

            // SKILL LEVEL UP INSULTING
            if(StringUtils.stripControlCodes(event.message.unformattedText).contains("SKILL LEVEL UP") && StringUtils.stripControlCodes(event.message.unformattedText).contains("➜")){
                UChat.chat("&dFrom &c${nameArray[Random.nextInt(nameArray.size)]} &7${skillLevelUpHilarityArray[Random.nextInt(skillLevelUpHilarityArray.size)]}")
            }

            // ON DEATH COIN LOSS HILARITY
            val randNum = Random.nextInt(100000, 100000000)
            val amount: Double = randNum.toDouble()
            val formatter = DecimalFormat("#,###.00")
            if(StringUtils.stripControlCodes(event.message.unformattedText).contains("☠ You")){
                Timer().schedule(timerTask {
                    UChat.chat("&cYou died and lost ${formatter.format(amount)} coins!")
                }, 200)
            }
        }
    }
    private var foragingInsult = false
    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        foragingInsult = false
    }

    @SubscribeEvent
    fun renderWorld(event: RenderWorldLastEvent){
        if(NekoQOL.nekoconfig.hilarityForaging){
            if(isForagingIsland() && foragingInsult !== true){
                UChat.chat("&dFrom ${nameArray[Random.nextInt(nameArray.size)]} &7${foragingHilarityArray[Random.nextInt(
                    foragingHilarityArray.size)]}")
                foragingInsult = true
            }
        }
    }

    private fun isForagingIsland(): Boolean {
        return NekoQOL.inSkyblock && ScoreboardUtils.sidebarLines.any { s -> foragingLocations.any { ScoreboardUtils.cleanSB(s)
            .contains(it) } } || NekoQOL.config.forceSkyblock
    }

    private fun onChatString(event: ClientChatReceivedEvent, startsWith: String, callback: () -> Unit){
        if (StringUtils.stripControlCodes(event.message.unformattedText).startsWith(startsWith)) run {
            callback.invoke()
        }
    }

    // GHOST HILARITY
    @SubscribeEvent
    fun onCreeperDeath(event: LivingDeathEvent){
        val entity = event.entity as EntityCreeper
        if(event.entity == entity){
            if(NekoQOL.nekoconfig.hilarityDwarvenMines){
                val deepCavernLocation = listOf(
                    "Mist",
                )
                if(NekoQOL.inSkyblock && ScoreboardUtils.sidebarLines.any { s -> deepCavernLocation.any { ScoreboardUtils.cleanSB(s)
                        .contains(it) } } || NekoQOL.config.forceSkyblock){
                    if(Random.nextInt(0, 100) > 99.9){
                        UChat.chat("&eThe ghost's death materialized &61,000,000 coins&e from the mists!")
                    }
                }
            }
        }
    }
}