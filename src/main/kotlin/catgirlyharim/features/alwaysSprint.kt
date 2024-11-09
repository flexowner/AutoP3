package catgirlyharim.features

import catgirlyharim.CatgirlYharim.Companion.mc
import catgirlyharim.config.MyConfig.alwaysSprint
import net.minecraft.client.settings.KeyBinding.setKeyBindState
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object AlwaysSprint{
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!alwaysSprint) return
        setKeyBindState(mc.gameSettings.keyBindSprint.keyCode, true)
    }

}