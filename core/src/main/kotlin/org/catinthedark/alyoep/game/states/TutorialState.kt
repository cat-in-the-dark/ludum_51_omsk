package org.catinthedark.alyoep.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.sin
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.catinthedark.alyoep.audio.Bgm
import org.catinthedark.alyoep.game.Const
import org.catinthedark.alyoep.game.States
import org.catinthedark.alyoep.game.control.PlayerController
import org.catinthedark.alyoep.game.entities.*
import org.catinthedark.alyoep.game.entities.powerups.FirePowerUp
import org.catinthedark.alyoep.game.entities.powerups.HealPowerUp
import org.catinthedark.alyoep.game.entities.powerups.NovaPowerUp
import org.catinthedark.alyoep.game.states.TutorialState.DrawHelpers.drawButton
import org.catinthedark.alyoep.lib.*
import org.catinthedark.alyoep.lib.interfaces.ITransform
import org.catinthedark.alyoep.lib.math.randomDir
import org.catinthedark.alyoep.lib.states.IState
import org.slf4j.LoggerFactory

class TutorialState : IState {
    object Dimensions {
        val stage = Vector2(640f, 480f)

        const val BUTTON_SIZE = 75f
        const val BUTTON_SPACING = 30f
        const val BUTTON_RADIUS = 20f
        const val BUTTON_HEIGHT_PRESSED = 0f
        const val BUTTON_HEIGHT_RELEASED = 15f
        const val SPACE_WIDTH = 250f
        const val ARROW_WIDTH = 80f
        const val ARROW_DX = 20f
    }

    object DrawHelpers {
        fun drawSpaceBar(renderer: ShapeRenderer, pos: Vector2) {
            val spaceSymWidth = 200f
            val spaceSymPos =
                pos.cpy().add((Dimensions.SPACE_WIDTH - spaceSymWidth) / 2, -10f)
            renderer.roundedRectLine(
                pos.x,
                pos.y,
                Dimensions.SPACE_WIDTH,
                Dimensions.BUTTON_SIZE,
                Dimensions.BUTTON_RADIUS
            )
            renderer.roundedRectLineShadow(
                pos.x,
                pos.y + 10,
                Dimensions.SPACE_WIDTH,
                Dimensions.BUTTON_SIZE,
                Dimensions.BUTTON_RADIUS
            )
            renderer.roundedRectLineShadow(
                spaceSymPos.x,
                spaceSymPos.y,
                spaceSymWidth,
                Dimensions.BUTTON_SIZE,
                10f
            )
        }

        private fun drawArrow(renderer: ShapeRenderer, pos: Vector2, width: Float, height: Float) {
            val rp1 = pos.cpy()
            val rp2 = pos.cpy().add(width, 0f)
            val rp3 = pos.cpy().add(width, height)
            val rp4 = pos.cpy().add(0f, height)
            val triangleCenter = Vector2(pos.x + width + height / 2, pos.y + height / 2)
            val radius = Vector2(height, 0f)
            val p1 = triangleCenter.cpy().add(radius.cpy())
            val p2 = triangleCenter.cpy().add(radius.cpy().rotateDeg(120f))
            val p3 = triangleCenter.cpy().add(radius.cpy().rotateDeg(240f))
            renderer.polygon2(rp1, rp2, p3, p1, p2, rp3, rp4)
        }

        fun animateArrow(time: Float, renderer: ShapeRenderer, pos: Vector2, width: Float, height: Float) {
            val dx = MathUtils.map(-1f, 1f, 0f, Dimensions.ARROW_DX, sin(time * 10))
            pos.add(dx, 0f)
            drawArrow(renderer, pos, width, height)
        }

