package org.catinthedark.alyoep.game.entities.powerups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import org.catinthedark.alyoep.audio.Bgm
import org.catinthedark.alyoep.game.Const
import org.catinthedark.alyoep.game.entities.HealNova
import org.catinthedark.alyoep.game.entities.Player
import org.catinthedark.alyoep.game.entities.Tower
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import kotlin.math.min

class HealPowerUp(override var pos: Vector2) : PowerUp(pos, Color.LIME) {
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }
    private val tower: Tower = IOC.atOrFail("tower")

    override fun apply(player: Player) {
        tower.currentHP = min(Const.Balance.Tower.MAX_HP, tower.currentHP + Const.Balance.Tower.MAX_HP / 2f)

        bgm.collectPowerup(true)

        player.healNova = HealNova(tower.pos, player.height)
    }
}
