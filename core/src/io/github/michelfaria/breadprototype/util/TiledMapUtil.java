package io.github.michelfaria.breadprototype.util;

import com.badlogic.gdx.maps.tiled.TiledMap;

public class TiledMapUtil {
    public static int mapWidth(TiledMap map) {
        return map.getProperties().get("width", Integer.class);
    }

    public static int mapHeight(TiledMap map) {
        return map.getProperties().get("height", Integer.class);
    }

    public static int tilePixelWidth(TiledMap map) {
        return map.getProperties().get("tilewidth", Integer.class);
    }

    public static int tilePixelHeight(TiledMap map) {
        return map.getProperties().get("tileheight", Integer.class);
    }

    public static int mapPixelWidth(TiledMap map) {
        return mapWidth(map) * tilePixelWidth(map);
    }

    public static int mapPixelHeight(TiledMap map) {
        return mapHeight(map) * tilePixelHeight(map);
    }
}
