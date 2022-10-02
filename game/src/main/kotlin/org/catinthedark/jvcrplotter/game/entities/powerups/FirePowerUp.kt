package org.catinthedark.jvcrplotter.game.entities.powerups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.audio.Bgm
import org.catinthedark.jvcrplotter.game.Const.Balance.PowerUp.MAX_FIRE_SPEED
import org.catinthedark.jvcrplotter.game.entities.Player
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail

class FirePowerUp(override var pos: Vector2) : PowerUp(pos, Color.CORAL) {
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }

    override fun apply(player: Player) {
        // 1-1 2-1 2-2 3-2 3-3 4-3 4-4 5-4 6-4 7-4
        if ((player.stats.bulletsCount + player.stats.bulletsFireSpeed) % 2 == 1 &&
            player.stats.bulletsFireSpeed < MAX_FIRE_SPEED
        ) {
            player.stats.bulletsFireSpeed += 1
        } else {
            player.stats.bulletsCount += 1
        }

        bgm.collectPowerup(false)
    }
}
