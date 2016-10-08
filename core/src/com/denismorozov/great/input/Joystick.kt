package com.denismorozov.great.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad

val deadZoneRadius = 10f
val position = Vector2(75f, 75f)
val size = Vector2(300f, 300f)

object Joystick {
    var touchpad: Touchpad
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