package catgirlyharim.utils

import catgirlyharim.CatgirlYharim.Companion.mc
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.round

object WorldRenderUtils {

    private val tessellator: Tessellator = Tessellator.getInstance()
    private val worldRenderer: WorldRenderer = tessellator.worldRenderer
    private val renderManager = mc.renderManager

    /**
     * Draws a line connecting the points [start] and [finish].
     *
     * @param phase Determines whether the box should be visible through walls (disables the depth test).
     */
    fun drawLine(start: Vec3, finish: Vec3, color: Color, thickness: Float = 3f, phase: Boolean = true) {
        drawLine(start.xCoord, start.yCoord, start.zCoord, finish.xCoord, finish.yCoord, finish.zCoord, color, thickness, phase)
    }

    /**
     * Draws a line connecting the points ([x], [y], [z]) and ([x2], [y2], [z2]).
     *
     * @param phase Determines whether the box should be visible through walls (disables the depth test).
     */
    fun drawLine (x: Double, y: Double, z: Double, x2: Double, y2: Double, z2:Double, color: Color, thickness: Float = 3f, phase: Boolean = true) {
        GlStateManager.disableLighting()
        GL11.glBlendFunc(770, 771)
        GlStateManager.enableBlend()
        GL11.glLineWidth(thickness)
        if (phase) GlStateManager.disableDepth()
        GlStateManager.disableTexture2D()
        GlStateManager.pushMatrix()

        GlStateManager.translate(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ)
        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        GlStateManager.color(color.red.toFloat() / 255f, color.green.toFloat() / 255f,
            color.blue.toFloat() / 255f, 1f)

        worldRenderer.pos(x, y, z).endVertex()
        worldRenderer.pos(x2, y2, z2).endVertex()

        tessellator.draw()

        GlStateManager.popMatrix()
        GlStateManager.enableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.disableBlend()
    }

