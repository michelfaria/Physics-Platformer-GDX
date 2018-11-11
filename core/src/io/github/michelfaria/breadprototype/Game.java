package io.github.michelfaria.breadprototype;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.michelfaria.breadprototype.fud.WorldSolidFUD;

import static io.github.michelfaria.breadprototype.Bits.BIT_ENTITY;
import static io.github.michelfaria.breadprototype.Bits.BIT_WORLD;

public class Game extends ApplicationAdapter {
    public static final int   VRESX   = 15;
    public static final int   VRESY   = 10;
    public static final float GRAVITY = -9.18f;

    private SpriteBatch                batch;
    private TextureAtlas               textureAtlas;
    private OrthographicCamera         camera;
    private Viewport                   viewport;
    private Stage                      stage;
    private TmxMapLoader               tmxMapLoader;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private TiledMap                   tiledMap;
    private Box2DDebugRenderer         box2DDebugRenderer;
    private World                      world;

    @Override
    public void create() {
        initGraphics();
        initScene2D();
        initTiled();
        initBox2D();
        initTiledBox2DIntegration();
        makePlayer();
    }

    private void initGraphics() {
        batch = new SpriteBatch();
        textureAtlas = new TextureAtlas("default.atlas");
    }

    private void initScene2D() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(VRESX, VRESY, camera);
        stage = new Stage(viewport);
    }

    private void initTiled() {
        tmxMapLoader = new TmxMapLoader();
        tiledMap = tmxMapLoader.load("maps/test.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, ptm(1), batch);
    }

    private void initBox2D() {
        box2DDebugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0, GRAVITY), true);
        world.setContactListener(new MyContactListener());
    }

    private void initTiledBox2DIntegration() {
        for (MapLayer layer : tiledMap.getLayers()) {
            if (!isPhysicsLayer(layer)) {
                continue;
            }

            final BodyDef      bdef  = new BodyDef();
            final PolygonShape shape = new PolygonShape();
            final FixtureDef   fdef  = new FixtureDef();

            for (RectangleMapObject o : layer.getObjects().getByType(RectangleMapObject.class)) {
                final Rectangle rectangle = o.getRectangle();

                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.x = ptm(rectangle.x + rectangle.width / 2);
                bdef.position.y = ptm(rectangle.y + rectangle.height / 2);
                shape.setAsBox(ptm(rectangle.width / 2), ptm(rectangle.height / 2));
                fdef.shape = shape;
                fdef.filter.categoryBits = BIT_WORLD;
                fdef.filter.maskBits = BIT_ENTITY;

                final Body body = world.createBody(bdef);
                body.createFixture(fdef)
                    .setUserData(new WorldSolidFUD());
            }
            shape.dispose();
        }
    }

    private boolean isPhysicsLayer(MapLayer layer) {
        return layer.getName().contains("[phys]");
    }

    private void makePlayer() {
        final Player player = new Player(textureAtlas, world);
        player.getBody().setTransform(2, 5, 0);
        stage.addActor(player);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

    @Override
    public void render() {
        update();
        clearScreen();
        renderTiledMap();
        batch.begin();
        {
            drawStage();
        }
        batch.end();
        renderDebug();
    }

    private void update() {
        world.step(1 / 60f, 6, 2);
        stage.act();
        camera.update();
    }

    protected void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    protected void renderTiledMap() {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
    }

    protected void drawStage() {
        batch.setProjectionMatrix(camera.combined);
        stage.draw();
    }

    protected void renderDebug() {
        box2DDebugRenderer.render(world, camera.combined);
    }

    @Override
    public void dispose() {
        batch.dispose();
        textureAtlas.dispose();
        stage.dispose();
        tiledMapRenderer.dispose();
        tiledMap.dispose();
        box2DDebugRenderer.dispose();
        world.dispose();
    }

    public static float ptm(float pixels) {
        return pixels / 16;
    }
}
