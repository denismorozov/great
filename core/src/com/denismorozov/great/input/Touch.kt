package com.denismorozov.great.input

import com.badlogic.ashley.core.*
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.denismorozov.great.components.*
import com.denismorozov.great.systems.SpellSystem

class Touch (val camera: OrthographicCamera, val engine: PooledEngine, val spellSystem: SpellSystem) : InputProcessor {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val gameCoordinates = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val family = Family.all(PlayerComponent::class.java).get()
        val player = engine.getEntitiesFor(family).firstOrNull()
        if (player !== null) {
            spellSystem.touchDown(gameCoordinates, player)
        }
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun keyDown(keycode: Int): Boolean = false
    override fun keyTyped(character: Char): Boolean = false
    override fun keyUp(keycode: Int): Boolean = false
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false
    override fun scrolled(amount: Int): Boolean = false // hmm...?
}