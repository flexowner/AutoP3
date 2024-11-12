package catgirlyharim.init.features

import catgirlyharim.init.CatgirlYharim.Companion.config
import catgirlyharim.init.CatgirlYharim.Companion.mc
import catgirlyharim.init.utils.WorldRenderUtils.drawEntityBox
import catgirlyharim.init.utils.WorldRenderUtils.renderText
import catgirlyharim.init.utils.colorMap
import catgirlyharim.init.utils.lavaClip.lavaClipDistance
import catgirlyharim.init.utils.lavaClip.lavaclipping
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.monster.EntityGhast
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object PowderGhastEsp{
    var ghastExists = false
    var text = ""
    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (config!!.powderGhastEsp == true && mc.theWorld != null) {
            var ghasts = mc.theWorld.loadedEntityList.filterIsInstance<EntityGhast>()
            for (ghast in ghasts) {
                val color = colorMap[config!!.colorEsp]
                if (ghast.isInvisible || !ghast.name.contains("Powder")) {
                    ghastExists = false
                    return
                }
                drawEntityBox(ghast, color!!, true, true, 0f)
                ghastExists = true
                text = ghast.name
            }
        }
    }
    @SubscribeEvent
    fun onOverlay(event: RenderGameOverlayEvent.Post) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR || !ghastExists || mc.ingameGUI == null) return
        val sr = ScaledResolution(mc)
        val width = sr.scaledWidth / 2 - mc.fontRendererObj.getStringWidth(text) / 2
        renderText(
            text = text,
            x = width,
            y = sr.scaledHeight / 2 + 10
        )
    }
}