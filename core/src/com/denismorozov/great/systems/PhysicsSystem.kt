package com.denismorozov.great.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import com.denismorozov.great.components.BodyComponent
import com.denismorozov.great.components.TransformComponent


class PhysicsSystem(private val world: World) : IteratingSystem(
        Family.all(BodyComponent::class.java, TransformComponent::class.java).get()
) {
    companion object {
        private val MAX_STEP_TIME = 1 / 45f
        private var accumulator = 0f
    }

    private val bodiesQueue = Array<Entity>()
    private val bodyMapper = ComponentMapper.getFor(BodyComponent::class.java)
    private val transformMapper = ComponentMapper.getFor(TransformComponent::class.java)


    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        val frameTime = Math.min(deltaTime, 0.25f)
        accumulator += frameTime
        if (accumulator >= MAX_STEP_TIME) {
            world.step(MAX_STEP_TIME, 6, 2)
            accumulator -= MAX_STEP_TIME

            for (entity in bodiesQueue) {
                val transform = transformMapper.get(entity)
                val body = bodyMapper.get(entity).body
                transform.position.x = body.position.x
                transform.position.y = body.position.y
                transform.rotation = body.angle * MathUtils.radiansToDegrees
            }
        }

        bodiesQueue.clear()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        bodiesQueue.add(entity)
    }
}