        fun drawButton(renderer: ShapeRenderer, pos: Vector2, pressed: Boolean) {
            val offset = if (pressed) {
                Dimensions.BUTTON_HEIGHT_PRESSED
            } else {
                Dimensions.BUTTON_HEIGHT_RELEASED
            }
            renderer.roundedRectLine(
                pos.x,
                pos.y - offset,
                Dimensions.BUTTON_SIZE,
                Dimensions.BUTTON_SIZE,
                Dimensions.BUTTON_RADIUS
            )
            renderer.roundedRectLineShadow(
                pos.x,
                pos.y,
                Dimensions.BUTTON_SIZE,
                Dimensions.BUTTON_SIZE,
                Dimensions.BUTTON_RADIUS
            )
        }
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    private val render: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }
    private val controllers: Map<PlayerController, Boolean> = IOC.atOrFail("input")
    private val players: MutableList<Player> = IOC.atOrPut("players", mutableListOf())
    private val enemies: MutableList<SimpleEnemy> = IOC.atOrPut("enemies", mutableListOf())
    private val bullets: MutableList<Bullet> = IOC.atOrPut("bullets", mutableListOf())
    private val enemiesController: EnemiesController = IOC.atOrPut("enemiesController", EnemiesController())
    private val powerUpsController = IOC.atOrPut("powerUpsController", PowerUpsController())
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }
    private lateinit var tower: Tower

    private var time = 0f

    private val enemyBarrier = AfterBarrier(2f)
    private val powerUpBarriers = listOf(OnceBarrier(0f), OnceBarrier(0.15f), OnceBarrier(0.3f))
    private val pickupBarrier = OnceBarrier(1.5f)
    private val moveBackBarrier = OnceBarrier(2.5f)
    private val showNextBarrier = AfterBarrier(5f)
    private val forceNextBarrier = OnceBarrier(8f)

    private val inputBarrier = RepeatBarrier(0f, 1f)

    private val dirButtons =
        mutableMapOf(Pair("up", false), Pair("down", false), Pair("left", false), Pair("right", false))
    private val wasdButtons = mapOf(Pair("w", false), Pair("s", false), Pair("a", false), Pair("d", false))

    private var tutorialStage = 0
    private var prevTutorialStage = 0

    private val collisionsSystem = CollisionsSystem()
    private val garbageCollectorSystem = GarbageCollectorSystem()
    private lateinit var player: Player
    private lateinit var enemyGenerator: EnemyGenerator

    private val stagePos: Vector2 = Vector2(
        50f, 50f
    )

    private val spawnPos = stagePos.cpy().add(Dimensions.stage.cpy().scl(0.2f))

    private lateinit var inputController: TutorialInputController
    private var advancePressed = false

    override fun onActivate() {
        IOC.put("showTutorial", false)
        time = 0f
        inputController = TutorialInputController()
        showNextBarrier.reset()

        player = Player(spawnPos.cpy().add(0f, 200f), Color.CHARTREUSE, inputController)
        player.currentHP = player.stats.maxHP * 0.5f
        players.clear()
        players.add(player)

        inputController.boundTo(player)

        tower = Tower(spawnPos.cpy().add(0f, 250f))
        tower.currentHP = Const.Balance.Tower.MAX_HP * 0.5f
        IOC.put("tower", tower)

        enemyGenerator = EnemyGenerator(
            Rectangle(stagePos.x + Dimensions.stage.x, stagePos.y, 50f, Dimensions.stage.y),
            demoMode = true
        )
    }

    override fun onUpdate() {
        time += Gdx.graphics.deltaTime
        collisionsSystem.update(hashMapOf(Pair(player, 0)))
        garbageCollectorSystem.update()
        powerUpsController.update()
        enemyBarrier.invoke {
            enemiesController.update()
            enemyGenerator.update(currentMaxBossness = 0)
        }

        bgm.update()
        bgm.updateLayers(playersCount = 1, maxBossness = 0)

        tryShoot(player, 0)
        tryNova(player, 0)

        player.update()
        bullets.forEach { it.update() }

        updateTutorial()
        advanceTutorial()
    }

    private fun advanceTutorial() {
        var shouldAdvance = false
        forceNextBarrier.invoke {
            if (tutorialStage < 3) {
                shouldAdvance = true
            }
        }

        for (controller in controllers) {
            if (controller.key.isStartJustPressed()) {
                shouldAdvance = true
                advancePressed = true
            }
        }

        if (shouldAdvance) {
            if (tutorialStage < 3) {
                tutorialStage++
            } else {
                IOC.put("state", States.PLAYER_SCREEN)
            }
        }
    }

    private fun updateTutorial() {
        if (tutorialStage != prevTutorialStage) {
            exitStage(prevTutorialStage)
            initStage(tutorialStage)
        }

        prevTutorialStage = tutorialStage
        updateStage(tutorialStage)
        drawTutorialUi()
    }

    private fun drawTutorialUi() {
        // border
        render.managed(ShapeRenderer.ShapeType.Filled) {
            it.color = Color.BLACK
            it.rect(0f, 0f, stagePos.x, Const.Screen.HEIGHT.toFloat())
            it.rect(0f, 0f, Const.Screen.WIDTH.toFloat(), stagePos.y)
            it.rect(
                0f,
                stagePos.y + Dimensions.stage.y,
                Const.Screen.WIDTH.toFloat(),
                Const.Screen.HEIGHT - Dimensions.stage.y - stagePos.y
            )
            it.rect(
                stagePos.x + Dimensions.stage.x,
                0f,
                Const.Screen.WIDTH - Dimensions.stage.x - stagePos.x,
                Const.Screen.HEIGHT.toFloat()
            )
        }

        val progressPos =
            stagePos.cpy().add(Dimensions.stage.x / 2 - 80, Dimensions.stage.y + Dimensions.BUTTON_SPACING)
        drawProgress(progressPos)

        render.managed(ShapeRenderer.ShapeType.Line) {
            it.color = Color.WHITE
            // stage border
            it.rect(stagePos.x, stagePos.y, Dimensions.stage.x, Dimensions.stage.y)

            if (tutorialStage == 0) {
                // buttons
                drawButtons(
                    it,
                    Vector2(
                        750f,
                        stagePos.y + Dimensions.stage.y - Dimensions.BUTTON_SIZE * 2 - Dimensions.BUTTON_SPACING * 2
                    )
                )
            }

            showNextBarrier.invoke {
                if (!advancePressed) {
                    val spacePos = Vector2(
                        stagePos.x + Dimensions.stage.x / 2 - (Dimensions.SPACE_WIDTH + Dimensions.ARROW_WIDTH + 2 * Dimensions.BUTTON_SPACING) / 2,
                        stagePos.y + Dimensions.stage.y + 3 * Dimensions.BUTTON_SPACING
                    )
                    DrawHelpers.drawSpaceBar(it, spacePos)

                    val arrowPos = spacePos.cpy().add(Dimensions.SPACE_WIDTH + 2 * Dimensions.BUTTON_SPACING, 15f)
                    DrawHelpers.animateArrow(
                        time,
                        it,
                        arrowPos,
                        Dimensions.ARROW_WIDTH,
                        45f
                    )
                }
            }
        }
    }

    private fun drawProgress(pos: Vector2) {
        render.managed(ShapeRenderer.ShapeType.Filled) {
            for (i in 0 until 4) {
                var radius = 10f
                var color = Color.DARK_GRAY
                if (i == tutorialStage) {
                    radius = 15f
                    color = Color.WHITE
                }

                it.color = color
                it.circle(pos.x + 40 * i, pos.y, radius)
            }
        }
    }

    private fun drawButtons(renderer: ShapeRenderer, pos: Vector2) {
        val upOffset = Vector2(
            Dimensions.BUTTON_SIZE + Dimensions.BUTTON_SPACING,
            0f
        )

        drawButton(renderer, pos.cpy().add(upOffset), dirButtons["up"]!!)

        val leftOffset = Vector2(
            0f,
            Dimensions.BUTTON_SIZE + Dimensions.BUTTON_SPACING
        )
        drawButton(renderer, pos.cpy().add(leftOffset), dirButtons["left"]!!)

        val downOffset = Vector2(
            Dimensions.BUTTON_SIZE + Dimensions.BUTTON_SPACING,
            Dimensions.BUTTON_SIZE + Dimensions.BUTTON_SPACING,
        )
        drawButton(renderer, pos.cpy().add(downOffset), dirButtons["down"]!!)

        val rightOffset = Vector2(
            2 * (Dimensions.BUTTON_SIZE + Dimensions.BUTTON_SPACING),
            Dimensions.BUTTON_SIZE + Dimensions.BUTTON_SPACING,
        )

        drawButton(renderer, pos.cpy().add(rightOffset), dirButtons["right"]!!)
    }

    private fun updateStage(stage: Int) {
        if (stage == 0) {
            inputBarrier.invoke {
                val pos = when (inputBarrier.count % 4) {
                    1 -> spawnPos.cpy()
                    2 -> spawnPos.cpy().add(200f, 0f)
                    3 -> spawnPos.cpy().add(200f, 200f)
                    0 -> spawnPos.cpy().add(0f, 200f)
                    else -> {
                        TODO()
                    }
                }

                val button = when (inputBarrier.count % 4) {
                    1 -> "up"
                    2 -> "right"
                    3 -> "down"
                    0 -> "left"
                    else -> {
                        TODO()
                    }
                }

                dirButtons[button] = true
                inputController.moveTo(pos)
            }

            if (!inputController.isMoving()) {
                dirButtons.forEach {
                    dirButtons[it.key] = false
                }
            }
        } else {
            if (player.shouldDestroy) {
                IOC.put("state", States.PLAYER_SCREEN)
            }

            when (stage) {
                1 -> {
                    enemyGenerator.demoWaveNumber = 3
                    powerUpBarriers.forEachIndexed { i, it ->
                        it.invoke {
                            if (i == 0) {
                                inputController.moveTo(Vector2(spawnPos.cpy().add(0f, 150f)))
                            }
                            powerUpsController.addPowerUp(
                                FirePowerUp(
                                    stagePos.cpy().add(Dimensions.stage.cpy().scl(0.5f).add(50f * (i - 1), 0f))
                                )
                            )
                        }
                    }
                    pickupBarrier.invoke {
                        inputController.moveTo(Vector2(spawnPos.cpy().add(300f, 150f)))
                    }
                    moveBackBarrier.invoke {
                        inputController.moveTo(Vector2(spawnPos.cpy().add(0f, 150f)))
                    }
                    //            inputController.dir = Vector2(0.5f, 0f)
                }

                2 -> {
                    enemyGenerator.demoWaveNumber = 5
                    powerUpBarriers.forEachIndexed { i, it ->
                        it.invoke {
                            if (i == 0) {
                                inputController.moveTo(Vector2(spawnPos.cpy().add(0f, 150f)))
                            }
                            powerUpsController.addPowerUp(
                                NovaPowerUp(
                                    stagePos.cpy().add(Dimensions.stage.cpy().scl(0.5f).add(50f * (i - 1), 0f))
                                )
                            )
                        }
                    }
                    pickupBarrier.invoke {
                        inputController.moveTo(Vector2(spawnPos.cpy().add(300f, 150f)))
                    }
                    //            inputController.dir = Vector2(0.5f, 0f)
                }

                3 -> {
                    tower.update()

                    powerUpBarriers.forEachIndexed { i, it ->
                        it.invoke {
                            if (i == 0) {
                                powerUpsController.addPowerUp(
                                    HealPowerUp(
                                        stagePos.cpy().add(Dimensions.stage.cpy().scl(0.5f))
                                    )
                                )
                            }
                        }
                    }
                    pickupBarrier.invoke {
                        enemyGenerator.demoWaveNumber = 10
                        inputController.moveTo(Vector2(spawnPos.cpy().add(0f, 150f)))
                    }
                    moveBackBarrier.invoke {
                        inputController.moveTo(Vector2(spawnPos.cpy().add(300f, 150f)))
                    }
                    //            inputController.dir = Vector2(0.5f, 0f)
                }
            }
        }
    }

    private fun initStage(stage: Int) {
        logger.info("init $stage")
        if (stage > 1) {
            player.stats.bulletsCount = 3
            player.stats.bulletsFireSpeed = 2
        }
        if (stage > 2) {
            player.stats.nova = NovaStats()
            player.stats.nova?.novaFreq = 3
            player.stats.nova?.novaDmg = 3f
        }
    }

    private fun exitStage(stage: Int) {
        logger.info("exit $stage")
        powerUpsController.reset()
        powerUpBarriers.forEach { it.reset() }
        pickupBarrier.reset()
        moveBackBarrier.reset()
        forceNextBarrier.reset()
    }

    override fun onExit() {
        IOC.atOrFail<MutableList<Player>>("players").clear()
    }

    private fun tryShoot(player: Player, playerId: Int) {
        val targets = enemies.map { enemy ->
            val dst = player.center.dst(enemy.pos)
            Pair(enemy, dst)
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
        val novaOptions = player.stats.nova ?: return

        bgm.novas.tryShootIf(playerId, novaOptions.novaFreq) {
            player.nova = Nova(
                center = player.center,
                radius = player.height,
                stats = novaOptions,
            )
        }
    }

    inner class TutorialInputController : PlayerController() {
        var dir = Vector2(0f, 0f)
            set(value) {
                field = value
                moveToMode = false
            }
        private var movable: ITransform? = null
        private var targetPos = Vector2(0f, 0f)
        private var moveToMode = false

        fun boundTo(transform: ITransform) {
            movable = transform
        }

        fun moveTo(pos: Vector2) {
            targetPos = pos
            moveToMode = true
        }

        fun isMoving(): Boolean {
            movable?.let {
                return targetPos.cpy().sub(it.pos).len2() > 5f
            }
            return false
        }

        override fun getDirection(): Vector2 {
            return if (moveToMode) {
                var dist = Vector2(0f, 0f)
                movable?.let {
                    dist = targetPos.cpy().sub(it.pos)
                    if (dist.len2() < 5f) {
                        dist.setZero()
                    }
                }
                dist.scl(0.5f)
            } else {
                dir
            }
        }

        override fun isStartPressed(): Boolean {
            return false
        }
    }
}
