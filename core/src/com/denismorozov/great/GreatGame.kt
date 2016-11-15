package com.denismorozov.great

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.denismorozov.great.screens.MainMenuScreen

class GreatGame : Game() {
    internal lateinit var batch: SpriteBatch
    internal lateinit var font: BitmapFont

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont() // Arial
        font.data.setScale(2f)
        font.color = Color.CYAN
        setScreen(MainMenuScreen(this))
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}