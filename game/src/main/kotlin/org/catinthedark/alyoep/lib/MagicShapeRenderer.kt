package org.catinthedark.alyoep.lib

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import kotlin.math.max


fun ShapeRenderer.managed(type: ShapeRenderer.ShapeType, block: (shapeRenderer: ShapeRenderer) -> Unit) {
    begin(type)
    block(this)
    end()
}

fun ShapeRenderer.polygon2(vararg vertices: Vector2) {
    this.polygon2(listOf(*vertices))
}

fun ShapeRenderer.polygon2(vertices: List<Vector2>) {
    val verticesCoordinates = vertices.flatMap {
        listOf(it.x, it.y)
    }.toFloatArray()
    polygon(verticesCoordinates)
}

fun ShapeRenderer.triangle2(p1: Vector2, p2: Vector2, p3: Vector2) {
    this.triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
}

fun ShapeRenderer.roundedRectLine(x: Float, y: Float, width: Float, height: Float, radius: Float) {
    // Four side lines, in clockwise order
    line(x + radius, y, x + width - radius, y)
    line(x + width, y + radius, x + width, y + height - radius)
    line(x + radius, y + height, x + width - radius, y + height)
    line(x, y + radius, x, y + height - radius)

    // https://github.com/libgdx/libgdx/blob/2d43b1074f770635212c9e8cae10da2b6a30b46c/gdx/src/com/badlogic/gdx/graphics/glutils/ShapeRenderer.java#L832
    val segments = max(1, (6 * Math.cbrt(radius.toDouble()) / 4).toInt())

    // Four arches, clockwise too
    curve(x, y + radius, x, y, x, y, x + radius, y, segments)
    curve(x + width - radius, y, x + width, y, x + width, y, x + width, y + radius, segments)
    curve(x + width, y + height - radius, x + width, y + height, x + width, y + height, x + width - radius, y + height, segments)
    curve(x, y + height - radius, x, y + height, x, y + height, x + radius, y + height, segments)
}

fun ShapeRenderer.roundedRectLineShadow(x: Float, y: Float, width: Float, height: Float, radius: Float) {
    // bottom line
    line(x + radius, y + height, x + width - radius, y + height)

    val segments = max(1, (6 * Math.cbrt(radius.toDouble()) / 4).toInt())

    // Two arches, clockwise too
    curve(x + width, y + height - radius, x + width, y + height, x + width, y + height, x + width - radius, y + height, segments)
    curve(x, y + height - radius, x, y + height, x, y + height, x + radius, y + height, segments)
}
