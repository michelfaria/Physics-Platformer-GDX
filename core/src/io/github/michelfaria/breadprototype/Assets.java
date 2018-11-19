package io.github.michelfaria.breadprototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.michelfaria.breadprototype.util.Util;

import static io.github.michelfaria.breadprototype.util.Util._n;

public class Assets {

    public static final AssetDescriptor<TextureAtlas>
            TEXTURE_ATLAS = new AssetDescriptor<>("default.atlas", TextureAtlas.class);

    public static final FileHandle
            EFFECT_BLOCK_CREATE = _n(Gdx.files.internal("effects/block-create.p")),
            EFFECT_WAND_PARTICLE = _n(Gdx.files.internal("effects/wand-projectile.p")),
            EFFECT_EXPLOSION_SMOKE = _n(Gdx.files.internal("effects/explosion-smoke.p")),
            EFFECT_NOVA = _n(Gdx.files.internal("effects/force-nova.p"));

    public static final String
            TEST_MAP = "maps/test.tmx";
}
