package nekoqol.utils

import gg.essential.api.EssentialAPI
import gg.essential.universal.ChatColor
import gg.essential.universal.UChat
import nekoqol.NekoQOL
import nekoqol.NekoQOL.Companion.CHAT_PREFIX
import nekoqol.NekoQOL.Companion.config
import nekoqol.NekoQOL.Companion.mc
import nekoqol.utils.ScoreboardUtils.sidebarLines
import net.minecraft.block.*
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor
import kotlin.math.round


object Utils {
    fun Any?.equalsOneOf(vararg other: Any): Boolean {
        return other.any {
            this == it
        }
    }

    fun isFloor(floor: Int): Boolean {
        sidebarLines.forEach {
            val line = ScoreboardUtils.cleanSB(it)
            if (line.contains("The Catacombs (")) {
                if (line.substringAfter("(").substringBefore(")").equalsOneOf("F$floor", "M$floor")) {
                    return true
                }
            }
        }
        return config.forceSkyblock && mc.thePlayer != null && mc.theWorld != null
    }

    val ItemStack.itemID: String
        get() {
            if (this.hasTagCompound() && this.tagCompound.hasKey("ExtraAttributes")) {
                val attributes = this.getSubCompound("ExtraAttributes", false)
                if (attributes.hasKey("id", 8)) {
                    return attributes.getString("id")
                }
            }
            return ""
        }

    val ItemStack.lore: List<String>
        get() {
            if (this.hasTagCompound() && this.tagCompound.hasKey("display", 10)) {
                val display = this.tagCompound.getCompoundTag("display")
                if (display.hasKey("Lore", 9)) {
                    val nbt = display.getTagList("Lore", 8)
                    val lore = ArrayList<String>()
                    (0..nbt.tagCount()).forEach {
                        lore.add(nbt.getStringTagAt(it))
                    }
                    return lore
                }
            }
            return emptyList()
        }

    fun modMessage(message: String) = UChat.chat("$CHAT_PREFIX $message")

    fun renderText(
        text: String,
        x: Int,
        y: Int,
        scale: Double = 1.0,
        color: Int = 0xFFFFFF
    ) {
        GlStateManager.pushMatrix()
        GlStateManager.disableLighting()
        GlStateManager.disableDepth()
        GlStateManager.disableBlend()
        GlStateManager.scale(scale, scale, scale)
        var yOffset = y - mc.fontRendererObj.FONT_HEIGHT
        text.split("\n").forEach {
            yOffset += (mc.fontRendererObj.FONT_HEIGHT * scale).toInt()
            mc.fontRendererObj.drawString(
                it,
                round(x / scale).toFloat(),
                round(yOffset / scale).toFloat(),
                color,
                true
            )
        }
        GlStateManager.popMatrix()
    }

    fun rightClick() {
        val method = try {
            Minecraft::class.java.getDeclaredMethod("func_147121_ag")
        } catch (e: NoSuchMethodException) {
            Minecraft::class.java.getDeclaredMethod("rightClickMouse")
        }
        method.isAccessible = true
        method.invoke(Minecraft.getMinecraft())
    }

    fun leftClick() {
        val method = try {
            Minecraft::class.java.getDeclaredMethod("func_147116_af")
        } catch (e: NoSuchMethodException) {
            Minecraft::class.java.getDeclaredMethod("clickMouse")
        }
        method.isAccessible = true
        method.invoke(Minecraft.getMinecraft())
    }

    fun getChatWidth(): Any? {
        val method = Minecraft::class.java.getDeclaredField("field_216699_f")
        method.isAccessible = true
        return method
    }

    val CENTER_PX: Int = 320 / 2

    fun sendCenteredMessage(message: String) {
        var message = message
        if (message == null || message == "") UChat.chat("")
        message = ChatColor.translateAlternateColorCodes('&', message);
        var messagePxSize = 0
        var previousCode = false
        var isBold = false
        for (c in message!!.toCharArray()) {
            if (c == '§') {
                previousCode = true
                continue
            } else if (previousCode == true) {
                previousCode = false
                if (c == 'l' || c == 'L') {
                    isBold = true
                    continue
                } else isBold = false
            } else {
                val dFI: DefaultFontInfo = DefaultFontInfo.getDefaultFontInfo(c)
                messagePxSize += if (isBold) dFI.getBoldLength() else dFI.length
                messagePxSize++
            }
        }
        val halvedMessageSize = messagePxSize / 2
        val toCompensate = CENTER_PX - halvedMessageSize
        val spaceLength = DefaultFontInfo.SPACE.length + 1
        var compensated = 0
        val sb = StringBuilder()
        while (compensated < toCompensate) {
            sb.append(" ")
            compensated += spaceLength
        }
        UChat.chat(sb.toString() + message)
    }
    enum class DefaultFontInfo(val character: Char, val length: Int) {

