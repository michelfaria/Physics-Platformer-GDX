package io.github.michelfaria.breadprototype

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.g2d.TextureAtlas

class Assets {
    companion object {
        val TEXTURE_ATLAS = AssetDescriptor<TextureAtlas>("default.atlas", TextureAtlas::class.java)
        const val TEST_MAP = "maps/test.tmx"
    }
}