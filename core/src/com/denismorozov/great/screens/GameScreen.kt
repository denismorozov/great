package com.denismorozov.great.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.denismorozov.great.GreatGame
import com.denismorozov.great.components.PositionComponent
import com.denismorozov.great.components.RenderableComponent
import com.denismorozov.great.components.SpriteComponent
import com.denismorozov.great.components.VelocityComponent
import com.denismorozov.great.input.Joystick
import com.denismorozov.great.systems.MovementSystem
import com.denismorozov.great.systems.RenderSystem

class GameScreen(private val game: GreatGame) : Screen {
    private val camera: OrthographicCamera
    private val viewport: FitViewport

    private val stage: Stage
    private val engine: Engine

    private val tiledMap: TiledMap
    private val tiledMapRenderer: TiledMapRenderer

    init {
        camera = OrthographicCamera()
        viewport = FitViewport(1280f, 720f, camera)
        viewport.apply()
        camera.setToOrtho(false)
        camera.update() // check necessity

        Gdx.app.log("Camera init", "Global x " + Gdx.graphics.width)
        Gdx.app.log("Camera init", "Global y " + Gdx.graphics.height)
        Gdx.app.log("Camera init", "Viewport x " + camera.viewportWidth)
        Gdx.app.log("Camera init", "Viewport y " + camera.viewportHeight)

        tiledMap = TmxMapLoader().load("map.tmx")
        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap) // accepts unit scale - how many pixels map to world unit
        val mapProps = tiledMap.properties
        val numTiles = object {
            val x: Int = mapProps.get("width", Integer::class.java) as Int
            val y: Int = mapProps.get("height", Integer::class.java) as Int
        }
        val tilePixels = object {
           val x: Int = mapProps.get("tilewidth", Integer::class.java) as Int
           val y: Int = mapProps.get("tileheight", Integer::class.java) as Int
        }
        val mapPixels = object {
            val x = numTiles.x * tilePixels.x
            val y = numTiles.y * tilePixels.y
        }
        Gdx.app.log("Map init", "Num tiles x " + numTiles.x.toString())
        Gdx.app.log("Map init", "Num tiles y " + numTiles.y.toString())
        Gdx.app.log("Map init", "Tile pixels x " + tilePixels.y.toString())
        Gdx.app.log("Map init", "Tile tiles y " + tilePixels.y.toString())
        Gdx.app.log("Map init", "Map pixels x " + mapPixels.y.toString())
        Gdx.app.log("Map init", "Map pixels y " + mapPixels.y.toString())

        engine = Engine()
        engine.addSystem(MovementSystem())
        engine.addSystem(RenderSystem(game.batch))

        stage = Stage(viewport, game.batch)
        stage.addActor(Joystick.touchpad)
        Gdx.input.inputProcessor = stage

        val player = Entity()
            .add(PositionComponent(camera.viewportWidth/2, camera.viewportHeight/2))
            .add(VelocityComponent(150f))
            .add(SpriteComponent(Texture(Gdx.files.internal("player.png"))))
            .add(RenderableComponent())
        val enemy = Entity()
            .add(PositionComponent(camera.viewportWidth/2 + camera.viewportWidth/4, camera.viewportHeight/2))
            .add(VelocityComponent(150f))
            .add(SpriteComponent(Texture(Gdx.files.internal("enemy.png"))))
            .add(RenderableComponent())
        engine.addEntity(player)
        engine.addEntity(enemy)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1f) // rgba
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

//        camera.position.x = Math.min(Math.max(player.x, width/2), mapPixels.x - (width/2))
//        camera.position.x = Math.min(Math.max(player.y, height/2), mapPixels.y - (height/2))
        camera.update()

//        if (player.x < 0) {
//            player.x = 0f
//        } else if (player.x > camera.viewportWidth - player.width) {
//            player.x = (camera.viewportWidth - player.width).toFloat()
//        }
//
//        if (player.y < 0) {
//            player.y = 0f
//        } else if (player.y > camera.viewportHeight - player.height) {
//            player.y = (camera.viewportHeight - player.height).toFloat()
//        }

        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()

        game.batch.projectionMatrix = camera.combined
        game.batch.begin()
        game.font.draw(game.batch, "X: ${Joystick.touchpad.knobPercentX * 100}%", 50f, camera.viewportHeight - 50f)
        game.font.draw(game.batch, "Y: ${Joystick.touchpad.knobPercentY * 100}%", 50f, camera.viewportHeight - 100f)
        game.font.draw(game.batch, "FPS: ${Gdx.graphics.framesPerSecond}", camera.viewportWidth - 150f, camera.viewportHeight - 50f)
        engine.update(delta)
        game.batch.end()
        stage.act(delta)
        stage.draw()

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        camera.setToOrtho(false)
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
        tiledMap.dispose()
    }
}