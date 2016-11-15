package com.denismorozov.great.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.denismorozov.great.components.BodyComponent
import com.denismorozov.great.components.PlayerComponent
import com.denismorozov.great.components.TextureComponent
import com.denismorozov.great.components.TransformComponent
import com.denismorozov.great.screens.GameScreen

fun createPlayer(engine: PooledEngine, world: World, playerTexture: Texture): Entity {
    val player = engine.createEntity()

    player
        .add(PlayerComponent())
        .add(TextureComponent(playerTexture))
        .add(TransformComponent())

    val bodyDef = BodyDef()
    bodyDef.type = BodyDef.BodyType.DynamicBody
    val center = Vector3(GameScreen.worldWidth /2f, GameScreen.worldHeight /2f, 0f)
    bodyDef.position.set(center.x, center.y)
    val body = world.createBody(bodyDef)
    val circle = CircleShape()
    circle.radius = 0.5f
    val fixtureDef = FixtureDef()
    fixtureDef.shape = circle
    fixtureDef.density = 1f
    fixtureDef.restitution = 0.2f
    body.createFixture(fixtureDef)
    body.userData = player
    circle.dispose()

    player.add(BodyComponent(body))

    return player
}