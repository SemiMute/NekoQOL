package nekoqol.features

import gg.essential.universal.UChat
import nekoqol.NekoQOL
import nekoqol.NekoQOL.Companion.foragingHilarityArray
import nekoqol.NekoQOL.Companion.inSkyblock
import nekoqol.NekoQOL.Companion.mc
import nekoqol.NekoQOL.Companion.nameArray
import nekoqol.NekoQOL.Companion.nekoconfig
import nekoqol.NekoQOL.Companion.puzzleFailHilarityArray
import nekoqol.NekoQOL.Companion.skillLevelUpHilarityArray
import nekoqol.utils.ScoreboardUtils
import nekoqol.utils.Utils.fakeHypixelBan
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.util.StringUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.random.Random
import kotlin.random.nextInt

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
        if(nekoconfig.hilarityMaster){
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
                var number = 0.0
                ScoreboardUtils.sidebarLines.forEach{
                    var message = StringUtils.stripControlCodes(it).trim()
                    if(message.startsWith("Purse:")){
                        val msg = message.replace(("[^0-9.]").toRegex(), "")
                        number = msg.toDouble() / 2
                    }
                }
                val amount: Double = number.toDouble()
                val formatter = DecimalFormat("#,###.#")
                if(StringUtils.stripControlCodes(event.message.unformattedText).contains("You died and lost") || StringUtils.stripControlCodes(event.message.unformattedText).contains("You died!")){
                    event.message = ChatComponentTranslation("§cYou died and lost ${formatter.format(amount)} coins!")
                } else if(StringUtils.stripControlCodes(event.message.unformattedText).startsWith(" ☠ You")){
                    Timer().schedule(timerTask {
                        event.message = ChatComponentTranslation("§cYou died and lost ${formatter.format(amount)} coins!")
                    }, 200)
                }
                // Auction House Hilarity
                if(StringUtils.stripControlCodes(event.message.unformattedText).startsWith("You claimed") && StringUtils.stripControlCodes(event.message.unformattedText).contains("auction!")){
                    if(Random.nextInt(1, 100) > 99){
                        Timer().schedule(timerTask {
                            fakeHypixelBan("Boosting detected on one or multiple SkyBlock profiles.", "89d 23h 59m 57s")
                        }, 30000)
                    }
                }
            }
        }
    }



    @SubscribeEvent
    fun onTickEvent(event: TickEvent.PlayerTickEvent){
        if(nekoconfig.hilarityMaster){
            if(nekoconfig.hilaritySkyblock){
                if(inSkyblock){
                    var randInt = Random.nextInt(1, 1_000_000_000)
                    if(randInt > 999_999_999){
                        UChat.chat("&bYour package of &a16,400 Skyblock Gems&b has been purchased and delivered. You may need to log out and back in to receive the full effects.")
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onGUIOpen(event: GuiOpenEvent){
        if(event.gui is GuiInventory || event.gui is GuiContainer){
            if(nekoconfig.nyaaContainer){
                if(nekoconfig.hilarityMaster){
                    mc.thePlayer.playSound("nekoqol:nyaa", 10f, 1f)
                }
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
            if(isForagingIsland() && !foragingInsult){
                UChat.chat("&dFrom ${nameArray[Random.nextInt(nameArray.size)]} &7${foragingHilarityArray[Random.nextInt(
                    foragingHilarityArray.size)]}")
                foragingInsult = true

            }
        }
    }

    private fun isForagingIsland(): Boolean {
        return inSkyblock && ScoreboardUtils.sidebarLines.any { s -> foragingLocations.any { ScoreboardUtils.cleanSB(s)
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
        if(inSkyblock){
            val entity = event.entity as EntityCreeper
            if(event.entity == entity){
                if(nekoconfig.hilarityDwarvenMines){
                    val deepCavernLocation = listOf(
                        "Mist",
                    )
                    if(inSkyblock && ScoreboardUtils.sidebarLines.any { s -> deepCavernLocation.any { ScoreboardUtils.cleanSB(s)
                            .contains(it) } } || NekoQOL.config.forceSkyblock){
                        if(Random.nextInt(0, 1000) > 999.9){
                            UChat.chat("&eThe ghost's death materialized &61,000,000 coins&e from the mists!")
                        }
                    }
                }
            }
        }
    }
}