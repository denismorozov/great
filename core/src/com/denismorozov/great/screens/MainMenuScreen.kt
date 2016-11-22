package com.denismorozov.great.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.denismorozov.great.GreatGame

class MainMenuScreen(private val game: GreatGame) : Screen {
    private val skin: Skin
    private val stage: Stage
    private val batch: SpriteBatch
    private val textButtonStyle: TextButtonStyle
    private val camera: OrthographicCamera
    private val viewport: ScreenViewport

    init {
        skin = Skin()

        val pixmap = Pixmap(100, 100, Format.RGBA8888)
        pixmap.setColor(Color.GREEN)
        pixmap.fill()
        skin.add("white", Texture(pixmap))

        val font = BitmapFont()
        skin.add("default", font)

        textButtonStyle = TextButtonStyle()
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY)
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY)
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE)
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY)
        textButtonStyle.font = skin.getFont("default")
        skin.add("default", textButtonStyle)

        batch = SpriteBatch()
        camera = OrthographicCamera()
        viewport = ScreenViewport(camera)
        viewport.apply(true)
        stage = Stage(viewport, batch)

        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun show() {
        val mainTable = Table()
        mainTable.setFillParent(true)
        mainTable.center()

        // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
        val gameIsRunning = game.gameScreen !== null
        val playButton = TextButton(
            if (gameIsRunning) "Resume" else "Play",
            textButtonStyle
        )
        val exitButton = TextButton("Exit", textButtonStyle)

        playButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (!gameIsRunning) {
                    game.gameScreen = GameScreen(game)
                }
                game.screen = game.gameScreen
            }
        })

        exitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                Gdx.app.exit()
            }
        })

        mainTable.add(playButton).width(camera.viewportWidth/2f).space(10f)
        mainTable.row()
        mainTable.add(exitButton).width(camera.viewportWidth/2f).space(10f)
        stage.addActor(mainTable)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun hide() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
