package io.github.michelfaria.breadprototype.util

import com.badlogic.gdx.maps.tiled.TiledMap

object TiledMapUtil {

    fun mapWidth(map: TiledMap): Int {
        return map.properties.get("width", Int::class.java)
    }

    fun mapHeight(map: TiledMap): Int {
        return map.properties.get("height", Int::class.java)
    }

    fun tilePixelWidth(map: TiledMap): Int {
        return map.properties.get("tilewidth", Int::class.java)
    }

    fun tilePixelHeight(map: TiledMap): Int {
        return map.properties.get("tileheight", Int::class.java)
    }

    fun mapPixelWidth(map: TiledMap): Int {
        return mapWidth(map) * tilePixelWidth(map)
    }

    fun mapPixelHeight(map: TiledMap): Int {
        return mapHeight(map) * tilePixelHeight(map)
    }
}