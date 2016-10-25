package com.denismorozov.great.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.denismorozov.great.GreatGame
import com.denismorozov.great.components.*
import com.denismorozov.great.input.Joystick
import com.denismorozov.great.input.Touch
import com.denismorozov.great.systems.*
import com.denismorozov.great.utilities.CollisionListener
import com.denismorozov.great.utilities.CollisionSystem
import com.denismorozov.great.utilities.PhysicsEntityListener
import java.util.*

class GameScreen(private val game: GreatGame) : Screen {
    private val hudCamera: OrthographicCamera
    private val hudViewport: ScreenViewport
    private val gameCamera: OrthographicCamera
    private val gameViewport: FitViewport

    private val engine: PooledEngine

    private val map: TiledMap
    private val mapRenderer: TiledMapRenderer

    private val stage: Stage
    private val world: World

    private val playerTexture: Texture
    private val enemyTexture: Texture

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
        stage.addActor(Joystick.touchpad)
        //stage.addActor()

        gameCamera = OrthographicCamera()
        gameViewport = FitViewport(worldWidth, worldHeight, gameCamera)
        gameViewport.apply(false)

        map = TmxMapLoader().load("map.tmx")
        val someArbitraryScaleThatLooksGood = 1f/30f // @TODO
        mapRenderer = OrthogonalTiledMapRenderer(map, someArbitraryScaleThatLooksGood)

        world = World(Vector2(0f, 0f), false)

        // @TODO Finish configuring pooled engine, making components poolable, etc
        engine = PooledEngine()

        engine.addSystem(MovementSystem(gameCamera))
        engine.addSystem(RenderingSystem(game.batch))
        engine.addSystem(PhysicsSystem(world))
        engine.addSystem(PhysicsDebugSystem(world, gameCamera))
        val chasing = EnemyPathfinding()
        chasing.setProcessing(false)
        engine.addSystem(chasing)

        engine.addEntityListener(PhysicsEntityListener(world))

        playerTexture = Texture(Gdx.files.internal("player.png"))
        enemyTexture = Texture(Gdx.files.internal("enemy.png"))

        engine.addEntity(createPlayer())
        for (i in -10..10 step 2) {
            engine.addEntity(createEnemy(3f, i.toFloat() + .1f))
            engine.addEntity(createEnemy(3f, i.toFloat()))
            engine.addEntity(createEnemy(-3f, i.toFloat()))
        }

        val touchInput = Touch(gameCamera, engine, world)
        val inputMultiplexer = InputMultiplexer(stage, touchInput)
        Gdx.input.inputProcessor = inputMultiplexer

        val collisionListeners = ArrayList<CollisionListener>()
        val collisionSystem = CollisionSystem(engine, collisionListeners)
        world.setContactListener(collisionSystem)
    }

    private fun createPlayer(): Entity {
        val player = engine.createEntity()

        player
            .add(PlayerComponent())
            .add(TextureComponent(playerTexture))
            .add(TransformComponent())

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        val center = Vector3(worldWidth/2f, worldHeight/2f, 0f)
        bodyDef.position.set(center.x, center.y)
        val body = world.createBody(bodyDef)
        val circle = CircleShape()
        circle.radius = 0.5f
        val fixtureDef = FixtureDef()
        fixtureDef.shape = circle
        fixtureDef.density = 1f
        fixtureDef.restitution = 0.2f
        body.createFixture(fixtureDef)
        body.userData = player
//        body.userData = "player"
        circle.dispose()
        player.add(BodyComponent(body))

        return player
    }

    private fun createEnemy(x: Float, y:Float): Entity {
        val enemy = engine.createEntity()

        enemy
            .add(EnemyComponent())
            .add(TextureComponent(enemyTexture))
            .add(TransformComponent())

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        val center = Vector3(worldWidth/2f, worldHeight/2f, 0f)
        bodyDef.position.set(center.x + x, center.y + y)
        val body = world.createBody(bodyDef)
        val circle = CircleShape()
        circle.radius = 0.5f
        val fixtureDef = FixtureDef()
        fixtureDef.shape = circle
        fixtureDef.density = 1f
        fixtureDef.restitution = 0.2f
        body.createFixture(fixtureDef)
        body.userData = enemy
//        body.userData = "enemy"
        circle.dispose()
        enemy.add(BodyComponent(body))

        return enemy
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1f) // rgba
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)


        mapRenderer.setView(gameCamera)
        mapRenderer.render()

        game.batch.projectionMatrix = gameCamera.combined
        gameViewport.apply()
        engine.update(delta)
        gameCamera.update()

        stage.viewport.apply()
        stage.act(delta)
        stage.draw()

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit()
    }

    override fun resize(width: Int, height: Int) {
        hudViewport.update(width, height, true)
        gameViewport.update(width, height, false)
    }

    override fun show() {
    }

    override fun hide() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        map.dispose()
        world.dispose()
        playerTexture.dispose()
        enemyTexture.dispose()
//        SoundManager.dispose()
    }
}