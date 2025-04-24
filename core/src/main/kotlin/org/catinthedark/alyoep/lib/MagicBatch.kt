package org.catinthedark.alyoep.lib

import com.badlogic.gdx.graphics.g2d.Batch

fun Batch.managed(block: (batch: Batch) -> Unit) {
    begin()
    block(this)
    end()
}
