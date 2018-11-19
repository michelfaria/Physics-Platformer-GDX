package io.github.michelfaria.breadprototype.actor;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import io.github.michelfaria.breadprototype.TextureRegionNames;
import io.github.michelfaria.breadprototype.util.Util;

import static io.github.michelfaria.breadprototype.util.Util._n;

public class DirtBlock extends Block {
    public DirtBlock(World world, ParticleEffectPool blockCreationEffectPool, TextureAtlas atlas) {
        super(world, blockCreationEffectPool, _n(atlas.findRegion(TextureRegionNames.DIRT)));
    }

    public static class DirtBlockFactory implements BlockFactory<DirtBlock> {
        private final World world;
        private final ParticleEffectPool blockCreationEffectPool;
        private final TextureAtlas textureAtlas;

        public DirtBlockFactory(World world, ParticleEffectPool blockCreationEffectPool, TextureAtlas textureAtlas) {
            this.world = world;
            this.blockCreationEffectPool = blockCreationEffectPool;
            this.textureAtlas = textureAtlas;
        }

        public DirtBlock make() {
            return new DirtBlock(world, blockCreationEffectPool, textureAtlas);
        }
    }
}
