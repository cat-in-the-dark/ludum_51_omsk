package org.catinthedark.jvcrplotter.game.entities.powerups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.entities.Player

class FirePowerUp(override var pos: Vector2) : PowerUp(pos, Color.CORAL) {
    override fun apply(player: Player) {
        player.stats.bulletsCount += 1
    }
}
