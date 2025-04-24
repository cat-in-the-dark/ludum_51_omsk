package org.catinthedark.alyoep.game.entities.powerups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import org.catinthedark.alyoep.audio.Bgm
import org.catinthedark.alyoep.game.Assets
import org.catinthedark.alyoep.game.Const
import org.catinthedark.alyoep.game.entities.NovaStats
import org.catinthedark.alyoep.game.entities.Player
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import kotlin.math.min

class NovaPowerUp(override var pos: Vector2) : PowerUp(pos, Color.BLUE) {
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }

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

        bgm.collectPowerup()
    }
}
