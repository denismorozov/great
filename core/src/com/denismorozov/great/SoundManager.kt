package com.denismorozov.great

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound

object SoundManager {
    private var music: Sound? = null
    private var sfx: Sound? = null
    private var masterVolume: Float = 1.0f

    fun playMusic(filePath: String, volume: Float) {
        music?.stop()
        music?.dispose()
        music = Gdx.audio.newSound(Gdx.files.internal(filePath))
        music?.loop(volume * masterVolume) // also accepts pitch, pan
    }

    fun playSoundEffect(filePath: String, volume: Float) {
        sfx?.stop()
        sfx?.dispose()
        sfx = Gdx.audio.newSound(Gdx.files.internal(filePath))
        sfx?.play(volume * masterVolume)
    }

    fun dispose() {
        music?.dispose()
        sfx?.dispose()
    }
}