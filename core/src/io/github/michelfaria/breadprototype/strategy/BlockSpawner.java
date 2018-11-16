package io.github.michelfaria.breadprototype.strategy;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.michelfaria.breadprototype.actor.Block;
import io.github.michelfaria.breadprototype.actor.DirtBlock;

public class BlockSpawner {

    private final Stage stage;
    private final World world;
    private final TextureAtlas atlas;
    private final ParticleEffectPool blockCreationEffectPool;

    public BlockSpawner(Stage stage, World world, TextureAtlas atlas, ParticleEffectPool blockCreationEffectPool) {
        this.stage = stage;
        this.world = world;
        this.atlas = atlas;
        this.blockCreationEffectPool = blockCreationEffectPool;
    }

    public Block spawnBlock(float x, float y) {
        final Block block = new DirtBlock(world, blockCreationEffectPool, atlas);
        block.init();
        stage.addActor(block);
        block.setPosition(x, y);
        block.addNewCreationEffect();
        return block;
    }

    public void removeBlock(float x, float y) {
        final Actor hit = stage.hit(x, y, true);
        if (hit != null) {
            if (hit instanceof Block) {
                ((Block) hit).dispose();
            }
        }
    }
}