    /**
     * Draws a cube outline for the block at the given [blockPos].
     *
     * This outline will be visible through walls. The depth test is disabled.
     *
     * @param relocate Translates the coordinates to account for the camera position. See [WorldRenderUtils] for more information.
     */
    fun drawBoxAtBlock (blockPos: BlockPos, color: Color, thickness: Float = 3f, relocate: Boolean = true) {
        drawBoxAtBlock(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), color, thickness, relocate)
    }

    /**
     * Draws a cube outline of size 1 starting at [x], [y], [z] which extends by 1 along the axes in positive direction.
     *
     * This outline will be visible through walls. The depth test is disabled.
     *
     * @param relocate Translates the coordinates to account for the camera position. See [WorldRenderUtils] for more information.
     */
    fun drawBoxAtBlock (x: Double, y: Double, z: Double, color: Color, thickness: Float = 3f, relocate: Boolean = true) {
        drawCustomSizedBoxAt(x, y, z, 1.0, 1.0, 1.0, color, thickness, true, relocate)
    }

    /**
     * Draws a rectangular cuboid outline (box) around the [entity].
     *
     * This box is centered horizontally around the entity with the given [width].
     * Vertically the box is aligned with the bottom of the entities hit-box and extends upwards by [height].
     * The box can be offset from this default alignment through the use of [xOffset], [yOffset], [zOffset].
     *
     * @param phase Determines whether the box should be visible through walls (disables the depth test).
     * @param partialTicks Used for predicting the [entity]'s position so that the box smoothly moves with the entity.
     */
    fun drawBoxByEntity (entity: Entity, color: Color, width: Double, height: Double, partialTicks: Float = 0f,
                         lineWidth: Double = 2.0, phase: Boolean = false,
                         xOffset: Double = 0.0, yOffset: Double = 0.0, zOffset: Double = 0.0
    ) {
        drawBoxByEntity(entity, color, width.toFloat(), height.toFloat(), partialTicks, lineWidth.toFloat(),phase,xOffset, yOffset, zOffset)
    }

    /**
     * Draws a rectangular cuboid outline (box) around the [entity].
     *
     * This box is centered horizontally around the entity with the given [width].
     * Vertically the box is aligned with the bottom of the entities hit-box and extends upwards by [height].
     * The box can be offset from this default alignment through the use of [xOffset], [yOffset], [zOffset].
     *
     * @param phase Determines whether the box should be visible through walls (disables the depth test).
     * @param partialTicks Used for predicting the [entity]'s position so that the box smoothly moves with the entity.
     */
    fun drawBoxByEntity (entity: Entity, color: Color, width: Float, height: Float, partialTicks: Float = 0f,
                         lineWidth: Float = 2f, phase: Boolean = false,
                         xOffset: Double = 0.0, yOffset: Double = 0.0, zOffset: Double = 0.0
    ){
        val x = entity.posX + ((entity.posX-entity.lastTickPosX)*partialTicks) + xOffset - width / 2.0
        val y = entity.posY + ((entity.posY-entity.lastTickPosY)*partialTicks) + yOffset
        val z = entity.posZ + ((entity.posZ-entity.lastTickPosZ)*partialTicks) + zOffset - width / 2.0

        drawCustomSizedBoxAt(x, y, z, width.toDouble(), height.toDouble(), width.toDouble(), color, lineWidth, phase)
    }

    fun drawCustomSizedBoxAt(x: Double, y: Double, z: Double, xWidth: Double, yHeight: Double, zWidth: Double, color: Color, thickness: Float = 3f, phase: Boolean = true, relocate: Boolean = true) {
        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glLineWidth(thickness)
        if (phase) GlStateManager.disableDepth()
        GlStateManager.disableTexture2D()

        GlStateManager.pushMatrix()

        if (relocate) GlStateManager.translate(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ)
        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        GlStateManager.color(color.red.toFloat() / 255f, color.green.toFloat() / 255f,
            color.blue.toFloat() / 255f, 1f)


        worldRenderer.pos(x+xWidth,y+yHeight,z+zWidth).endVertex()
        worldRenderer.pos(x+xWidth,y+yHeight,z).endVertex()
        worldRenderer.pos(x,y+yHeight,z).endVertex()
        worldRenderer.pos(x,y+yHeight,z+zWidth).endVertex()
        worldRenderer.pos(x+xWidth,y+yHeight,z+zWidth).endVertex()
        worldRenderer.pos(x+xWidth,y,z+zWidth).endVertex()
        worldRenderer.pos(x+xWidth,y,z).endVertex()
        worldRenderer.pos(x,y,z).endVertex()
        worldRenderer.pos(x,y,z+zWidth).endVertex()
        worldRenderer.pos(x,y,z).endVertex()
        worldRenderer.pos(x,y+yHeight,z).endVertex()
        worldRenderer.pos(x,y,z).endVertex()
        worldRenderer.pos(x+xWidth,y,z).endVertex()
        worldRenderer.pos(x+xWidth,y+yHeight,z).endVertex()
        worldRenderer.pos(x+xWidth,y,z).endVertex()
        worldRenderer.pos(x+xWidth,y,z+zWidth).endVertex()
        worldRenderer.pos(x,y,z+zWidth).endVertex()
        worldRenderer.pos(x,y+yHeight,z+zWidth).endVertex()
        worldRenderer.pos(x+xWidth,y+yHeight,z+zWidth).endVertex()

        tessellator.draw()

        GlStateManager.popMatrix()
        GlStateManager.enableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.disableBlend()
    }
    fun drawP3box(x: Double, y: Double, z: Double, xWidth: Double, yHeight: Double, zWidth: Double, color: Color, thickness: Float = 3f, phase: Boolean = true, relocate: Boolean = true) {

        val yMiddle = (y + yHeight / 2)
        drawSquare(x, y + yHeight, z, xWidth, zWidth, color, thickness, phase, relocate)
        drawSquare(x, yMiddle, z, xWidth, zWidth, color, thickness, phase, relocate)
        drawSquare(x, y + 0.02, z, xWidth, zWidth, color, thickness, phase, relocate)
    }
    private fun drawSquare(x: Double, y: Double, z: Double, xWidth: Double, zWidth: Double, color: Color, thickness: Float = 3f, phase: Boolean = true, relocate: Boolean = true) {
        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glLineWidth(thickness)
        if (phase) GlStateManager.disableDepth()
        GlStateManager.disableTexture2D()

        GlStateManager.pushMatrix()

        if (relocate) GlStateManager.translate(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ)
        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        GlStateManager.color(color.red.toFloat() / 255f, color.green.toFloat() / 255f,
            color.blue.toFloat() / 255f, 1f)

        worldRenderer.pos(x + xWidth, y, z + zWidth).endVertex()
        worldRenderer.pos(x + xWidth, y, z).endVertex()
        worldRenderer.pos(x, y, z).endVertex()
        worldRenderer.pos(x, y, z + zWidth).endVertex()
        worldRenderer.pos(x + xWidth, y, z + zWidth).endVertex()

        tessellator.draw()

        GlStateManager.popMatrix()
        GlStateManager.enableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.disableBlend()
    }
    fun renderText(
        text: String,
        x: Int,
        y: Int,
        scale: Double = 1.0,
        color: Int = 0xFFFFFF,
    ) {
        GlStateManager.pushMatrix()
        GlStateManager.disableLighting()
        GlStateManager.disableDepth()
        GlStateManager.disableBlend()
        GlStateManager.scale(scale, scale, scale)
        var yOffset = y - mc.fontRendererObj.FONT_HEIGHT
        text.split("\n").forEach {
            yOffset += (mc.fontRendererObj.FONT_HEIGHT * scale).toInt()
            mc.fontRendererObj.drawString(
                it,
                round(x / scale).toFloat(),
                round(yOffset / scale).toFloat(),
                color,
                true
            )
        }
        GlStateManager.popMatrix()
    }

    fun drawSquareTwo(
        x: Double,
        y: Double,
        z: Double,
        xWidth: Double,
        zWidth: Double,
        color: Color,
        thickness: Float = 3f,
        phase: Boolean = true,
        relocate: Boolean = true
    ) {
        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glLineWidth(thickness)

        if (phase) GlStateManager.disableDepth()
        GlStateManager.disableTexture2D()

        GlStateManager.pushMatrix()

        // Translate to viewer's position if relocate is true
        if (relocate) {
            GlStateManager.translate(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ)
        }

        // Begin drawing the lines
        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)

        // Set the color
        GlStateManager.color(color.red.toFloat() / 255f, color.green.toFloat() / 255f,
            color.blue.toFloat() / 255f, 1f)

        // Calculate offsets so the center of the square is at (x, y, z)
        val halfXWidth = xWidth / 2
        val halfZWidth = zWidth / 2

        // Define the vertices based on the center (x, y, z)
        worldRenderer.pos(x + halfXWidth, y, z + halfZWidth).endVertex()
        worldRenderer.pos(x + halfXWidth, y, z - halfZWidth).endVertex()
        worldRenderer.pos(x - halfXWidth, y, z - halfZWidth).endVertex()
        worldRenderer.pos(x - halfXWidth, y, z + halfZWidth).endVertex()
        worldRenderer.pos(x + halfXWidth, y, z + halfZWidth).endVertex()

        tessellator.draw()

        GlStateManager.popMatrix()

        // Restore the OpenGL states
        GlStateManager.enableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.disableBlend()
    }

}