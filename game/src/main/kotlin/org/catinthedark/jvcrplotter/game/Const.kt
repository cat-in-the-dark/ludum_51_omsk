package org.catinthedark.jvcrplotter.game

import com.badlogic.gdx.math.Rectangle

object Const {
    object Screen {
        const val WIDTH = 1280
        const val HEIGHT = 720
        const val ZOOM = 1f
    }

    object Balance {
        const val ENEMY_SPAWN_TIMEOUT = 2f
        const val MAX_PLAYER_SPEED = 300f
        const val MAX_PLAYERS = 4

        val generatorPlaces = listOf(
            Rectangle(5f, 5f, 1270f, 64f),
            Rectangle(5f, 715f - 64f, 1270f, 64f),
            Rectangle(5f, 72f, 64f, 700f),
            Rectangle(1211f, 72f, 64f, 700f),
        )

        object Spawn {
            const val TIMEOUT = 1f
            const val SIN_TIME_SCALE = 2f
            const val MAX_SPAWN = 6f
        }
    }

    object UI {

    }
}
