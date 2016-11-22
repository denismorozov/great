package com.denismorozov.great.input

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.denismorozov.great.components.*

class Touch (val camera: OrthographicCamera, val engine: PooledEngine, val world: World) : InputProcessor {
    private val spellTexture: Texture
    private val bodyM = ComponentMapper.getFor(BodyComponent::class.java)

    init {
        spellTexture = Texture(Gdx.files.internal("spell.png"))
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val gameCoordinates = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        val family = Family.all(PlayerComponent::class.java).get()
        val player = engine.getEntitiesFor(family).firstOrNull()
        if (player !== null) {
            engine.addEntity(createSpell(gameCoordinates, player))
        }
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    private fun createSpell(gameCoordinates: Vector3, player: Entity): Entity {
        val spell = engine.createEntity()

        val playerBody = bodyM.get(player).body

        spell
            .add(SpellComponent())
            .add(TextureComponent(spellTexture))
            .add(TransformComponent())

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
        body.userData = spell
        circle.dispose()
        body.linearVelocity = Vector2(gameCoordinates.x - playerBody.position.x, gameCoordinates.y - playerBody.position.y)
        spell.add(BodyComponent(body))

        return spell
    }

    override fun keyDown(keycode: Int): Boolean = false
    override fun keyTyped(character: Char): Boolean = false
    override fun keyUp(keycode: Int): Boolean = false
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false
    override fun scrolled(amount: Int): Boolean = false // hmm...?
}