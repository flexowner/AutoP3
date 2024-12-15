package catgirlyharim.init.config

import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.features.petKeyBinds.equipPet
import catgirlyharim.init.utils.Hclip.hclip
import catgirlyharim.init.utils.Utils.modMessage
import catgirlyharim.init.utils.Utils.test
import catgirlyharim.init.utils.lavaClip.toggleLavaClip

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import kotlinx.serialization.cbor.Cbor.Default.decodeFromByteArray
import org.lwjgl.input.Keyboard
import java.io.File
import java.nio.file.Paths

class MyConfig : Config(Mod("CatgirlYharimAddons", ModType.SKYBLOCK), "config.json") {

    @Switch(
        name = "AutoP3",
        category = "AutoP3",
        subcategory = "Main"
    )
    var autoP3Active = false

    @Switch(
        name = "Select Route on Boss start!",
        category = "AutoP3",
        subcategory = "Route",
    )
    var onBossStart = false

    @Switch(
        name = "Select Route on P3 start!",
        category = "AutoP3",
        subcategory = "Route",
    )
    var onP3Start = false

    @Text(
        name = "Route",
        category = "AutoP3",
        subcategory = "Main",
        placeholder = "Select a route!",
        secure = false,
        multiline = false
    )
    var selectedRoute = ""

    @Text(
        name = "Boss start Route",
        category = "AutoP3",
        subcategory = "Route",
        placeholder = "Select a route!",
        secure = false,
        multiline = false
    )
    var BossStartRoute = ""

    @Text(
        name = "P3 start Route",
        category = "AutoP3",
        subcategory = "Route",
        placeholder = "Select a route!",
        secure = false,
        multiline = false
    )
    var P3StartRoute = ""

    @Switch(
        name = "Editmode",
        category = "AutoP3",
        subcategory = "Misc",
    )
    var editmode = true

    @Color(
        name = "Look Color",
        category = "Render",
        subcategory = "AutoP3"
    )
    var lookColor = OneColor(255, 0, 255)

    @Color(
        name = "Stop Color",
        category = "Render",
        subcategory = "AutoP3"
    )
    var stopColor = OneColor(255, 0, 0)

    @Color(
        name = "Jump Color",
        category = "Render",
        subcategory = "AutoP3"
    )
    var jumpColor = OneColor(100, 0, 100)

    @Color(
        name = "Walk Color",
        category = "Render",
        subcategory = "AutoP3"
    )
    var walkColor = OneColor(0, 255, 0)

    @Color(
        name = "Hclip Color",
        category = "Render",
        subcategory = "AutoP3"
    )
    var hclipColor = OneColor(0, 0, 0)

    @Color(
        name = "Vclip Color",
        category = "Render",
        subcategory = "AutoP3"
    )
    var vclipColor = OneColor(255, 255, 0)

    @Color(
        name = "Block Color",
        category = "Render",
        subcategory = "AutoP3"
    )
    var blockColor = OneColor(0, 0, 255)

    @Color(
        name = "Edge Color",
        category = "Render",
        subcategory = "AutoP3"
    )
    var edgeColor = OneColor(0, 255, 255)

    @Color(
        name = "Wait Color",
        category = "Render",
        subcategory = "AutoP3"
    )
    var waitColor = OneColor(255, 125, 0)

    @Color(
        name = "Boom Color",
        category = "Render",
        subcategory = "AutoP3"
    )
    var boomColor = OneColor(255, 0, 70)

    @Color(
        name = "Bonzo Color",
        category = "Render",
        subcategory = "AutoP3"
    )
    var bonzoColor = OneColor(255, 255, 255)

    @Color(
        name = "Term Color",
        category = "Render",
        subcategory = "AutoP3"
    )
    var termColor = OneColor(0, 200, 100)

    @Slider(
        name = "Render Distance",
        category = "Render",
        subcategory = "AutoP3",
        min = 0f,
        max = 100f,
        step = 1
    )
    var renderDistance = 50

    var fuckEpilepticPeople = false

    @Button(
        name = "Fuck Epileptic People",
        category = "Render",
        subcategory = "AutoP3",
        text = "Make walk chrome"
    )
    var runnable = Runnable {
        fuckEpilepticPeople = !fuckEpilepticPeople
    }

