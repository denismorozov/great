package com.denismorozov.great.input

import com.badlogic.gdx.InputProcessor

class Touch : InputProcessor {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
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