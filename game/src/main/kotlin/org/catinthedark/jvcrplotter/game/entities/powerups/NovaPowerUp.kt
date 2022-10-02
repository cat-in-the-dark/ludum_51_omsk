package org.catinthedark.jvcrplotter.game.entities.powerups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.game.entities.NovaStats
import org.catinthedark.jvcrplotter.game.entities.Player
import kotlin.math.min

class NovaPowerUp(override var pos: Vector2) : PowerUp(pos, Color.BLUE) {
    override fun apply(player: Player) {
        val nova = player.stats.nova
        if (nova != null) {
            if (nova.novaFreq < Const.Balance.PowerUp.MAX_NOVA_FREQ) {
                nova.novaFreq += 1
                nova.novaDmg += 2f
            } else {
                nova.novaFreq = Const.Balance.PowerUp.MAX_NOVA_FREQ

                nova.novaDmg += 2f
                nova.novaSpeed = min(nova.novaSpeed + 32, 600f)
            }
        } else {
            player.stats.nova = NovaStats()
        }
    }
}
