package com.denismorozov.great.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.denismorozov.great.components.BodyComponent
import com.denismorozov.great.components.EnemyComponent
import com.denismorozov.great.components.PlayerComponent

class EnemyPathfinding() : IteratingSystem(
        Family.all(EnemyComponent::class.java).get()
) {
    private val bodiesQueue = Array<Entity>()
    private val bodyM = ComponentMapper.getFor(BodyComponent::class.java)


    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        val family = Family.all(PlayerComponent::class.java).get()
        val player = engine.getEntitiesFor(family).first()
        val playerBody = bodyM.get(player).body

        for (entity in bodiesQueue) {
            val body = bodyM.get(entity).body
            val diffX = playerBody.position.x - body.position.x
            val diffY = playerBody.position.y - body.position.y
            body.linearVelocity = Vector2(diffX, diffY).scl(0.5f)
        }

        bodiesQueue.clear()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        bodiesQueue.add(entity)
    }
}
