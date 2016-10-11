package com.denismorozov.great.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion


class TextureComponent(texture: Texture) : Component {
    val region: TextureRegion
    init {
        region = TextureRegion(texture)
    }
}
