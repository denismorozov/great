package com.denismorozov.great

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.denismorozov.great.screens.GameScreen
import com.denismorozov.great.screens.MainMenuScreen

class GreatGame : Game() {
    internal lateinit var batch: SpriteBatch
    internal lateinit var font: BitmapFont

    lateinit var menuScreen: MainMenuScreen
    var gameScreen: GameScreen? = null

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont() // Arial
        font.data.setScale(Gdx.graphics.density)
        font.color = Color.WHITE

        // create screens
        menuScreen = MainMenuScreen(this)
        setScreen(menuScreen)
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        menuScreen.dispose()
        gameScreen?.dispose()
    }
}