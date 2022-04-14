package nekoqol.command

import gg.essential.universal.UChat
import gg.essential.vigilance.gui.settings.TextComponent
import nekoqol.NekoQOL
import nekoqol.NekoQOL.Companion.config
import nekoqol.NekoQOL.Companion.display
import nekoqol.NekoQOL.Companion.mc
import nekoqol.NekoQOL.Companion.nekoconfig
import nekoqol.features.qol.SpiralMacro.Helper.facePos
import nekoqol.utils.DiscordWebhook
import nekoqol.utils.Utils.fakeHypixelBan
import nekoqol.utils.Utils.getPing
import nekoqol.utils.Utils.modMessage
import nekoqol.utils.Utils.sendCenteredMessage
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent
import java.util.*
import kotlin.random.Random


class NekoQOLCommands : CommandBase() {
    override fun getCommandName(): String {
        return "nekoqol"
    }

    override fun getCommandAliases(): List<String> {
        return listOf(
            "nekoqol",
            "nqol",
            "nyaa",
            "nq"
        )
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return "/$commandName"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            mc.thePlayer.playSound("nekoqol:nyaa", 10f, 1f)
            UChat.chat("&b&m====================================================")
            sendCenteredMessage("&b&lNeko&7&lQOL &7- &fMod Developed by &b&l§zSemiMute")
            UChat.chat("")
            UChat.chat("&7- &b/nekoqol config &9- &7Configure your settings NYA~!")
            UChat.chat("&7- &b/nekoqol filter &9- &c(( DISABLED FEATURE ))")
            UChat.chat("&7- &b/nekoqol testWebhook &9- &7Sends a test webhook message!")
            UChat.chat("&7- &b/nekoqol limbo &9- &7Forces you into limbo")
            UChat.chat("&7- &b/nekoqol credits&9 - &7Shows off some wonderful people!")
            UChat.chat("")
            UChat.chat("&b&m====================================================")
            return
        }
        if(args[0] == "credits") {
            mc.thePlayer.playSound("nekoqol:nyaa", 10f, 1f)
            UChat.chat("&b&m====================================================")
            sendCenteredMessage("&b&lNekoQOL &7- &fCredits")
            UChat.chat("&7Some special thanks to those who've helped!")
            UChat.chat("")
            UChat.chat("&7- &b&l[DEVELOPER] &bDer_s &7Debugging is pain")
            UChat.chat("&7- &bAzael_Mew &7Helped get the project started in ChatTriggers!")
            UChat.chat("&7- &b0Kelvin_ &7Helped me learn Kotlin, the reason its now a Mod!")
            UChat.chat("")
            UChat.chat("&b&m====================================================")
            return
        }
        if(args[0] == "limbo"){
            modMessage("&cAttempting to force client into limbo..")
            mc.thePlayer.sendChatMessage("§")
        }
        if(args[0] == "testWebhook"){
            if(nekoconfig.discordURL == ""){
                modMessage("&cERROR: &fGive me a Discord Webhook via &b/nekoqol config")
                return
            }
            mc.thePlayer.playSound("nekoqol:nyaa", 10f, 1f)
            modMessage("&fAttempting to send a &3Discord Webhook&r message..")
            DiscordWebhook(nekoconfig.discordURL).setContent("**NYAA!** This is a test message to make sure your webhook URL is set correctly!").execute()
        }
        if(args[0] == "testWebhookPing"){
            if(nekoconfig.discordURL == ""){
                modMessage("&cERROR: &fGive me a Discord Webhook via &b/nekoqol config")
                return
            }
            mc.thePlayer.playSound("nekoqol:nyaa", 10f, 1f)
            modMessage("&fAttempting to send a &3Discord Webhook&f message while pinging..")
            DiscordWebhook(nekoconfig.discordURL).setContent("<@${NekoQOL.nekoconfig.discordID}> **NYAA!** This is a test message to make sure your webhook URL with a ping is setup!").execute()
        }
        if(args[0] == "test"){
            if(args[1] == "hilarityGems"){
                modMessage("&cForcing hilarity type FakeGemEvent")
                UChat.chat("&bYour package of &a16,400 Skyblock Gems&b has been purchased and delivered. You may need to log out and back in to receive the full effects.")
            }
            if(args[1] == "ping"){
                UChat.chat("&b&lNYAA DEBUGGER: &fPing == ${getPing()}")
            }
            if(args[1] == "antiadmin"){
                val randLocations = listOf(
                    "0;0",
                    "100;100",
                    "73;0",
                    "35;100"
                )
                var locChoice = randLocations[Random.nextInt(randLocations.size)]
                var pos = BlockPos(1111, 69, 100)
                facePos(pos)
                modMessage("&c&lDEBUGGER: &fStarting &cAnti-Admin&f Failsafe test.")
            }
            if(args[1] == "disconnectTest"){
                fakeHypixelBan("Boosting detected on one or multiple SkyBlock profiles.", "89d 23h 59m 57s")
            }
        }
        if(args[0] == "filter"){
            modMessage("&cFilter no work sorreh")
            mc.gameSettings.renderDistanceChunks = 999
        }
        if (args[0].lowercase() == "config"){
            mc.thePlayer.playSound("nekoqol:nyaa", 10f, 1f)
            display = nekoconfig.gui()
        }
        if (args[0].lowercase() == "configLegacy"){
            modMessage("&cOpening the legacy config from SkyblockClient")
            display = config.gui()
        }
    }
}
