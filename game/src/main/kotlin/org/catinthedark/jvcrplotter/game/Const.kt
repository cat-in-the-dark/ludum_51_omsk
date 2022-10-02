package org.catinthedark.jvcrplotter.game

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

object Const {
    object Screen {
        const val WIDTH = 1280
        const val HEIGHT = 720
        const val ZOOM = 1f
    }

    object Balance {
        val PLAYER_SPAWN_POINT = Vector2(Screen.WIDTH / 2f, Screen.HEIGHT / 2f)
        const val ENEMY_SPAWN_TIMEOUT = 2f
        const val MAX_PLAYER_SPEED = 300f
        const val MAX_PLAYERS = 4
        const val MAX_FOLLOW_DIST = 600
        const val MAX_SHOOT_DIST = 500

        const val SPAWNS_H_WIDTH = Screen.WIDTH + 16f
        const val SPAWNS_H_HEIGHT = 64f
        const val SPAWNS_V_WIDTH = 64f
        const val SPAWNS_V_HEIGHT = Screen.HEIGHT + 16f

        val generatorPlaces = listOf(
            Rectangle((Screen.WIDTH - SPAWNS_H_WIDTH) / 2f, -SPAWNS_H_HEIGHT - 1f, SPAWNS_H_WIDTH, SPAWNS_H_HEIGHT),
            Rectangle((Screen.WIDTH - SPAWNS_H_WIDTH) / 2f, Screen.HEIGHT + 1f, SPAWNS_H_WIDTH, SPAWNS_H_HEIGHT),
            Rectangle(-SPAWNS_V_WIDTH - 1f, (Screen.HEIGHT - SPAWNS_V_HEIGHT) / 2f, SPAWNS_V_WIDTH, SPAWNS_V_HEIGHT),
            Rectangle(Screen.WIDTH + 1f, (Screen.HEIGHT - SPAWNS_V_HEIGHT) / 2f, SPAWNS_V_WIDTH, SPAWNS_V_HEIGHT),
        )

        object Spawn {
            const val TIMEOUT = 1f
            const val BOSS_COOLDOWN = 2f
            const val SIN_TIME_SCALE = 2f
            const val MAX_SPAWN = 2f
        }

        object PowerUp {
            const val TIMEOUT = 10f
            const val MAX_FIRE_SPEED = 4

            const val MAX_NOVA_FREQ = 4
            const val MIN_NOVA_FREQ = 2
        }

        object Tower {
            const val RADIUS = 60f
            const val MAX_HP = 600f
            const val VISUAL_HEIGHT = 180f
        }
    }

    object UI {

    }
}
