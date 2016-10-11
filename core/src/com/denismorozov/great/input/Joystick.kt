package com.denismorozov.great.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.denismorozov.great.systems.RenderingSystem

val deadZoneRadius = 1f
val position = Vector2(Gdx.app.graphics.width/8f, Gdx.app.graphics.height/8f)
val size = Vector2(200f, 200f)

object Joystick {
    val touchpad: Touchpad
    val X: Float
        get() = touchpad.knobPercentX
    val Y: Float
        get() = touchpad.knobPercentY

    init {
        val skin = Skin()
        skin.add("background", Texture(Gdx.files.internal("touchBackground.png")))
        skin.add("knob", Texture(Gdx.files.internal("touchKnob.png")))

        val style = Touchpad.TouchpadStyle()
        style.background = skin.getDrawable("background")
        style.knob = skin.getDrawable("knob")

        touchpad = Touchpad(deadZoneRadius, style)
        touchpad.setBounds(position.x, position.y, size.x, size.y)
    }

}