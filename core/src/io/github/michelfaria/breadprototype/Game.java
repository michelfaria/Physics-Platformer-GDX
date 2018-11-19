package io.github.michelfaria.breadprototype;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.michelfaria.breadprototype.actor.*;
import io.github.michelfaria.breadprototype.actor.DirtBlock.DirtBlockFactory;
import io.github.michelfaria.breadprototype.actor.TntBlock.TntBlockFactory;
import io.github.michelfaria.breadprototype.fud.WorldSolidUD;
import io.github.michelfaria.breadprototype.logic.Positionable;
import io.github.michelfaria.breadprototype.strategy.*;
import io.github.michelfaria.breadprototype.util.Pair;
import io.github.michelfaria.breadprototype.util.TiledMapUtil;
import org.jetbrains.annotations.Nullable;

public class Game extends ApplicationAdapter {

    public static final float
            VRESX = 25,
            VRESY = 15,
            GRAVITY = -9.18f * 2,
            CAMERA_LERP = 0.1f;
    public static final int
            RAYS_NUM = 1000,
            BLUR_NUM = 2;

    public static float ptm(float pixels) {
        return pixels / 16;
    }

    private AssetManager assetManager;
    private FPSLogger fpsLogger;
    private SpriteBatch spriteBatch;
    private TextureAtlas textureAtlas;
    private ShapeRenderer shapeRenderer;
    private ParticlePools particlePools;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private TmxMapLoader tmxMapLoader;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap tiledMap;
    private Box2DDebugRenderer box2DDebugRenderer;
    private World world;
    private RayHandler rayHandler;
    private InputProcessor inputProcessor;

    private ExplosionEmitter.ExplosionEmitterFactory explosionEmitterFactory;
    private BlockFactory<DirtBlock> dirtBlockBlockFactory;
    private BlockFactory<TntBlock> tntBlockBlockFactory;
    private BlockSpawner blockSpawner;
    private WandProjectileSpawner wandProjectileSpawner;
    private Unprojector unprojector;
    private TodoListAppender todoListAppender;
    private ExplosionMaker explosionMaker;

    private Array<Runnable> todoList = new Array<>(Runnable.class);
    private @Nullable Positionable cameraTarget = null;
    private @Nullable Positionable player = null;

    @Override
    public void create() {
        super.create();
        // Assets
        assetManager = new AssetManager();
        assetManager.load(Assets.TEXTURE_ATLAS);
        assetManager.finishLoading();

        // Graphics
        fpsLogger = new FPSLogger();
        spriteBatch = new SpriteBatch();
        textureAtlas = assetManager.get(Assets.TEXTURE_ATLAS);
        shapeRenderer = new ShapeRenderer();
        particlePools = new ParticlePools(textureAtlas);
        camera = new OrthographicCamera();
        viewport = new FitViewport(VRESX, VRESY, camera);

        // Scene2d
        stage = new Stage(viewport);

        // Tiled
        tmxMapLoader = new TmxMapLoader();
        tiledMap = tmxMapLoader.load(Assets.TEST_MAP);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, ptm(1f), spriteBatch);

