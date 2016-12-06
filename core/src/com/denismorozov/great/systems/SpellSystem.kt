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
import com.denismorozov.great.components.*
import java.util.*

class SpellSystem(private val world: World) : IteratingSystem(
        Family.all(SpellComponent::class.java, PlayerComponent::class.java).get()
) {
    private val spellTexture = Texture(Gdx.files.internal("spell.png"))
    private val bodyM = ComponentMapper.getFor(BodyComponent::class.java)

    private val spellM = ComponentMapper.getFor(SpellComponent::class.java)
    private val playerM = ComponentMapper.getFor(PlayerComponent::class.java)

    private val timer = Timer()

    val maxBallCharges = 3
    val maxBlinkCharges = 1

    val ballRechargeRate = 1 // per second

    enum class Spell { BALL, BLINK }
    var selectedSpell: Spell = Spell.BALL

    var ballCharges = maxBallCharges
    var blinkCharges = maxBlinkCharges

    var ballRecharging = false


    override fun update (deltaTime: Float) {
        super.update(deltaTime)
    }

    override fun processEntity (entity: Entity, deltaTime: Float) {
    }

    fun touchDown (gameCoordinates: Vector3, player: Entity) {
        val spell = if (selectedSpell === Spell.BALL && ballCharges > 0) {
            createBall(gameCoordinates, player)
        } else if (selectedSpell === Spell.BLINK){
            blink(gameCoordinates, player) // side effect
            null
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
        ballCharges--

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

        if (!ballRecharging) {
            rechargeBalls()
        }

        return spell
    }

    private fun rechargeBalls () {
        ballRecharging = true
        timer.schedule(object : TimerTask() {
            override fun run() {
                ballCharges++
                if (ballCharges < maxBallCharges) {
                    rechargeBalls()
                } else {
                    ballRecharging = false
                }
            }
        }, (1000 * ballRechargeRate).toLong())
    }

    private fun blink (gameCoordinates: Vector3, player: Entity) {
        blinkCharges--

        val playerBody = bodyM.get(player).body

        val movementSystem = engine.getSystem(MovementSystem::class.java)
        movementSystem.setProcessing(false)

//        Gdx.app.log("excuse me", gameCoordinates.toString())
        Gdx.app.log("tp", playerBody.position.toString())

        playerBody.position.set(gameCoordinates.x, gameCoordinates.y)

        Gdx.app.log("tp after", playerBody.position.toString())

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                blinkCharges++
            }
        }, 5000)
    }
}
