package org.catinthedark.alyoep.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import org.catinthedark.alyoep.audio.Bgm
import org.catinthedark.alyoep.game.Const
import org.catinthedark.alyoep.game.Const.Balance.PLAYER_SPAWN_POINT
import org.catinthedark.alyoep.game.States
import org.catinthedark.alyoep.game.control.PlayerController
import org.catinthedark.alyoep.game.entities.*
import org.catinthedark.alyoep.lib.*
import org.catinthedark.alyoep.lib.math.randomDir
import org.catinthedark.alyoep.lib.states.IState
import org.slf4j.LoggerFactory

class PlayerState : IState {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val colors = listOf(Color.CORAL, Color.CHARTREUSE, Color.GOLDENROD, Color.ROYAL)
    private val controllers: MutableMap<PlayerController, Boolean> by lazy { IOC.atOrFail("input") }
    private lateinit var enemiesController: EnemiesController
    private val enemies: MutableList<SimpleEnemy> by lazy { IOC.atOrFail("enemies") }
    private val powerUpsController = IOC.atOrPut("powerUpsController", PowerUpsController())
    private val enemyGenerators = mutableListOf<EnemyGenerator>()
    private val bullets: MutableList<Bullet> = IOC.atOrPut("bullets", mutableListOf())
    private val players: MutableList<Player> = IOC.atOrPut("players", mutableListOf())
    private val collisionsSystem = CollisionsSystem()
    private val garbageCollectorSystem = GarbageCollectorSystem()
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }
    private lateinit var powerUpsGenerator: PowerUpsGenerator
    private lateinit var tower: Tower
    private val render: ShapeRenderer = IOC.atOrFail("shapeRenderer")

    private val showHintBarrier = AfterBarrier(3f)
    private var hintTime = 0f

    private var activePlayers = 0

    private val bossesCount: Int
        get() = enemies.count { it.isBoss }

    override fun onActivate() {
        showHintBarrier.reset()
        hintTime = 0f
        powerUpsGenerator = PowerUpsGenerator()
        tower = Tower(Vector2(Const.Screen.WIDTH / 2f, Const.Screen.HEIGHT / 3f * 2f))
        IOC.put("tower", tower)
        enemiesController = EnemiesController()
        IOC.put("enemiesController", enemiesController)

        controllers.forEach {
            controllers[it.key] = false
        }
        activePlayers = 0
        bullets.clear()
        players.clear()
        enemies.clear()
        enemyGenerators.clear()
        powerUpsController.reset()
    }

    override fun onUpdate() {
        hintTime += Gdx.graphics.deltaTime

        controllers.forEach {
            if (activePlayers < 4 && !it.value && it.key.getDirection().len() > 0.0001) {
                players.add(Player(PLAYER_SPAWN_POINT.cpy(), colors[activePlayers], it.key))
                controllers[it.key] = true
                enemyGenerators.add(EnemyGenerator(Const.Balance.generatorPlaces[activePlayers]))
                activePlayers++
            }
        }

        val playersIndex = players.foldIndexed(HashMap<Player, Int>()) { idx, map, player ->
            map.apply {
                put(player, idx)
            }
        }
        collisionsSystem.update(playersIndex)
        garbageCollectorSystem.update()

        players.forEachIndexed { idx, player ->
            player.update()
            tryShoot(player, idx)
            tryNova(player, idx)
        }

        enemiesController.update()
        bullets.forEach { it.update() }
        enemyGenerators.forEach { it.update() } // TODO: update only for online players
        powerUpsGenerator.update()
        powerUpsController.update()
        tower.update()
        bgm.update()
        bgm.updateLayers(playersCount = players.size, bossesCount = bossesCount)

        showHintBarrier.invoke {
            if (activePlayers == 0) {
                val space = TutorialState.Dimensions.BUTTON_SIZE + TutorialState.Dimensions.BUTTON_SPACING
                drawHint(
                    Vector2(
                        Const.Screen.WIDTH - space * 3,
                        Const.Screen.HEIGHT - space * 2
                    ), MathUtils.floor(hintTime * 2) % 2 == 0
                )
            }
        }

        checkGameOver()
        checkRestart()
    }

    private fun drawHint(pos: Vector2, state: Boolean) {
        render.managed(ShapeRenderer.ShapeType.Line) {
            it.color = Color.WHITE
            val upOffset = Vector2(
                TutorialState.Dimensions.BUTTON_SIZE + TutorialState.Dimensions.BUTTON_SPACING,
                0f
            )

            TutorialState.DrawHelpers.drawButton(it, pos.cpy().add(upOffset), false)

            val leftOffset = Vector2(
                0f,
                TutorialState.Dimensions.BUTTON_SIZE + TutorialState.Dimensions.BUTTON_SPACING
            )
            TutorialState.DrawHelpers.drawButton(it, pos.cpy().add(leftOffset), state)

            val downOffset = Vector2(
                TutorialState.Dimensions.BUTTON_SIZE + TutorialState.Dimensions.BUTTON_SPACING,
                TutorialState.Dimensions.BUTTON_SIZE + TutorialState.Dimensions.BUTTON_SPACING,
            )
            TutorialState.DrawHelpers.drawButton(it, pos.cpy().add(downOffset), false)

            val rightOffset = Vector2(
                2 * (TutorialState.Dimensions.BUTTON_SIZE + TutorialState.Dimensions.BUTTON_SPACING),
                TutorialState.Dimensions.BUTTON_SIZE + TutorialState.Dimensions.BUTTON_SPACING,
            )

            TutorialState.DrawHelpers.drawButton(it, pos.cpy().add(rightOffset), false)
        }
    }

    override fun onExit() {
    }

    private fun tryShoot(player: Player, playerId: Int) {
        val targets = enemies.map { enemy ->
            val dst = player.center.dst(enemy.pos)
            Pair(enemy, dst)
        }.filter { (_, dst) ->
            dst <= Const.Balance.MAX_SHOOT_DIST
        }.sortedBy { it.second }

        if (targets.isEmpty()) {
            return
        }

        // TODO: shoot only if there ia any enemy in a distance
        bgm.bullets.tryShootIf(playerId, player.stats.bulletsFireSpeed) {
            for (i in 0 until player.stats.bulletsCount) {
                val target = targets[i % targets.size]
                val dir = target.first.center.cpy().sub(player.p1).nor()
                val pos = player.p1.cpy()
                if (i >= targets.size) {
                    val jitter = randomDir().scl(2f)
                    pos.add(jitter)
                    dir.add(jitter).nor()
                }
                bullets.add(Bullet(pos, dir, player.stats.bulletDamage))
            }
        }
    }

    private fun tryNova(player: Player, playerId: Int) {
//        if (Gdx.input.isKeyPressed(Input.Keys.N)) {
//            logger.info("NOVA CHEAT")
//            player.nova = Nova(
//                center = player.center,
//                radius = player.height,
//                stats = NovaStats(),
//            )
//        }

        val novaOptions = player.stats.nova ?: return

        bgm.novas.tryShootIf(playerId, novaOptions.novaFreq) {
            player.nova = Nova(
                center = player.center,
                radius = player.height,
                stats = novaOptions,
            )
        }
    }

    private fun checkGameOver() {
        if (players.size == 0 && activePlayers != 0 || tower.currentHP <= 0) {
            IOC.put("state", States.GAME_OVER_SCREEN)
        }
    }

    private fun checkRestart() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            IOC.put("state", States.TITLE_SCREEN)
        }
    }
}
