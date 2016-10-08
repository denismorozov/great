package com.denismorozov.great.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.denismorozov.great.components.PositionComponent
import com.denismorozov.great.components.RenderableComponent
import com.denismorozov.great.components.SpriteComponent

class RenderSystem(val batch: SpriteBatch) : IteratingSystem(
    Family.all(
            RenderableComponent::class.java,
            SpriteComponent::class.java,
            PositionComponent::class.java
    ).get()
) {
    private val pm = ComponentMapper.getFor(PositionComponent::class.java)
    private val sm = ComponentMapper.getFor(SpriteComponent::class.java)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = pm.get(entity)
        val sprite = sm.get(entity)
        batch.draw(sprite.sprite.texture, position.x, position.y)
    }
}