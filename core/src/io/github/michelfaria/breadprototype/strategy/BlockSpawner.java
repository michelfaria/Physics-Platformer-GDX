package io.github.michelfaria.breadprototype.strategy;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.michelfaria.breadprototype.actor.Block;
import io.github.michelfaria.breadprototype.actor.BlockFactory;
import io.github.michelfaria.breadprototype.actor.DirtBlock;
import io.github.michelfaria.breadprototype.actor.TntBlock;

public class BlockSpawner {

    private final Stage stage;
    private final BlockFactory<DirtBlock> dirtBlockBlockFactory;
    private final BlockFactory<TntBlock> tntBlockBlockFactory;

    //temp
    public boolean spawnDirt = false;

    public BlockSpawner(Stage stage, BlockFactory<DirtBlock> dirtBlockBlockFactory, BlockFactory<TntBlock> tntBlockBlockFactory) {
        this.stage = stage;
        this.dirtBlockBlockFactory = dirtBlockBlockFactory;
        this.tntBlockBlockFactory = tntBlockBlockFactory;
    }

    public Block spawnBlock(float x, float y) {
        Block block;
        if (spawnDirt) {
            block = dirtBlockBlockFactory.make();
        } else {
            block = tntBlockBlockFactory.make();
        }
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
