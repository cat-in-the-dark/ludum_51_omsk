package org.catinthedark.alyoep.game.entities.powerups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import org.catinthedark.alyoep.audio.Bgm
import org.catinthedark.alyoep.game.Assets
import org.catinthedark.alyoep.game.entities.*
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail

class NukePowerUp(override var pos: Vector2) : PowerUp(pos, Color.PINK) {
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }
    private val tower: Tower = IOC.atOrFail("tower")

    override fun apply(player: Player) {
        bgm.collectPowerup(Assets.Sounds.NUKE)

        player.nukeNova = NukeNova(tower.pos, player.height)
    }
}
