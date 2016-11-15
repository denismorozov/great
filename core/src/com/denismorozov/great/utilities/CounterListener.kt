package com.denismorozov.great.utilities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener


class CounterListener : EntityListener {
    var entityCount = 0
    override fun entityAdded(entity: Entity?) {
        entityCount++
    }
    override fun entityRemoved(entity: Entity?) {
        entityCount--
    }
    fun reset() {
        entityCount = 0
    }
}