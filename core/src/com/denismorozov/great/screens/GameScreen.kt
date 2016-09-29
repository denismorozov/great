package com.denismorozov.great.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.denismorozov.great.GreatGame

class GameScreen(internal val game: GreatGame) : Screen {
    private val player: Sprite
    private val camera: OrthographicCamera
    private val stage: Stage
    private val touchpad: Touchpad
    private val touchpadSkin: Skin
    private val touchpadStyle: Touchpad.TouchpadStyle
    private val touchBackground: Drawable
    private val touchKnob: Drawable
    private val viewport: FitViewport

    init {
        camera = OrthographicCamera()
        viewport = FitViewport(1280f, 720f, camera)
        viewport.apply()
        camera.setToOrtho(false)

        touchpadSkin = Skin()
        touchpadSkin.add("touchBackground", Texture(Gdx.files.internal("touchBackground.png")))
        touchpadSkin.add("touchKnob", Texture(Gdx.files.internal("touchKnob.png")))
        touchBackground = touchpadSkin.getDrawable("touchBackground")
        touchKnob = touchpadSkin.getDrawable("touchKnob")

        touchpadStyle = Touchpad.TouchpadStyle()
        touchpadStyle.background = touchBackground
        touchpadStyle.knob = touchKnob

        touchpad = Touchpad(10f, touchpadStyle)
        touchpad.setBounds(75f, 75f, 300f, 300f)

        stage = Stage(viewport, game.batch)
        stage.addActor(touchpad)
        Gdx.input.inputProcessor = stage

        val playerTexture = Texture(Gdx.files.internal("player.png"))
        player = Sprite(playerTexture)
        player.setPosition(camera.viewportWidth/2, camera.viewportHeight/2)
        player.setSize(100f, 100f)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1f) // rgba
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        game.batch.projectionMatrix = camera.combined

        val movementSpeed = 15
        player.x += touchpad.knobPercentX * movementSpeed
        player.y += touchpad.knobPercentY * movementSpeed

        if (player.x < 0) {
            player.x = 0f
        } else if (player.x > camera.viewportWidth - player.width) {
            player.x = (camera.viewportWidth - player.width).toFloat()
        }

        if (player.y < 0) {
            player.y = 0f
        } else if (player.y > camera.viewportHeight - player.height) {
            player.y = (camera.viewportHeight - player.height).toFloat()
        }

        game.batch.begin()
        player.draw(game.batch)
        game.font.draw(game.batch, "X: ${touchpad.knobPercentX * 100}%", 50f, camera.viewportHeight - 50f)
        game.font.draw(game.batch, "Y: ${touchpad.knobPercentY * 100}%", 50f, camera.viewportHeight - 100f)
        game.font.draw(game.batch, "FPS: ${Gdx.graphics.framesPerSecond}", camera.viewportWidth - 150f, camera.viewportHeight - 50f)
        game.batch.end()

        stage.act(delta)
        stage.draw()
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
        player.texture.dispose()
    }
}