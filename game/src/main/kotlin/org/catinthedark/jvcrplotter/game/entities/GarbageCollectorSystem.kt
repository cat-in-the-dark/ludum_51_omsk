package org.catinthedark.jvcrplotter.game.entities

import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.interfaces.IDestructible
import org.catinthedark.jvcrplotter.lib.interfaces.IUpdatable

class GarbageCollectorSystem : IUpdatable {
    override fun update() {
        val enemies: MutableList<IDestructible> = IOC.atOrFail("enemies")
        val bullets: MutableList<IDestructible> = IOC.atOrFail("bullets")

        enemies.removeIf { it.shouldDestroy }
        bullets.removeIf { it.shouldDestroy }
    }
}
