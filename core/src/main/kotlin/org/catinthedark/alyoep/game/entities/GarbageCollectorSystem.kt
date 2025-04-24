package org.catinthedark.alyoep.game.entities

import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import org.catinthedark.alyoep.lib.interfaces.IDestructible
import org.catinthedark.alyoep.lib.interfaces.IUpdatable

class GarbageCollectorSystem : IUpdatable {
    override fun update() {
        val enemies: MutableList<IDestructible> = IOC.atOrFail("enemies")
        val bullets: MutableList<IDestructible> = IOC.atOrFail("bullets")
        val players: MutableList<IDestructible> = IOC.atOrFail("players")

        enemies.removeIf { it.shouldDestroy }
        bullets.removeIf { it.shouldDestroy }
        players.removeIf { it.shouldDestroy }
    }
}
