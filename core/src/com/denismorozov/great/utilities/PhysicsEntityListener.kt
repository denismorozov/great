package com.denismorozov.great.utilities

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.gdx.physics.box2d.World
import com.denismorozov.great.components.BodyComponent

class PhysicsEntityListener(val world: World) : EntityListener {
    private val bodyM = ComponentMapper.getFor(BodyComponent::class.java)

    override fun entityRemoved(entity: Entity) {
        val body = bodyM.get(entity).body
        world.destroyBody(body)
    }

    override fun entityAdded(entity: Entity) {

    }
}