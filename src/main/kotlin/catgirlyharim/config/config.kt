package catgirlyharim.config

import catgirlyharim.CatgirlYharim.Companion.mc
import catgirlyharim.utils.Hclip.hclip
import catgirlyharim.utils.lavaClip.lavaClipDistance
import catgirlyharim.utils.lavaClip.toggleLavaClip

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import org.lwjgl.input.Keyboard

object MyConfig : Config(Mod("My Mod", ModType.SKYBLOCK), "config.json") {

    @KeyBind(name = "Hclip")
    var hclipKeyBind = OneKeyBind(Keyboard.KEY_G); // Initialize it with a default keybind

    @KeyBind(name = "Lava Clip")
    var lavaClipKeyBind = OneKeyBind(Keyboard.KEY_G); // Initialize it with a default keybind

    @Slider(
        name = "Distance",
        min = 1f,
        max = 3F,
        step = 0
    )
    var hclipDistance = 2.8f;

    @Slider(
        name = "Distance",
        min = 1f,
        max = 50F,
        step = 1
    )
    var lavaClipDistanceKeyBind: Float = 30f;

    @Switch(name = "Editmode")
    var editmode = true

    @Text(
        name = "Route",
        placeholder = "Select a route!",
        secure = false,
        multiline = false
    )
    var selectedRoute = ""

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



