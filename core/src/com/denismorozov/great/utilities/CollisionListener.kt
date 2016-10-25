package com.denismorozov.great.utilities

import com.badlogic.gdx.physics.box2d.Fixture

interface CollisionListener {
    fun onBeginContact(fixtureA: Fixture, fixtureB: Fixture)
}