        // Box2d
        box2DDebugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0, GRAVITY), true);

        // Lights
        rayHandler = new RayHandler(world);
        rayHandler.setBlur(true);
        rayHandler.setBlurNum(BLUR_NUM);
        rayHandler.setAmbientLight(08888f);
        new PointLight(rayHandler, RAYS_NUM, new Color(1, 1, 1, 1), 20, 10, 15);
        new PointLight(rayHandler, RAYS_NUM, new Color(1, 1, 1, 1), 20, 41, 24);

        // Logic
        unprojector = new Unprojector(camera);
        todoListAppender = new TodoListAppender(todoList);

        // Spawners
        explosionEmitterFactory = new ExplosionEmitter.ExplosionEmitterFactory(particlePools.explosionSmokeParticlePool);
        explosionMaker = new ExplosionMaker();
        dirtBlockBlockFactory = new DirtBlockFactory(world, particlePools.blockCreationEffectPool, textureAtlas);
        tntBlockBlockFactory = new TntBlockFactory(world, particlePools.blockCreationEffectPool, textureAtlas, explosionMaker, explosionEmitterFactory);
        blockSpawner = new BlockSpawner(stage, dirtBlockBlockFactory, tntBlockBlockFactory);
        wandProjectileSpawner = new WandProjectileSpawner(stage, world, blockSpawner, particlePools.wandProjectileEffectPool);

        // Box2d collision handling
        world.setContactListener(new MyContactListener(blockSpawner, todoListAppender));

        initTiledBox2DIntegration();

        // Player
        final Player p = new Player(world, textureAtlas);
        p.init();
        p.setTransform(2f, 5f, 0f);
        stage.addActor(p);
        cameraTarget = p;
        player = p;

        // Input
        inputProcessor = new MyInputProcessor(blockSpawner, wandProjectileSpawner, unprojector, player);
        Gdx.input.setInputProcessor(inputProcessor);
    }

    private void initTiledBox2DIntegration() {
        for (MapLayer layer : tiledMap.getLayers()) {
            if (!isPhysicsLayer(layer)) {
                continue;
            }

            final BodyDef b = new BodyDef();
            final PolygonShape s = new PolygonShape();
            final FixtureDef f = new FixtureDef();

            for (RectangleMapObject o : layer.getObjects().getByType(RectangleMapObject.class)) {
                final Rectangle r = o.getRectangle();
                b.type = BodyDef.BodyType.StaticBody;
                b.position.x = ptm(r.x + r.width / 2);
                b.position.y = ptm(r.y + r.height / 2);
                s.setAsBox(ptm(r.width / 2), ptm(r.height / 2));
                f.shape = s;
                f.filter.categoryBits = Bits.BIT_SOLID;
                final Body body = world.createBody(b);
                body.createFixture(f).setUserData(new WorldSolidUD());
            }
            s.dispose();
        }
    }

    private boolean isPhysicsLayer(MapLayer layer) {
        return layer.getName().contains("[phys]");
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

    @Override
    public void render() {
        super.render();
        fpsLogger.log();
        update();
        clearScreen();
        renderTiledMap();
        spriteBatch.enableBlending();
        spriteBatch.begin();
        {
            drawStage();
        }
        spriteBatch.end();
        renderLighting();
        // renderDebugThings();
        // renderBox2DDebug();
    }

    private void update() {
        runTodos();
        world.step(1 / 60f, 6, 2);
        stage.act();
        updateCamera();
    }

    private void runTodos() {
        for (Runnable runnable : todoList) {
            runnable.run();
        }
        todoList.clear();
    }

    private void updateCamera() {
        if (cameraTarget != null) {
            camera.position.x = MathUtils.lerp(camera.position.x, cameraTarget.getX() + cameraTarget.getWidth() / 2, CAMERA_LERP);
            camera.position.y = MathUtils.lerp(camera.position.y, cameraTarget.getY() + cameraTarget.getHeight() / 2, CAMERA_LERP);
        }

        final int mapWidth = TiledMapUtil.mapPixelWidth(tiledMap);
        final int mapHeight = TiledMapUtil.mapPixelHeight(tiledMap);
        final float cameraHalfWidth = camera.viewportWidth / 2;
        final float cameraHalfHeight = camera.viewportHeight / 2;

        camera.position.x = MathUtils.clamp(camera.position.x, cameraHalfWidth, mapWidth - cameraHalfWidth);
        camera.position.y = MathUtils.clamp(camera.position.y, cameraHalfHeight, mapHeight - cameraHalfHeight);
        camera.update();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0.258f, 0.541f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void renderTiledMap() {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    private void drawStage() {
        spriteBatch.setProjectionMatrix(camera.combined);
        stage.draw();
    }

    private void renderLighting() {
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
    }

    private void renderDebugThings() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        for (Actor a : stage.getActors()) {
            shapeRenderer.setColor(Color.RED);

            // Render dot at Actor x,y position
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(a.getX(), a.getY(), 0.05f, 10);

            // Render a rectangle around the bounds of the Actor
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            shapeRenderer.rect(a.getX(), a.getY(),
                    a.getOriginX(), a.getOriginY(),
                    a.getWidth(), a.getHeight(),
                    a.getScaleX(), a.getScaleY(),
                    a.getRotation());

            if (a instanceof Player) {
                // Render Player's kick area
                shapeRenderer.setColor(Color.GREEN);
                shapeRenderer.set(ShapeRenderer.ShapeType.Line);
                final Pair<Vector2> kickArea = ((Player) a).getKickArea();
                shapeRenderer.rect(kickArea.a.x, kickArea.a.y, kickArea.b.x - kickArea.a.x, kickArea.b.y - kickArea.a.y);
            }
        }
        shapeRenderer.end();
    }

    private void renderBox2DDebug() {
        box2DDebugRenderer.render(world, camera.combined);
    }

    @Override
    public void dispose() {
        super.dispose();
        assetManager.dispose();
        spriteBatch.dispose();
        textureAtlas.dispose();
        shapeRenderer.dispose();
        stage.dispose();
        mapRenderer.dispose();
        tiledMap.dispose();
        box2DDebugRenderer.dispose();
        rayHandler.dispose();
        world.dispose();
    }
}
