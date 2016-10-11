package com.denismorozov.great.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.denismorozov.great.components.TextureComponent
import com.denismorozov.great.components.TransformComponent
import java.util.*


class RenderingSystem(private val batch: SpriteBatch) : IteratingSystem(
        Family.all(TransformComponent::class.java, TextureComponent::class.java).get()
//        ZComparator()
) {
//    class ZComparator : Comparator<Entity> {
//        private val transformM: ComponentMapper<TransformComponent>
//
//        init {
//            transformM = ComponentMapper.getFor(TransformComponent::class.java)
//        }
//
//        override fun compare(left: Entity, right: Entity): Int {
//            return Math.signum(transformM.get(right).position.z - transformM.get(left).position.z).toInt()
//        }
//    }

    companion object {
        internal val PPM = 1f
        internal val FRUSTUM_WIDTH = Gdx.graphics.width / PPM
        internal val FRUSTUM_HEIGHT = Gdx.graphics.height / PPM

        val PIXELS_TO_METRES = 1.0f / PPM

        private val meterDimensions = Vector2()
        private val pixelDimensions = Vector2()
        val screenSizeInMeters: Vector2
            get() {
                meterDimensions.set(Gdx.graphics.width * PIXELS_TO_METRES,
                        Gdx.graphics.height * PIXELS_TO_METRES)
                return meterDimensions
            }

        val screenSizeInPixesl: Vector2
            get() {
                pixelDimensions.set(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
                return pixelDimensions
            }

        fun PixelsToMeters(pixelValue: Float): Float {
            return pixelValue * PIXELS_TO_METRES
        }
    }

    private val renderQueue = Array<Entity>()
//    private val comparator: Comparator<Entity>? = null

    private val textureM = ComponentMapper.getFor(TextureComponent::class.java)
    private val transformM = ComponentMapper.getFor(TransformComponent::class.java)


    override fun update(deltaTime: Float) {
        super.update(deltaTime)

//        renderQueue.sort(comparator)
        batch.begin()

        for (entity in renderQueue) {
            val texture = textureM.get(entity)
            val transform = transformM.get(entity)

            if (transform.isHidden) {
                continue
            }

            val width = texture.region.regionWidth.toFloat()
            val height = texture.region.regionHeight.toFloat()

            val originX = width / 2f
            val originY = height / 2f

            batch.draw(
                texture.region,
                transform.position.x - originX,
                transform.position.y - originY,
                originX,
                originY,
                width,
                height,
                PixelsToMeters(transform.scale.x),
                PixelsToMeters(transform.scale.y),
                transform.rotation
            )
        }

        batch.end()
        renderQueue.clear()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        renderQueue.add(entity)
    }
}
