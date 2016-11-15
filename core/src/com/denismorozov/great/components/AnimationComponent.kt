package com.denismorozov.great.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.utils.ArrayMap

class AnimationComponent : Component {
    val animations = ArrayMap<String, Animation>()
}
