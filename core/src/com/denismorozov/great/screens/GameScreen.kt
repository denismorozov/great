package com.denismorozov.great.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.denismorozov.great.GreatGame
import com.denismorozov.great.components.*
import com.denismorozov.great.entities.createEnemy
import com.denismorozov.great.entities.createPlayer
import com.denismorozov.great.input.Joystick
import com.denismorozov.great.input.Touch
import com.denismorozov.great.systems.*
import com.denismorozov.great.utilities.CollisionListener
import com.denismorozov.great.utilities.CollisionSystem
import com.denismorozov.great.utilities.CounterListener
import com.denismorozov.great.utilities.PhysicsEntityListener
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class GameScreen(private val game: GreatGame) : Screen {
    private val hudCamera: OrthographicCamera
    private val hudViewport: ScreenViewport
    private val gameCamera: OrthographicCamera
    private val gameViewport: FitViewport

    private val engine: PooledEngine
    private val enemyCounter: CounterListener

//    private val map: TiledMap
//    private val mapRenderer: TiledMapRenderer

    private val inputMultiplexer: InputMultiplexer
    private val stage: Stage
    private val world: World

    private val playerTexture: Texture
    private val enemyTexture: Texture

    private val enemyCounterLabel: Label
    private val waveCounterLabel: Label

    private val table: Table
    private var waveCount = 0

    companion object {
        val screenWidth: Int
            get() = Gdx.graphics.width
        val screenHeight: Int
            get() = Gdx.graphics.height
        val worldWidth = 16f
        val worldHeight = 9f
    }

    init {
        hudCamera = OrthographicCamera()
        hudViewport = ScreenViewport(hudCamera)
        hudViewport.apply(true)
        stage = Stage(hudViewport, game.batch)
        val labelStyle = Label.LabelStyle(game.font, Color.WHITE)
        waveCounterLabel = Label(" Wave 0", labelStyle)
        waveCounterLabel.color = Color.GREEN
        waveCounterLabel.setFontScale(Gdx.graphics.density)
        enemyCounterLabel = Label("", labelStyle)
        enemyCounterLabel.color = Color.RED
        enemyCounterLabel.setFontScale(Gdx.graphics.density)
        val menuLabel = Label("Menu", labelStyle)
        menuLabel.color = Color.GREEN
        menuLabel.setFontScale(Gdx.graphics.density)
        menuLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                game.screen = game.menuScreen
            }
        })
        table = Table()
        table.setFillParent(true)
//        table.debugTable()
        table.top()
        table
            .add(waveCounterLabel)
            .width(screenWidth* 1/2f)
        table
            .add(enemyCounterLabel)
            .width(screenWidth * 7/16f)
        table
            .add(menuLabel)
            .width(screenWidth * 1/16f)
        stage.addActor(table)
        stage.addActor(Joystick.touchpad)

        gameCamera = OrthographicCamera()
        gameViewport = FitViewport(worldWidth, worldHeight, gameCamera)
        gameViewport.apply(false)

//        map = TmxMapLoader().load("map.tmx")
//        val someArbitraryScaleThatLooksGood = 1f/30f // @TODO
//        mapRenderer = OrthogonalTiledMapRenderer(map, someArbitraryScaleThatLooksGood)

        world = World(Vector2(0f, 0f), false)

        // @TODO Finish configuring pooled engine, making components poolable, etc
        engine = PooledEngine()

        engine.addSystem(MovementSystem(gameCamera))
        engine.addSystem(RenderingSystem(game.batch))
        engine.addSystem(PhysicsSystem(world))
        engine.addSystem(PhysicsDebugSystem(world, gameCamera))
        val chasing = EnemyPathfinding() //consider adding later
//        chasing.setProcessing(false)
        engine.addSystem(chasing)

        enemyCounter = CounterListener()
        val enemyFamily = Family.all(EnemyComponent::class.java).get()
        engine.addEntityListener(enemyFamily, enemyCounter)
        engine.addEntityListener(PhysicsEntityListener(world))

        playerTexture = Texture(Gdx.files.internal("player.png"))
        enemyTexture = Texture(Gdx.files.internal("enemy.png"))

        val touchInput = Touch(gameCamera, engine, world)
        inputMultiplexer = InputMultiplexer(stage, touchInput)
        Gdx.input.inputProcessor = inputMultiplexer

        val collisionListeners = ArrayList<CollisionListener>()
        val collisionSystem = CollisionSystem(engine, collisionListeners)
        world.setContactListener(collisionSystem)
    }

    fun initializeWave(waveNumber: Int = 1) {
        engine.removeAllEntities()
        engine.addEntity(createPlayer(engine, world, playerTexture))

        val numEnemies = waveNumber * 10
        val random = Random()
        val timer = Timer()
        for (i in 1..numEnemies) {
            val r = random.nextFloat()
            val x = worldWidth * r
            val y = if (r > 0.5) worldHeight else -worldHeight
            val enemy = createEnemy(engine, world, enemyTexture, x, y)
            timer.schedule(object : TimerTask() {
                override fun run() {
                    enemy.add(TextureComponent(enemyTexture))
                }
            }, (1000 * 5 * r).toLong())
            engine.addEntity(enemy)
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1f) // rgba
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

//        mapRenderer.setView(gameCamera)
//        mapRenderer.render()

        game.batch.projectionMatrix = gameCamera.combined
        gameViewport.apply()
        engine.update(delta)
        gameCamera.update()

        stage.viewport.apply()
        stage.act(delta)
        stage.draw()

        if (enemyCounter.entityCount === 0) {
            enemyCounterLabel.isVisible = false
            waveCount++
            waveCounterLabel.setText(" Wave $waveCount")
            initializeWave(waveCount)
        } else {
            enemyCounterLabel.isVisible = true
            enemyCounterLabel.setText(enemyCounter.entityCount.toString())
        }
    }

    override fun resize(width: Int, height: Int) {
        hudViewport.update(width, height, true)
        gameViewport.update(width, height, false)
    }

    override fun show() {
        Gdx.input.inputProcessor = inputMultiplexer
        if (waveCount > 0) {
            engine.systems.forEach { it.setProcessing(true) }
        }
    }

    override fun hide() {
        engine.systems.forEach { it.setProcessing(false) }
    }

    override fun pause() {

    }

    override fun resume() {
    }

    override fun dispose() {
//        map.dispose()
        world.dispose()
        playerTexture.dispose()
        enemyTexture.dispose()
    }
}