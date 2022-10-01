package org.catinthedark.jvcrplotter.lib

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

fun ShapeRenderer.managed(type: ShapeRenderer.ShapeType, block: (shapeRenderer: ShapeRenderer) -> Unit) {
    begin(type)
    block(this)
    end()
}
