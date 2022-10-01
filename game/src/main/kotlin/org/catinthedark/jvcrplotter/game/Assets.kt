package org.catinthedark.jvcrplotter.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.DEFAULT_CHARS
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader

object Assets {
    fun load(): AssetManager {
        return AssetManager().apply {
            Names.textures.forEach { load(it, Texture::class.java) }
        }
    }

    object Names {
        val LOGO = "textures/title.png"
        val TITLE = "textures/title.png"

        val textures = listOf(
            LOGO,
            TITLE,
        )
    }
}

inline fun <reified T> AssetManager.at(name: String): T {
    return get(name, T::class.java)
}
