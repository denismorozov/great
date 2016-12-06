package com.denismorozov.great.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import com.denismorozov.great.components.*

class SpellSystem(private val world: World) : IteratingSystem(
        Family.all(SpellComponent::class.java, PlayerComponent::class.java).get()
) {
    private val spellTexture: Texture
    private val bodyM = ComponentMapper.getFor(BodyComponent::class.java)

    private val bodiesQueue = Array<Entity>()

    private val spellM = ComponentMapper.getFor(SpellComponent::class.java)
    private val playerM = ComponentMapper.getFor(PlayerComponent::class.java)

    init {
        spellTexture = Texture(Gdx.files.internal("spell.png"))
    }

    enum class Spell { BALL, BLINK }
    var selectedSpell: Spell = Spell.BALL

    override fun update (deltaTime: Float) {
        super.update(deltaTime)

//        val family = Family.all(PlayerComponent::class.java).get()
//        val player = engine.getEntitiesFor(family).firstOrNull()
//
//        if (player !== null) {
//            val playerBody = bodyM.get(player).body
//            for (entity in bodiesQueue) {
//                val body = bodyM.get(entity).body
//                val diffX = playerBody.position.x - body.position.x
//                val diffY = playerBody.position.y - body.position.y
//                body.linearVelocity = Vector2(diffX, diffY).scl(0.5f)
//            }
//        }
//
//        bodiesQueue.clear()
    }

    override fun processEntity (entity: Entity, deltaTime: Float) {
//        bodiesQueue.add(entity)
    }

    fun touchDown (gameCoordinates: Vector3, player: Entity) {
        val spell = if (selectedSpell === Spell.BALL) {
            createBall(gameCoordinates, player)
        } else if (selectedSpell === Spell.BLINK){
            teleport(gameCoordinates, player)
        } else {
            null
        }
        if (spell !== null) {
            engine.addEntity(spell)
        }
    }

    private fun createSpell (): Entity {
        val spell = (engine as PooledEngine).createEntity()
        spell
            .add(SpellComponent())
            .add(TransformComponent())
        return spell
    }

    private fun createBall (gameCoordinates: Vector3, player: Entity): Entity {
        val spell = createSpell()

        val playerBody = bodyM.get(player).body

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(playerBody.position.x, playerBody.position.y)
        val body = world.createBody(bodyDef)

        val circle = CircleShape()
        circle.radius = 0.5f
        val fixtureDef = FixtureDef()
        fixtureDef.shape = circle
        fixtureDef.density = 1f
        fixtureDef.restitution = 0.2f
        fixtureDef.isSensor = true
        body.createFixture(fixtureDef)
        circle.dispose()

        body.userData = spell // todo: why is this done again

        // todo: perhaps make a min velocity
        body.linearVelocity = Vector2(gameCoordinates.x - playerBody.position.x, gameCoordinates.y - playerBody.position.y)

        spell.add(BodyComponent(body))
        spell.add(TextureComponent(spellTexture))

        return spell
    }

    private fun teleport (gameCoordinates: Vector3, player: Entity): Entity {
        val spell = createSpell()
        return spell
    }
}
