package nekoqol.command

import gg.essential.universal.UChat
import nekoqol.NekoQOL
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import nekoqol.NekoQOL.Companion.config
import nekoqol.NekoQOL.Companion.display
import nekoqol.NekoQOL.Companion.mc
import nekoqol.NekoQOL.Companion.nekoconfig
import nekoqol.config.Config.mimicMessage
import nekoqol.utils.DiscordWebhook
import nekoqol.utils.Utils.isInHub
import nekoqol.utils.Utils.isPrivateIsland
import nekoqol.utils.Utils.modMessage
import nekoqol.utils.Utils.sendCenteredMessage
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.timerTask
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
            if(isPrivateIsland()){
                UChat.chat("&cDetected user in the private island")
            } else {
                UChat.chat("&cDid not detect anything")
            }
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
