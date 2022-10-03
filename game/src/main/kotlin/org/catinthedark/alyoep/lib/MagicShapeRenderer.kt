package org.catinthedark.alyoep.lib

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

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