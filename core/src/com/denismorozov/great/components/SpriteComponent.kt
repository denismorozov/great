package com.denismorozov.great.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite

class SpriteComponent(texture: Texture) : Component {
    val sprite: Sprite
    init {
        sprite = Sprite(texture)
    }
}