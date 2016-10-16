package com.denismorozov.great.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Array
import com.denismorozov.great.components.TextureComponent
import com.denismorozov.great.components.TransformComponent


class RenderingSystem(private val batch: SpriteBatch) : IteratingSystem(
        Family.all(TransformComponent::class.java, TextureComponent::class.java).get()
) {
    private val renderQueue = Array<Entity>()

    private val textureM = ComponentMapper.getFor(TextureComponent::class.java)
    private val transformM = ComponentMapper.getFor(TransformComponent::class.java)


    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        batch.begin()

        for (entity in renderQueue) {
            val texture = textureM.get(entity)
            val transform = transformM.get(entity)

            if (transform.isHidden) {
                continue
            }

//            val width = texture.region.regionWidth.toFloat()
            val width = 1f
//            val height = texture.region.regionHeight.toFloat()
            val height = 1f

            val originX = width / 2f
            val originY = height / 2f

            batch.draw(
                texture.region,
                transform.position.x - originX,
                transform.position.y - originY,
                originX,
                originY,
                width,
                height,
                transform.scale.x,
                transform.scale.y,
                transform.rotation
            )
        }

        batch.end()
        renderQueue.clear()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        renderQueue.add(entity)
    }
}
