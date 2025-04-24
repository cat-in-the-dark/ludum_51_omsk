package org.catinthedark.alyoep.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Vector2
import org.catinthedark.alyoep.game.entities.powerups.PowerUp
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import kotlin.math.min
import kotlin.math.pow

fun intersectCircles(center1: Vector2, radius1: Float, center2: Vector2, radius2: Float): Boolean {
    val dist = center1.dst(center2)
    val sumR = radius1 + radius2
    return dist <= sumR
}

fun sign(p1: Vector2, p2: Vector2, p3: Vector2): Float {
    return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y)
}

fun pointInTriangle(pt: Vector2, v1: Vector2, v2: Vector2, v3: Vector2): Boolean {
    val d1 = sign(pt, v1, v2)
    val d2 = sign(pt, v2, v3)
    val d3 = sign(pt, v3, v1)
    val hasNeg = d1 < 0 || d2 < 0 || d3 < 0
    val hasPos = d1 > 0 || d2 > 0 || d3 > 0
    return !(hasNeg && hasPos)
}

fun intersectTriangleCircle(p1: Vector2, p2: Vector2, p3: Vector2, center: Vector2, radius: Float): Boolean {
    // 1. if any of triangle vertices is inside the circle => intersection
    val radius2 = radius * radius
    if (p1.dst2(center) < radius2 || p2.dst2(center) < radius2 || p3.dst2(center) < radius2) {
        return true
    }

    // 2. if circle center is inside the triangle => intersection
    if (pointInTriangle(center, p1, p2, p3)) {
        return true
    }

    // 3. If any of line intersects with the circle => intersection
    if (Intersector.intersectSegmentCircle(p1, p2, center, radius2)
        || Intersector.intersectSegmentCircle(p2, p3, center, radius2)
        || Intersector.intersectSegmentCircle(p3, p1, center, radius2)
    ) {
        return true
    }

    return false
}

class CollisionsSystem {
    fun update(playersIndex: HashMap<Player, Int>) {
        val enemies: MutableList<SimpleEnemy> = IOC.atOrFail("enemies")
        val players: MutableList<Player> = IOC.atOrFail("players")
        val bullets: MutableList<Bullet> = IOC.atOrFail("bullets")
        val powerUps: MutableList<PowerUp> = IOC.atOrFail("powerUps")
        val tower: Tower = IOC.atOrFail("tower")

        players.forEach { player ->
            // TODO: collide them
            val collidedPowerUps = powerUps.filter {
                it.getCollisionRect().overlaps(player.getCollisionRect())
            }.toList()
            collidedPowerUps.forEach {
                it.apply(player)
            }
            powerUps.removeAll(collidedPowerUps)

            players.forEach { other ->
                player.healNova?.apply {
                    if (other.healAnimationTime <= 0.01f && intersectTriangleCircle(p1, p2, p3, other.center, other.width)) {
                        other.playHealAnimation = true
                        other.currentHP = min(other.stats.maxHP, other.currentHP + other.stats.maxHP / 2f)
                    }
                }
            }
        }

        enemies.forEach { enemy ->
            players.forEach { player ->
                val playerIdx = playersIndex.getValue(player)
                if (intersectCircles(
                        enemy.pos,
                        enemy.radius,
                        player.center,
                        player.exradius
                    )
                ) {
                    onHitPlayerEnemy(player, playerIdx, enemy)
                }

                player.nova?.apply {
                    if (intersectTriangleCircle(p1, p2, p3, enemy.center, enemy.radius)) {
                        enemy.damage(stats.novaDmg * Gdx.graphics.deltaTime)
                    }
                }

                player.nukeNova?.apply {
                    if (intersectTriangleCircle(p1, p2, p3, enemy.center, enemy.radius)) {
                        enemy.damage(500f * Gdx.graphics.deltaTime)
                    }
                }
            }

            bullets.forEach { bullet ->
                // TODO: collide them
                val res = Intersector.intersectSegmentCircle(
                    bullet.pos,
                    bullet.posEnd,
                    enemy.pos,
                    enemy.radius.pow(2)
                )
                if (res) {
                    enemy.damage(bullet)
                    bullet.damage(enemy)
                }
            }

            if (
                tower.currentHP >= 0.0f &&
                intersectTriangleCircle(tower.p1, tower.p2, tower.p3, enemy.center, enemy.radius)
            ) {
                enemy.tryHitTower {
                    tower.hit(enemy.damage)
                    Gdx.app.log(this::class.simpleName, "tow damage: ${enemy.damage} -> ${tower.currentHP}")
                }
            }
        }
    }

    private fun onHitPlayerEnemy(player: Player, playerIdx: Int, enemy: SimpleEnemy) {
        if (player.currentHP <= 0) {
            return
        }
        enemy.tryHitPlayer(playerIdx) {
            player.hit(enemy.damage)
            Gdx.app.log(this::class.simpleName, "pl damage: ${enemy.damage} -> ${player.currentHP}")
        }
    }
}