        A('A', 5),
        a('a', 5),
        B('B', 5),
        b('b', 5),
        C('C', 5),
        c('c', 5),
        D('D', 5),
        d('d', 5),
        E('E', 5),
        e('e', 5),
        F('F', 5),
        f('f', 4),
        G('G', 5),
        g('g', 5),
        H('H', 5),
        h('h', 5),
        I('I', 3),
        i('i', 1),
        J('J', 5),
        j('j', 5),
        K('K', 5),
        k('k', 4),
        L('L', 5),
        l('l', 1),
        M('M', 5),
        m('m', 5),
        N('N', 5),
        n('n', 5),
        O('O', 5),
        o('o', 5),
        P('P', 5),
        p('p', 5),
        Q('Q', 5),
        q('q', 5),
        R('R', 5),
        r('r', 5),
        S('S', 5),
        s('s', 5),
        T('T', 5),
        t('t', 4),
        U('U', 5),
        u('u', 5),
        V('V', 5),
        v('v', 5),
        W('W', 5),
        w('w', 5),
        X('X', 5),
        x('x', 5),
        Y('Y', 5),
        y('y', 5),
        Z('Z', 5),
        z('z', 5),
        NUM_1('1', 5),
        NUM_2('2', 5),
        NUM_3('3', 5),
        NUM_4('4', 5),
        NUM_5('5', 5),
        NUM_6('6', 5),
        NUM_7('7', 5),
        NUM_8('8', 5),
        NUM_9('9', 5),
        NUM_0('0', 5),
        EXCLAMATION_POINT('!', 1),
        AT_SYMBOL('@', 6),
        NUM_SIGN('#', 5),
        DOLLAR_SIGN('$', 5),
        PERCENT('%', 5),
        UP_ARROW('^', 5),
        AMPERSAND('&', 5),
        ASTERISK('*', 5),
        LEFT_PARENTHESIS('(', 4),
        RIGHT_PERENTHESIS(')', 4),
        MINUS('-', 5),
        UNDERSCORE('_', 5),
        PLUS_SIGN('+', 5),
        EQUALS_SIGN('=', 5),
        LEFT_CURL_BRACE('{', 4),
        RIGHT_CURL_BRACE('}', 4),
        LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3),
        COLON(':', 1),
        SEMI_COLON(';', 1),
        DOUBLE_QUOTE('"', 3),
        SINGLE_QUOTE('\'', 1),
        LEFT_ARROW('<', 4),
        RIGHT_ARROW('>', 4),
        QUESTION_MARK('?', 5),
        SLASH('/', 5),
        BACK_SLASH('\\', 5),
        LINE('|', 1),
        TILDE('~', 5),
        TICK('`', 2),
        PERIOD('.', 1),
        COMMA(',', 1),
        SPACE(' ', 3),
        DEFAULT('a', 4);

        fun getBoldLength(): Int {
            if (this == DefaultFontInfo.SPACE) return this.length;
            return this.length + 1;
        }

        companion object {
            fun getDefaultFontInfo(c: Char): DefaultFontInfo
            {
                values().forEach {
                    if (it.character == c) return it;
                }
                return DefaultFontInfo.DEFAULT;
            }
        }
    }

    object CropUtilities {
        //#if MC==10809
        private val mc = Minecraft.getMinecraft()
        private val minecraftUtils = EssentialAPI.getMinecraftUtil()
        val CARROT_POTATO_BOX = arrayOf(
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.375, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.4375, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5625, 1.0)
        )
        val WHEAT_BOX = arrayOf(
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.375, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.625, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.75, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.875, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        )
        val NETHER_WART_BOX = arrayOf(
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.6875, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.875, 1.0)
        )

        fun updateCropsMaxY(world: World, pos: BlockPos?, block: Block) {
            val blockState = world.getBlockState(pos)
            val ageValue = blockState.getValue(BlockCrops.AGE)
            val accessor: BlockAccessor = block as BlockAccessor
            accessor.setMaxY(240.0)
        }

//        fun updateWartMaxY(world: World, pos: BlockPos?, block: Block) {
//            (block as BlockAccessor).setMaxY(
//                if (PatcherConfig.futureHitBoxes && (minecraftUtils.isHypixel() || mc.isIntegratedServerRunning)) NETHER_WART_BOX[world.getBlockState(
//                    pos
//                ).getValue(BlockNetherWart.AGE)].maxY else .25f
//            )
//        } //#endif
    }
    fun getDiscordPing(message: String): String? {
        var newMsg: String
        return if(NekoQOL.nekoconfig.discordPing){
            newMsg = "<@${NekoQOL.nekoconfig.discordID}> " + message
            newMsg
        } else {
            message
        }
    }
    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load){
        event.world.getChunkFromBlockCoords(mc.thePlayer.position).blockStorageArray.forEach {
            for(i in 0.. 65536){
                val block: IBlockState = it.data[i] as IBlockState
                //CropUtilities.updateCropsMaxY(event.world, BlockPos(block.))
            }
        }
    }
    fun isInLimbo(): Boolean {
        if(mc.theWorld == null) return false
        return mc.theWorld.getBlockState(BlockPos(-21, 32, 21)).block == Blocks.wall_sign && mc.theWorld.getBlockState(BlockPos(-22, 31, 21)).block == Blocks.carpet && mc.theWorld.getBlockState(BlockPos(-25, 31, 21)).block == Blocks.spruce_stairs
    }
    fun isInLobby(): Boolean {
        if(mc.thePlayer.inventory.getStackInSlot(4) == null || mc.thePlayer.inventory.getStackInSlot(0) == null){
            return false
        }
        return mc.thePlayer.inventory.getStackInSlot(4).displayName.contains("Collectibles") && mc.thePlayer.inventory.getStackInSlot(0).displayName.contains("Game Menu")
    }
    fun isInHub(): Boolean {
        return sidebarLines.any { s -> HubServer.any { ScoreboardUtils.cleanSB(s).contains(it) } }
    }
    fun isPrivateIsland(): Boolean {
        return sidebarLines.any { s -> PrivateIsland.any { ScoreboardUtils.cleanSB(s).contains(it) } }
    }
    private val PrivateIsland = listOf("You")
    private val HubServer = listOf(
        "Village",
        "Forest",
        "Coal Mine",
        "Farm",
        "Graveyard",
        "Crypts"
    )
}
@Mixin(Block::class)
interface BlockAccessor {
    //#if MC==10809
    @Accessor
    fun setMaxY(maxY: Double) //#endif
}
