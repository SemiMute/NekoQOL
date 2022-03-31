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
            "nyaa"
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
            UChat.chat("&b&m====================================================")
            sendCenteredMessage("&b&lNeko&7&lQOL &7- &fMod Developed by &b&l§zSemiMute")
            UChat.chat("")
            UChat.chat("&7- &b/nekoqol config &9- &7Configure your settings NYA~!")
            UChat.chat("&7- &b/nekoqol filter &9- &c(( DISABLED FEATURE ))")
            UChat.chat("&7- &b/nekoqol testWebhook &9- &7Sends a test webhook message!")
            UChat.chat("&7- &b/nekoqol credits&9 - &7Shows off some wonderful people!")
            UChat.chat("")
            UChat.chat("&b&m====================================================")
            return
        }
        if(args[0] == "credits") {
            UChat.chat("&b&m====================================================")
            sendCenteredMessage("&b&lNekoQOL &7- &fCredits")
            UChat.chat("&7Some special thanks to those who've helped!")
            UChat.chat("")
            UChat.chat("&7- &bAzael_Mew &7Helped get the project started in ChatTriggers!")
            UChat.chat("&7- &b0Kelvin_ &7Helped me learn Kotlin, the reason its now a Mod!")
            UChat.chat("&7- &bDer_s &7Debugging is pain")
            UChat.chat("")
            UChat.chat("&b&m====================================================")
            return
        }
        if(args[0] == "isInHub"){
            if(isInHub()){
                UChat.chat("You're in the hub, congrats")
            } else {
                UChat.chat("Not in the hub, L bozo")
            }
        }
        if(args[0] == "testWebhook"){
            modMessage("Attempting to send a &3Discord Webhook&r message..")
            DiscordWebhook(nekoconfig.discordURL).setContent("**NYAA!** This is a test message to make sure your webhook URL is set correctly!").execute()
        }
        if(args[0] == "testWebhookPing"){
            modMessage("Attempting to send a &3Discord Webhook&f message while pinging..")
            DiscordWebhook(nekoconfig.discordURL).setContent("<@${NekoQOL.nekoconfig.discordID}> **NYAA!** This is a test message to make sure your webhook URL with a ping is setup!").execute()
        }
        if(args[0] == "disconnectHandler"){
            UChat.chat("&cAttempting a disconnect packet")
            mc.theWorld.sendQuittingDisconnectingPacket()
        }
        if( args[0].lowercase() == "test"){
            val randNum = Random.nextInt(100000, 100000000)
            val amount: Double = randNum.toDouble()
            val formatter = DecimalFormat("#,###.0")
            UChat.chat("&cYou died and lost ${formatter.format(amount)} coins!")
            return
        }
        if (args[0].lowercase() == "config"){
            display = nekoconfig.gui()
        }
        if (args[0].lowercase() == "configLegacy"){
            modMessage("&cOpening the legacy config from SkyblockClient")
            display = config.gui()
        }
        val subcommand = args[0].lowercase()
        if (subcommand == "mimicmessage") {
            return modMessage("&chi ;3")
            args[0] = ""
            val message = args.joinToString(" ").trim()
            mimicMessage = message
            modMessage("§aMimic message changed to §f$message")
        }
    }
}
