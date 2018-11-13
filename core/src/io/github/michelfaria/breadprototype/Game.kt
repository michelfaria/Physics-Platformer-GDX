package io.github.michelfaria.breadprototype

import box2dLight.PointLight
import box2dLight.RayHandler
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.FPSLogger
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils.clamp
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.michelfaria.breadprototype.Bits.BIT_SOLID
import io.github.michelfaria.breadprototype.actor.Block
import io.github.michelfaria.breadprototype.actor.Player
import io.github.michelfaria.breadprototype.fud.WorldSolidFUD
import io.github.michelfaria.breadprototype.logic.Positionable
import io.github.michelfaria.breadprototype.strategy.BlockSpawner
import io.github.michelfaria.breadprototype.strategy.Unprojector
import io.github.michelfaria.breadprototype.util.TiledMapUtil.mapPixelHeight
import io.github.michelfaria.breadprototype.util.TiledMapUtil.mapPixelWidth

class Game : ApplicationAdapter() {

    companion object {
        const val VRESX = 25
        const val VRESY = 15
        const val GRAVITY = -9.18f
        const val RAYS_NUM = 1000
        const val BLUR_NUM = 2

        fun ptm(pixels: Float): Float {
            return pixels / 16
        }
    }

    private lateinit var assetManager: AssetManager
    private lateinit var fpsLogger: FPSLogger
    private lateinit var batch: SpriteBatch
    private lateinit var textureAtlas: TextureAtlas
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport
    private lateinit var stage: Stage
    private lateinit var tmxMapLoader: TmxMapLoader
    private lateinit var tiledMapRenderer: OrthogonalTiledMapRenderer
    private lateinit var tiledMap: TiledMap
    private lateinit var box2DDebugRenderer: Box2DDebugRenderer
    private lateinit var world: World
    private lateinit var rayHandler: RayHandler
    private lateinit var inputProcessor: InputProcessor

    private lateinit var blockFactory: Block.Factory
    private lateinit var blockSpawner: BlockSpawner
    private lateinit var unprojector: Unprojector

    private var cameraTarget: Positionable? = null

    override fun create() {
        initAssets()
        initGraphics()
        initScene2D()
        unprojector = Unprojector(camera)
        initTiled()
        initBox2D()
        initBlockManagement()
        initBox2DLights()
        initTiledBox2DIntegration()
        initInputProcessor()
        makePlayer()
    }

    private fun initAssets() {
        assetManager = AssetManager()
        assetManager.load(Assets.TEXTURE_ATLAS)
        assetManager.finishLoading()
    }

    private fun initGraphics() {
        fpsLogger = FPSLogger()
        batch = SpriteBatch()
        textureAtlas = assetManager.get(Assets.TEXTURE_ATLAS)
        shapeRenderer = ShapeRenderer()
    }

    private fun initScene2D() {
        camera = OrthographicCamera()
        viewport = FitViewport(VRESX.toFloat(), VRESY.toFloat(), camera)
        stage = Stage(viewport)
    }

