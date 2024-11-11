package catgirlyharim.init.features

import catgirlyharim.init.CatgirlYharim.Companion.config
import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.utils.WorldRenderUtils.drawBoxByEntity
import catgirlyharim.init.utils.WorldRenderUtils.drawEntityBox
import catgirlyharim.init.utils.colorMap
import net.minecraft.entity.boss.EntityWither
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color.*

object BossEsp{
    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (config!!.bossEsp == true && mc.theWorld != null) {
            val withers = mc.theWorld.loadedEntityList.filterIsInstance<EntityWither>()
            for (wither in withers) {
                val color = colorMap[config!!.colorEsp]
                if (wither.isInvisible) return
                drawEntityBox(wither, color!!, true, true, 0f)
            }
        }
    }
}