    @KeyBind(
        name = "Hclip",
        category = "Clip",
        subcategory = "Hclip"
    )
    var hclipKeyBind = OneKeyBind(Keyboard.KEY_NONE); // Initialize it with a default keybind

    @KeyBind(
        name = "Lava Clip",
        category = "Clip",
        subcategory = "Lavaclip"
    )
    var lavaClipKeyBind = OneKeyBind(Keyboard.KEY_NONE); // Initialize it with a default keybind

    @Slider(
        name = "Hclip Distance",
        category = "Clip",
        subcategory = "Hclip",
        min = 1f,
        max = 3F,
        step = 0
    )
    var hclipDistance = 2.8f;

    @Slider(
        name = "Distance",
        category = "Clip",
        subcategory = "Lavaclip",
        min = 1f,
        max = 50F,
        step = 1
    )
    var lavaClipDistanceKeyBind: Float = 30f;

    @Switch(
        name = "Storm Clip",
        category = "Clip",
        subcategory = "Stormclip"
    )
    var stormClipActive = false

    @Slider(
        name = "Storm Clip",
        category = "Clip",
        subcategory = "Stormclip",
        min = 10f,
        max = 60f,
        step = 1
    )
    var stormClipDistance = 40f

    @Switch(
        name = "Simulation",
        category = "Simulation"
    )
    var activeSimulation = false

    @Switch(
        name = "Lava",
        category = "Simulation"
    )
    var lavaSimulation = false

    @Switch(
        name = "Speed",
        category = "Simulation"
    )
    var speedSimulation = false

    //Pet Index

    @Slider(
        name = "Index #1",
        category = "Pets",
        subcategory = "Index",
        min = 10f,
        max = 44f,
        step = 1
    )
    var petIndexOne = 10

    @Slider(
        name = "Index #2",
        category = "Pets",
        subcategory = "Index",
        min = 10f,
        max = 44f,
        step = 1
    )
    var petIndexTwo = 10

    @Slider(
        name = "Index #3",
        category = "Pets",
        subcategory = "Index",
        min = 10f,
        max = 44f,
        step = 1
    )
    var petIndexThree = 10

    //Pet Keybinds

    @KeyBind(
        name = "Pet Keybind #1",
        category = "Pets",
        subcategory = "Index",
    )
    var petKeybindOne = OneKeyBind(Keyboard.KEY_NONE)

    @KeyBind(
        name = "Pet Keybind #1",
        category = "Pets",
        subcategory = "Index",
    )
    var petKeybindTwo = OneKeyBind(Keyboard.KEY_NONE)

    @KeyBind(
        name = "Pet Keybind #1",
        category = "Pets",
        subcategory = "Index",
    )
    var petKeybindThree = OneKeyBind(Keyboard.KEY_NONE)

    @Switch(
        name = "Wither ESP",
        category = "ESP",
        subcategory = "Wither"
    )
    var bossEsp = false

    @Switch(
        name = "Terminal ESP",
        category = "ESP",
        subcategory = "Terminal"
    )
    var terminalEsp = false

    @Slider(
        name = "ESP Opacity",
        category = "ESP",
        subcategory = "Customization",
        min = 0f,
        max = 1f,
        step = 0
    )
    var opacityEsp = 0.5f

    @Dropdown(
        name = "ESP Color",
        category = "ESP",
        subcategory = "Customization",
        options = arrayOf("White", "Gray", "Black", "Pink", "Red", "Yellow", "Green", "Cyan", "Blue")
    )
    var colorEsp = 7

    @Switch(
        name = "Powder Ghast ESP",
        category = "ESP",
        subcategory = ":3"
    )
    var powderGhastEsp = false

    init {
        initialize()
        registerKeyBind(hclipKeyBind) {
            hclip(mc.thePlayer.rotationYaw)
        }
        registerKeyBind(lavaClipKeyBind) {
            toggleLavaClip(lavaClipDistanceKeyBind)
        }
        registerKeyBind(petKeybindOne) {
            equipPet(petIndexOne)
        }
        registerKeyBind(petKeybindTwo) {
            equipPet(petIndexTwo)
        }
        registerKeyBind(petKeybindThree) {
            equipPet(petIndexThree)
        }
    }
}



