package nekoqol.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import nekoqol.NekoQOL.Companion.display
import nekoqol.guis.BlockAnimationBlacklist
import nekoqol.guis.HideModID
import nekoqol.guis.ItemMacros
import java.awt.Color
import java.io.File
import java.util.function.Consumer

object NekoConfig : Vigilant(File("./config/NekoQOL/nekoconfig.toml"), "nekoqol", sortingBehavior = Sorting) {

    @Property(
        type = PropertyType.SWITCH,
        name = "Hilarity Master",
        category = "Hilarity",
        description = "&a&lENABLE&r or &c&lDISABLE&r all hilarity based messages/features",
    )
    var hilarityMaster = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Skyblock Hilarity",
        category = "Hilarity",
        description = "Gives you small heart attacks in ALL of &bSkyblock&f!",
    )
    var hilaritySkyblock = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Dwarven Hilarity",
        category = "Hilarity",
        description = "Does a little bit of tom foolery in the &bDwarven Mines",
    )
    var hilarityDwarvenMines = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Dungeon Hilarity",
        category = "Hilarity",
        description = "Does a little bit of tom foolery in the &cThe Catacombs &r& &cThe Master Catacombs",
    )
    var hilarityDungeons = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Foraging Hilarity",
        category = "Hilarity",
        description = "Does a little bit of tom foolery in the &aForaging Islands",
    )
    var hilarityForaging = true

    @Property(
        type = PropertyType.SELECTOR,
        name = "S Shaped Direction",
        category = "Macros",
        subcategory = "S Shaped",
        description = "What direction should the S Shaped Macro be looking in while on?\n&cWARNING: This setting is static at the moment and will not work!",
        options = ["North", "South", "East", "West"]
    )
    var sShapedMacroDirection = 0

    init {
        setCategoryDescription(
            "Hilarity",
            "Ever wanted to not be able to trust basic chat messages, get insulted when you fuck up, and more? Well, here's the perfect feature for you! Keep this enabled to get frequent heart attacks!\n\n&cNekoQOL is not liable for any computers that get smashed because of your rage!"
        )
    }

    fun init() {
        initialize()
    }

    private object Sorting : SortingBehavior() {
        override fun getCategoryComparator(): Comparator<in Category> = Comparator.comparingInt { o: Category ->
            configCategories.indexOf(o.name)
        }
    }

    private val configCategories = listOf(
        "Macros", "Hilarity"
    )
}
