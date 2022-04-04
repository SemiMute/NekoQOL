package nekoqol
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import gg.essential.api.EssentialAPI
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import nekoqol.NekoQOL.Companion.tickCount
import nekoqol.command.NekoQOLCommands
import nekoqol.config.Config
import nekoqol.config.ConfigManager.loadConfig
import nekoqol.config.ConfigManager.parseData
import nekoqol.config.ConfigManager.writeConfig
import nekoqol.config.NekoConfig
import nekoqol.features.*
import nekoqol.features.dungeons.*
import nekoqol.features.qol.SShapedMacro
import nekoqol.features.qol.SpiralMacro
import nekoqol.utils.DiscordWebhook
import nekoqol.utils.EmbedObject
import nekoqol.utils.ScoreboardUtils
import nekoqol.utils.Utils.getDiscordPing
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.client.event.sound.SoundEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.lwjgl.input.Keyboard
import java.io.File


@Mod(
    modid = NekoQOL.MOD_ID,
    name = NekoQOL.MOD_NAME,
    version = NekoQOL.MOD_VERSION,
    clientSideOnly = true
)
class NekoQOL {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        val directory = File(event.modConfigurationDirectory, "NekoQOL")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        configFile = File(directory, "config.json")
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        nekoconfig.init()

        HttpClients.createMinimal().use {
            val httpGet = HttpGet("https://gist.githubusercontent.com/SemiMute/dfb8b04e889ddffdd47291061e362f46/raw/")
            val response = EntityUtils.toString(it.execute(httpGet).entity)
            nameArray = JsonParser().parse(response).asJsonObject["NAMES"].asJsonArray
                .filterIsInstance<JsonPrimitive>()
                .map { jsonPrimitive -> jsonPrimitive.asString }
            puzzleFailHilarityArray = JsonParser().parse(response).asJsonObject["PUZZLE_FAIL_RETURN_MESSAGE"].asJsonArray
                .filterIsInstance<JsonPrimitive>()
                .map { jsonPrimitive -> jsonPrimitive.asString }
            skillLevelUpHilarityArray = JsonParser().parse(response).asJsonObject["SKILL_LEVELUP_RETURN_MESSAGE"].asJsonArray
                .filterIsInstance<JsonPrimitive>()
                .map { jsonPrimitive -> jsonPrimitive.asString }
            foragingHilarityArray = JsonParser().parse(response).asJsonObject["FORAGING_ISLAND_RETURN_MESSAGE"].asJsonArray
                .filterIsInstance<JsonPrimitive>()
                .map { jsonPrimitive -> jsonPrimitive.asString }
        }
        // Random.nextInt()

        ClientCommandHandler.instance.registerCommand(NekoQOLCommands())

        // Kelvin the developer of Skyblock Client, helped me setup his mod to learn Kotlin off of. only a few files (that are my own) will actually load/work.
        listOf(
            this,
            /*AntiBlind(),
            AnvilUses(),
            ArrowAlign(),
            BloodReady(),
            BoneMacro(),
            BookAnvilMacro(),
            EnchantingExperiments(),
            EndstoneProtectorTimer(),
            F7PreGhostBlocks(),
            FastLeap(),
            GemstoneESP(),
            GhostBlock(),
            HiddenMobs(),
            ImpactParticles(),
            ItemMacro(),
            LividESP(),
            MimicMessage(),
            MobESP(),
            NoBlockAnimation(),
            NoWaterFOV(),
            SalvageOverlay(),
            SimonSaysButtons(),
            Terminals(),
            ThornStun(),
            */WormFishingLavaESP(),
            Hilarity(),
            AutoSell(),
            SShapedMacro(),
            rat(),
            SpiralMacro()
        ).forEach(MinecraftForge.EVENT_BUS::register)

        for (keyBind in keyBinds) {
            ClientRegistry.registerKeyBinding(keyBind)
        }
    }

    @Mod.EventHandler
    fun postInit(event: FMLLoadCompleteEvent) = runBlocking {
        launch {
            configFile?.let {
                loadConfig(it)
                parseData()
                writeConfig(it)
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        tickCount++
        if (display != null) {
            mc.displayGuiScreen(display)
            display = null
        }
        if (tickCount % 20 == 0) {
            if (mc.thePlayer != null) {
                val onHypixel = EssentialAPI.getMinecraftUtil().isHypixel()

                inSkyblock = config.forceSkyblock || onHypixel && mc.theWorld.scoreboard.getObjectiveInDisplaySlot(1)
                    ?.let { ScoreboardUtils.cleanSB(it.displayName).contains("SKYBLOCK") } ?: false

                inDungeons = config.forceSkyblock || inSkyblock && ScoreboardUtils.sidebarLines.any {
                    ScoreboardUtils.cleanSB(it).run {
                        (contains("The Catacombs") && !contains("Queue")) || contains("Dungeon Cleared:")
                    }
                }
            }
            tickCount = 0
        }
    }

    @SubscribeEvent
    fun onDisconnect(event: ClientDisconnectionFromServerEvent) {
        inSkyblock = false
        inDungeons = false
    }

    companion object {
        const val MOD_ID = "nekoqol"
        const val MOD_NAME = "NekoQOL"
        const val MOD_VERSION = "0.1.1"
        const val CHAT_PREFIX = "§8[§bNeko§7QOL§8]"
        val mc: Minecraft = Minecraft.getMinecraft()
        var config = Config
        var nekoconfig = NekoConfig
        val configData = HashMap<String, JsonElement>()
        var nameArray = listOf("")
        var puzzleFailHilarityArray = listOf("")
        var skillLevelUpHilarityArray = listOf("")
        var foragingHilarityArray = listOf("")
        var configFile: File? = null
        var display: GuiScreen? = null
        var inSkyblock = false
        var inDungeons = false
        val keyBinds = arrayOf(
            KeyBinding("Toggle S Shaped Macro", Keyboard.KEY_NONE, "NekoQOL"),
            KeyBinding("Toggle Spiral Macro", Keyboard.KEY_NONE, "NekoQOL"),
            //KeyBinding("Bone Macro", Keyboard.KEY_B, "NekoQOL"),
            //KeyBinding("Ghost Block", Keyboard.KEY_G, "NekoQOL"),
            // Toggle keybinds until I make better way of doing this lol
            //KeyBinding("Toggle NoRotate", Keyboard.KEY_NONE, "NekoQOL"),
            //KeyBinding("Toggle AntiKB", Keyboard.KEY_NONE, "NekoQOL"),
        )
        var tickCount = 0
    }
}
fun main() {
    DiscordWebhook(NekoQOL.nekoconfig.discordURL).setContent(getDiscordPing("Testing Message")).execute()
}
