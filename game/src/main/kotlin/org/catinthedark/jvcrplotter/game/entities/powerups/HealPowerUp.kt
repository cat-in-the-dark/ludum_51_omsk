package org.catinthedark.jvcrplotter.game.entities.powerups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.audio.Bgm
import org.catinthedark.jvcrplotter.game.entities.HealNova
import org.catinthedark.jvcrplotter.game.entities.Player
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import kotlin.math.min

class HealPowerUp(override var pos: Vector2) : PowerUp(pos, Color.LIME) {
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }
    private val players: List<Player> = IOC.atOrFail("players")

    override fun apply(player: Player) {
        players.forEach {
            it.currentHP = min(it.stats.maxHP, it.currentHP + it.stats.maxHP / 2f)
        }

        bgm.collectPowerup(true)

        player.healNova = HealNova(player.center.cpy(), player.height)
    }
}
