package com.denismorozov.great

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.denismorozov.great.screens.GameScreen

class GreatGame : Game() {
    internal lateinit var batch: SpriteBatch
    internal lateinit var font: BitmapFont

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont() // Arial
        font.data.setScale(2f)
        setScreen(GameScreen(this))
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}