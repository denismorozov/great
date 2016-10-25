package com.denismorozov.great.utilities

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.*
import com.denismorozov.great.components.EnemyComponent
import com.denismorozov.great.components.PlayerComponent
import com.denismorozov.great.components.SpellComponent

class CollisionSystem (val engine: Engine, val collisionListeners: List<CollisionListener>) : ContactListener {
    private val spellM = ComponentMapper.getFor(SpellComponent::class.java)
    private val playerM = ComponentMapper.getFor(PlayerComponent::class.java)
    private val enemyM = ComponentMapper.getFor(EnemyComponent::class.java)

    override fun beginContact(contact: Contact) {
        for (listener in collisionListeners) {
            listener.onBeginContact(contact.fixtureA, contact.fixtureB)
        }

        val fixtures = listOf(contact.fixtureA, contact.fixtureB)
        val entities = fixtures.map { it.body.userData as Entity }
        val spell = entities.filter { spellM.get(it) !== null }.firstOrNull()
        val player = entities.filter { playerM.get(it) !== null }.firstOrNull()
        val enemy = entities.filter { enemyM.get(it) !== null }.firstOrNull()

        if (enemy !== null) {
            Gdx.app.log("spell system", "enemy collision")
            if (spell !== null) {
                Gdx.app.log("spell system", "spell collision")
                engine.removeEntity(enemy)
            }
        }

        if (player !== null) {
            Gdx.app.log("spell system", "player collision")
        }
    }

    override fun endContact(contact: Contact?) {

    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {

    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {

    }
}