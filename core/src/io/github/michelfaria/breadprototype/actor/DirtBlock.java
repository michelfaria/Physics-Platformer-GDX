package io.github.michelfaria.breadprototype.actor;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import io.github.michelfaria.breadprototype.TextureRegionNames;
import io.github.michelfaria.breadprototype.util.Util;

import static io.github.michelfaria.breadprototype.util.Util._n;

public class DirtBlock extends Block {
    public DirtBlock(World world, ParticleEffectPool blockCreationEffectPool, TextureAtlas atlas) {
        super(world, blockCreationEffectPool, Util._n(atlas.findRegion(TextureRegionNames.DIRT)));
    }
}
