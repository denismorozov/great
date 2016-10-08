package com.denismorozov.great.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.denismorozov.great.components.PositionComponent
import com.denismorozov.great.components.VelocityComponent
import com.denismorozov.great.input.Joystick

class MovementSystem : IteratingSystem(
        Family.all(PositionComponent::class.java, VelocityComponent::class.java).get()
) {
    private val pm = ComponentMapper.getFor(PositionComponent::class.java)
    private val vm = ComponentMapper.getFor(VelocityComponent::class.java)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = pm.get(entity)
        val velocity = vm.get(entity)
        position.x += velocity.velocity * deltaTime * Joystick.touchpad.knobPercentX
        position.y += velocity.velocity * deltaTime * Joystick.touchpad.knobPercentY
    }
}