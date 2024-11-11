package catgirlyharim.init.features

import catgirlyharim.init.CatgirlYharim.Companion.config
import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.utils.WorldRenderUtils.drawEntityBox
import catgirlyharim.init.utils.colorMap
import net.minecraft.entity.item.EntityArmorStand
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object TerminalEsp{
    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (!config!!.terminalEsp) return
        val armorStands = mc.theWorld.loadedEntityList.filterIsInstance<EntityArmorStand>()
        for (armorstand in armorStands) {
            if (arrayOf("Inactive Terminal", "Inactive Device", "Not Activated").contains(armorstand.name)) {
                val color = colorMap[config!!.colorEsp]
                drawEntityBox(armorstand, color!!, true, true, 0f)
            }
        }
    }
}