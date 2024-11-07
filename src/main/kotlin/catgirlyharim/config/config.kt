package catgirlyharim.config

import catgirlyharim.CatgirlYharim.Companion.mc
import catgirlyharim.utils.Hclip.hclip

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import org.lwjgl.input.Keyboard

object MyConfig : Config(Mod("My Mod", ModType.SKYBLOCK), "config.json") {

    @KeyBind(name = "Keybinding")
    var hclipKeyBind = OneKeyBind(Keyboard.KEY_G); // Initialize it with a default keybind

    @Slider(
        name = "Distance",
        min = 1f,
        max = 3F,
        step = 0
    )
    var hclipDistance = 2.8f;

    init {
        initialize()
        registerKeyBind(hclipKeyBind) {
            hclip(mc.thePlayer.rotationYaw)
        }
    }
}



