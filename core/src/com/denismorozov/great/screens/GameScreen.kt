package com.denismorozov.great.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.denismorozov.great.GreatGame
import com.denismorozov.great.components.*
import com.denismorozov.great.input.Joystick
import com.denismorozov.great.systems.*

class GameScreen(private val game: GreatGame) : Screen {
    private val hudCamera: OrthographicCamera
    private val hudViewport: FitViewport
    private val gameCamera: OrthographicCamera
    private val gameViewport: FitViewport

    private val engine: PooledEngine

    private val map: TiledMap
    private val mapRenderer: TiledMapRenderer

    private val stage: Stage
    private val world: World

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
        hudViewport = FitViewport(screenWidth.toFloat(), screenHeight.toFloat(), hudCamera)
        hudViewport.apply(true)

        stage = Stage(hudViewport, game.batch)
        stage.addActor(Joystick.touchpad)
        Gdx.input.inputProcessor = stage

        gameCamera = OrthographicCamera()
        gameViewport = FitViewport(worldWidth, worldHeight, gameCamera)
        gameViewport.apply(true)

        Gdx.app.log("Camera init", "Global x " + screenWidth)
        Gdx.app.log("Camera init", "Global y " + screenHeight)
        Gdx.app.log("Camera init", "Viewport x " + gameCamera.viewportWidth)
        Gdx.app.log("Camera init", "Viewport y " + gameCamera.viewportHeight)


        map = TmxMapLoader().load("map.tmx")
        val pixelsPerPeter: Float = screenWidth / worldWidth
        mapRenderer = OrthogonalTiledMapRenderer(map, pixelsPerPeter)

//        val mapProps = map.properties
//        val numTiles = object {
//            val x: Int = mapProps.get("width", Integer::class.java) as Int
//            val y: Int = mapProps.get("height", Integer::class.java) as Int
//        }
//        val tilePixels = object {
//           val x: Int = mapProps.get("tilewidth", Integer::class.java) as Int
//           val y: Int = mapProps.get("tileheight", Integer::class.java) as Int
//        }
//        val mapPixels = object {
//            val x = numTiles.x * tilePixels.x
//            val y = numTiles.y * tilePixels.y
//        }
//        Gdx.app.log("Map init", "Num tiles x " + numTiles.x.toString())
//        Gdx.app.log("Map init", "Num tiles y " + numTiles.y.toString())
//        Gdx.app.log("Map init", "Tile pixels x " + tilePixels.y.toString())
//        Gdx.app.log("Map init", "Tile tiles y " + tilePixels.y.toString())
//        Gdx.app.log("Map init", "Map pixels x " + mapPixels.y.toString())
//        Gdx.app.log("Map init", "Map pixels y " + mapPixels.y.toString())


        world = World(Vector2(0f, 0f), false)

        engine = PooledEngine()

        engine.addSystem(MovementSystem(gameCamera))
        engine.addSystem(RenderingSystem(game.batch))
        engine.addSystem(PhysicsSystem(world))
        engine.addSystem(PhysicsDebugSystem(world, gameCamera))

        engine.addEntity(createPlayer())
        engine.addEntity(createEnemy(1f, 1f))
        engine.addEntity(createEnemy(1f, -1f))
        engine.addEntity(createEnemy(-1f, 1f))
        engine.addEntity(createEnemy(-1f, -1f))
    }

    private fun createPlayer(): Entity {
        val player = engine.createEntity()

        player
            .add(PlayerComponent())
            .add(TextureComponent(Texture(Gdx.files.internal("player.png"))))
            .add(TransformComponent())

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        // @TODO convert to meters
        val center = Vector3(Gdx.graphics.width/2f, Gdx.graphics.height/2f, 0f)
        bodyDef.position.set(center.x, center.y)
        val body = world.createBody(bodyDef)
        val circle = CircleShape()
        circle.radius = 0.5f
        val fixtureDef = FixtureDef()
        fixtureDef.shape = circle
        fixtureDef.density = 1f
        fixtureDef.restitution = 0.2f
        body.createFixture(fixtureDef)
        circle.dispose()
        player.add(BodyComponent(body))

        return player
    }

    private fun createEnemy(x: Float, y:Float): Entity {
        val enemy = engine.createEntity()

        enemy
            .add(TextureComponent(Texture(Gdx.files.internal("enemy.png"))))
            .add(TransformComponent())

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        val center = Vector3(Gdx.graphics.width/2f, Gdx.graphics.height/2f, 0f)
        bodyDef.position.set(center.x + x, center.y + y)
        val body = world.createBody(bodyDef)
        val circle = CircleShape()
        circle.radius = 0.5f
        val fixtureDef = FixtureDef()
        fixtureDef.shape = circle
        fixtureDef.density = 1f
        fixtureDef.restitution = 0.2f
        body.createFixture(fixtureDef)
        circle.dispose()
        enemy.add(BodyComponent(body))

        return enemy
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1f) // rgba
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
//        game.batch.enableBlending()
        gameCamera.update()

        hudViewport.apply()
        mapRenderer.setView(hudCamera)
        mapRenderer.render()

        game.batch.projectionMatrix = gameCamera.combined
        gameViewport.apply() // not sure
        engine.update(delta)

        game.batch.projectionMatrix = hudCamera.combined
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
//        SoundManager.dispose()
    }
}