package catgirlyharim.init.features

import catgirlyharim.init.CatgirlYharim.Companion.config
import catgirlyharim.init.CatgirlYharim.Companion.mc
import net.minecraft.client.settings.KeyBinding.setKeyBindState
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object AlwaysSprint{
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!config!!.alwaysSprint) return
        setKeyBindState(mc.gameSettings.keyBindSprint.keyCode, true)
    }

}