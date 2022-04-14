package nekoqol.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior

import java.io.File
import java.util.function.Consumer

object NekoConfig : Vigilant(File("./config/NekoQOL/nekoconfig.toml"), "NekoQOL Config", sortingBehavior = Sorting) {

    // FARM MACROING
    @Property(
        type = PropertyType.SELECTOR,
        name = "S Shaped Direction",
        description = "Forces your client to face a direction",
        category = "Macros",
        subcategory = "S Shaped",
        options = ["North", "South", "East", "West"]
    )
    var sShapedYaw = 0

    @Property(
        type = PropertyType.DECIMAL_SLIDER,
        name = "S Shaped Direction Pitch",
        description = "Forces your PITCH to the number below whilst active",
        category = "Macros",
        subcategory = "S Shaped",
        maxF = 90F
    )
    var sShapedPitch = 0F

    @Property(
        type = PropertyType.SWITCH,
        name = "Set Homes",
        description = "Sets your home on each level change to be more efficient when failsafes activate",
        category = "Macros",
        maxF = 90F
    )
    var sSetHome = true

    // STRAMDD

    // AUTO SELL

    @Property(
        type = PropertyType.SWITCH,
        name = "Auto Sell Toggle",
        category = "Macros",
        subcategory = "Auto Sell",
        description = "Should the Auto Sell macro activate?",
    )
    var autoSellToggle = false

    @Property(
        type = PropertyType.SELECTOR,
        name = "Auto Sell Selector",
        category = "Macros",
        subcategory = "Auto Sell",
        description = "What macro do you want active clearing up your inventory",
        options = ["Bazaar", "Trade Menu"]
    )
    var autoSellChoice = 0

    //
    // HILARITY
    //

    @Property(
        type = PropertyType.SWITCH,
        name = "Allow Discord Webhook",
        category = "Webhook",
        description = "Should you get notified in discord when failsafes activate?",
    )
    var discordPost = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Allow Discord Ping",
        category = "Webhook",
        description = "Should you get pinged in discord when failsafes activate?",
    )
    var discordPing = false

    @Property(
        type = PropertyType.TEXT,
        name = "Discord ID",
        description = "Paste your discord ID to get pinged when failsafes activate.",
        category = "Webhook",
    )
    var discordID = ""

    @Property(
        type = PropertyType.TEXT,
        name = "Discord Webhook URL",
        description = "Paste your Discord Webhook URL here",
        category = "Webhook",
    )
    var discordURL = ""

    //
    // HILARITY
    //

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
        type = PropertyType.SWITCH,
        name = "Nyaa Sound GUI",
        category = "Hilarity",
        description = "Play the &bNyaa&f sound every time you open up a &cContainer&f or &cInventory",
    )
    var nyaaContainer = false

    init {
        setCategoryDescription(
            "Hilarity",
            "Ever wanted to not be able to trust basic chat messages, get insulted when you fuck up, and more? Well, here's the perfect feature for you! Keep this enabled to get frequent heart attacks!\n\n&cNekoQOL is not liable for any computers that get smashed because of your rage!"
        )
        setCategoryDescription(
            "Macros",
            "It's called &bQuality of Life&f... not cheats! Get it right...\n&7We don't cheat... wtf!"
        )
        setSubcategoryDescription(
            "Macros",
            "Auto Sell",
            "Automatically sell your sellables once your inventory is full!"
        )
        listOf(
            "autoSellChoice"
        ).forEach(Consumer { s: String ->
            addDependency(s, "autoSellToggle")
        })
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
        "Macros", "Webhook", "Hilarity"
    )
}
