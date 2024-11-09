package catgirlyharim.init.config

import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.utils.Hclip.hclip
import catgirlyharim.init.utils.lavaClip.toggleLavaClip

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import org.lwjgl.input.Keyboard

class MyConfig : Config(Mod("My Mod", ModType.SKYBLOCK), "config.json") {

    @KeyBind(
        name = "Hclip",
        category = "Clip",
        subcategory = "Hclip"
    )
    var hclipKeyBind = OneKeyBind(Keyboard.KEY_G); // Initialize it with a default keybind

    @KeyBind(
        name = "Lava Clip",
        category = "Clip",
        subcategory = "Lavaclip"
    )
    var lavaClipKeyBind = OneKeyBind(Keyboard.KEY_G); // Initialize it with a default keybind

    @Slider(
        name = "Hclip distance",
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
        name = "AutoP3",
        category = "AutoP3",
    )
    var autoP3Active = false

    @Text(
        name = "Route",
        category = "AutoP3",
        subcategory = "Route",
        placeholder = "Select a route!",
        secure = false,
        multiline = false
    )
    var selectedRoute = ""

    @Switch(
        name = "Editmode",
        category = "AutoP3",
        subcategory = "Misc",
    )
    var editmode = true

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

    @Switch(
        name = "Always sprint",
        category = "Simulation"
    )
    public var alwaysSprint = false
    init {
        initialize()
        registerKeyBind(hclipKeyBind) {
            hclip(mc.thePlayer.rotationYaw)
        }
        registerKeyBind(lavaClipKeyBind) {
            toggleLavaClip(lavaClipDistanceKeyBind)
        }
    }
}