    private fun initTiled() {
        tmxMapLoader = TmxMapLoader()
        tiledMap = tmxMapLoader.load(Assets.TEST_MAP)
        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap, ptm(1f), batch)
    }

    private fun initBox2D() {
        box2DDebugRenderer = Box2DDebugRenderer()
        world = World(Vector2(0f, GRAVITY), true)
        world.setContactListener(MyContactListener())
    }

    private fun initBlockManagement() {
        blockFactory = Block.Factory(world, textureAtlas)
        blockSpawner = BlockSpawner(stage, blockFactory)
    }

    private fun initBox2DLights() {
        rayHandler = RayHandler(world).apply {
            setBlur(true)
            setBlurNum(BLUR_NUM)
            setAmbientLight(08888f)
        }
        PointLight(rayHandler, RAYS_NUM, Color(1f, 1f, 1f, 1f), 20f, 10f, 15f)
        PointLight(rayHandler, RAYS_NUM, Color(1f, 1f, 1f, 1f), 20f, 41f, 24f)
    }

    private fun initTiledBox2DIntegration() {
        for (layer in tiledMap.layers) {
            if (!isPhysicsLayer(layer)) {
                continue
            }
            val bodyDef = BodyDef()
            val shape = PolygonShape()
            val fixtureDef = FixtureDef()
            for (o in layer.objects.getByType(RectangleMapObject::class.java)) {
                val rectangle = o.rectangle
                bodyDef.apply {
                    type = BodyDef.BodyType.StaticBody
                    position.x = ptm(rectangle.x + rectangle.width / 2)
                    position.y = ptm(rectangle.y + rectangle.height / 2)
                }
                shape.setAsBox(ptm(rectangle.width / 2), ptm(rectangle.height / 2))
                fixtureDef.apply {
                    this.shape = shape
                    filter.categoryBits = BIT_SOLID
                }
                val body = world.createBody(bodyDef)
                body.createFixture(fixtureDef).apply {
                    userData = WorldSolidFUD()
                }
            }
            shape.dispose()
        }
    }

    private fun initInputProcessor() {
        inputProcessor = MyInputProcessor(blockSpawner, unprojector)
        Gdx.input.inputProcessor = inputProcessor
    }

    private fun isPhysicsLayer(layer: MapLayer): Boolean {
        return layer.name.contains("[phys]")
    }

    private fun makePlayer() {
        val player = Player(world, textureAtlas)
        player.body.setTransform(2f, 5f, 0f)
        stage.addActor(player)
        cameraTarget = player
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
    }

    override fun render() {
        fpsLogger.log()
        update()
        clearScreen()
        renderTiledMap()
        batch.enableBlending()
        batch.begin()
        run {
            drawStage()
        }
        batch.end()
        renderLighting()
        renderActorsDotsDebug()
        // renderBox2dDebug()
    }

    private fun update() {
        world.step(1 / 60f, 6, 2)
        stage.act()
        updateCamera()
    }

    private fun updateCamera() {
        cameraTarget?.let {
            with(camera) {
                position.x = it.getX() + it.getWidth() / 2;
                position.y = it.getY() + it.getHeight() / 2;
            }

            val mapWidth = mapPixelWidth(tiledMap);
            val mapHeight = mapPixelHeight(tiledMap);
            val cameraHalfWidth = camera.viewportWidth / 2;
            val cameraHalfHeight = camera.viewportHeight / 2;

            with(camera) {
                position.x = clamp(position.x, cameraHalfWidth, mapWidth - cameraHalfWidth);
                position.y = clamp(position.y, cameraHalfHeight, mapHeight - cameraHalfHeight);
            }
        }
        camera.update()
    }

    private fun clearScreen() {
        Gdx.gl.glClearColor(0.258f, 0.541f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    private fun renderTiledMap() {
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()
    }

    private fun drawStage() {
        batch.projectionMatrix = camera.combined
        stage.draw()
    }

    private fun renderLighting() {
        rayHandler.setCombinedMatrix(camera)
        rayHandler.updateAndRender()
    }

    private fun renderActorsDotsDebug() {
        shapeRenderer.apply {
            projectionMatrix = camera.combined
            setAutoShapeType(true)
            begin()
            color = Color.RED
            stage.actors.forEach {
                set(ShapeRenderer.ShapeType.Filled)
                circle(it.x, it.y, 0.05f, 10)
                set(ShapeRenderer.ShapeType.Line)
                rect(it.x, it.y, it.originX, it.originY, it.width, it.height, it.scaleX, it.scaleY, it.rotation)
            }
            end()
        }
    }

    private fun renderBox2dDebug() {
        box2DDebugRenderer.render(world, camera.combined)
    }

    override fun dispose() {
        assetManager.dispose()
        batch.dispose()
        textureAtlas.dispose()
        shapeRenderer.dispose()
        stage.dispose()
        tiledMapRenderer.dispose()
        tiledMap.dispose()
        box2DDebugRenderer.dispose()
        rayHandler.dispose()
        world.dispose()
    }
}
