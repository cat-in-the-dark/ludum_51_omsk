package org.catinthedark.jvcrplotter.game.entities.powerups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.Const.Balance.PowerUp.MAX_FIRE_SPEED
import org.catinthedark.jvcrplotter.game.entities.Player

class FirePowerUp(override var pos: Vector2) : PowerUp(pos, Color.CORAL) {
    override fun apply(player: Player) {
        // 1-1 2-1 2-2 3-2 3-3 4-3 4-4 5-4 6-4 7-4
        if (player.stats.bulletsCount % 2 == 0 &&
            player.stats.bulletsFireSpeed % 2 == 1 &&
            player.stats.bulletsFireSpeed < MAX_FIRE_SPEED
        ) {
            player.stats.bulletsFireSpeed += 1
        } else {
            player.stats.bulletsCount += 1
        }
    }
}
