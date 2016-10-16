package com.denismorozov.great.systems


import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.denismorozov.great.components.*
import com.denismorozov.great.input.Joystick

class MovementSystem(val camera: Camera) : IteratingSystem(
        Family.all(PlayerComponent::class.java).get()
) {
    private val bodyM = ComponentMapper.getFor(BodyComponent::class.java)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = bodyM.get(entity).body
        val currentVelocity = body.linearVelocity

        val maxVelocity = 1.25f
        val desiredVelocityX = maxVelocity * Joystick.X
        val desiredVelocityY = maxVelocity * Joystick.Y
        val velocityChangeX = desiredVelocityX - currentVelocity.x
        val velocityChangeY = desiredVelocityY - currentVelocity.y
        val impulseX = body.mass * velocityChangeX
        val impulseY = body.mass * velocityChangeY

        body.applyLinearImpulse(Vector2(impulseX, impulseY), body.worldCenter, true)
        camera.position.x += (body.position.x - camera.position.x) * 1.5f * deltaTime
        camera.position.y += (body.position.y - camera.position.y) * 1.5f * deltaTime
    }
}