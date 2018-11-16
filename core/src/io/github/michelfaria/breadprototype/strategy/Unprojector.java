package io.github.michelfaria.breadprototype.strategy;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class Unprojector {

    private final Camera camera;

    public Unprojector(Camera camera) {
        this.camera = camera;
    }

    public Vector3 unproject(Vector3 screenCoords, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
        return camera.unproject(screenCoords, viewportX, viewportY, viewportWidth, viewportHeight);
    }

    public Vector3 unproject(Vector3 screenCoords) {
        return camera.unproject(screenCoords);
    }
}
