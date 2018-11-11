package io.github.michelfaria.breadprototype

import box2dLight.PointLight
import box2dLight.RayHandler
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.michelfaria.breadprototype.Bits.BIT_SOLID
import io.github.michelfaria.breadprototype.actor.Player
import io.github.michelfaria.breadprototype.fud.WorldSolidFUD
import io.github.michelfaria.breadprototype.strategy.BlockSpawningStrategy
import io.github.michelfaria.breadprototype.strategy.UnprojectStrategy

class Game : ApplicationAdapter() {

    companion object {
        const val VRESX = 15
        const val VRESY = 10
        const val GRAVITY = -9.18f
        const val RAYS_NUM = 1000
        const val BLUR_NUM = 2

        fun ptm(pixels: Float): Float {
            return pixels / 16
        }
    }

    private lateinit var batch: SpriteBatch
    private lateinit var textureAtlas: TextureAtlas
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
    private lateinit var blockSpawningStrategy: BlockSpawningStrategy
    private lateinit var unprojectStrategy: UnprojectStrategy

    override fun create() {
        initGraphics()
        initScene2D()
        unprojectStrategy = UnprojectStrategy(camera)
        initTiled()
        initBox2D()
        blockSpawningStrategy = BlockSpawningStrategy(world)
        initBox2DLights()
        initTiledBox2DIntegration()
        initInputProcessor()
        makePlayer()
    }

    private fun initGraphics() {
        batch = SpriteBatch()
        textureAtlas = TextureAtlas("default.atlas")
    }

    private fun initScene2D() {
        camera = OrthographicCamera()
        viewport = FitViewport(VRESX.toFloat(), VRESY.toFloat(), camera)
        stage = Stage(viewport)
    }

    private fun initTiled() {
        tmxMapLoader = TmxMapLoader()
        tiledMap = tmxMapLoader.load("maps/test.tmx")
        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap, ptm(1f), batch)
    }

    private fun initBox2D() {
        box2DDebugRenderer = Box2DDebugRenderer()
        world = World(Vector2(0f, GRAVITY), true)
        world.setContactListener(MyContactListener())
    }

    private fun initBox2DLights() {
        rayHandler = RayHandler(world)
        rayHandler.setBlur(true)
        rayHandler.setBlurNum(BLUR_NUM)
        rayHandler.setAmbientLight(Color(0f, 0f, 0f, 1f))
        PointLight(rayHandler, RAYS_NUM, Color(1f, 1f, 1f, 1f), 10f, 10f, 10f)
    }

    private fun initTiledBox2DIntegration() {
        for (layer in tiledMap.layers) {
            if (!isPhysicsLayer(layer)) {
                continue
            }

            val bdef = BodyDef()
            val shape = PolygonShape()
            val fdef = FixtureDef()

            for (o in layer.objects.getByType(RectangleMapObject::class.java)) {
                val rectangle = o.rectangle

                bdef.type = BodyDef.BodyType.StaticBody
                bdef.position.x = ptm(rectangle.x + rectangle.width / 2)
                bdef.position.y = ptm(rectangle.y + rectangle.height / 2)
                shape.setAsBox(ptm(rectangle.width / 2), ptm(rectangle.height / 2))
                fdef.shape = shape
                fdef.filter.categoryBits = BIT_SOLID

                val body = world.createBody(bdef)
                body.createFixture(fdef).userData = WorldSolidFUD()
            }
            shape.dispose()
        }
    }

    private fun initInputProcessor() {
        inputProcessor = MyInputProcessor(blockSpawningStrategy, unprojectStrategy)
        Gdx.input.inputProcessor = inputProcessor
    }

    private fun isPhysicsLayer(layer: MapLayer): Boolean {
        return layer.name.contains("[phys]")
    }

    private fun makePlayer() {
        val player = Player(textureAtlas, world)
        player.body.setTransform(2f, 5f, 0f)
        stage.addActor(player)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
    }

    override fun render() {
        update()
        clearScreen()
        renderTiledMap()
        batch.begin()
        run {
            drawStage()
        }
        batch.end()
        renderLighting()
        renderDebug()
    }

    private fun update() {
        world.step(1 / 60f, 6, 2)
        stage.act()
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

    private fun renderDebug() {
        box2DDebugRenderer.render(world, camera.combined)
    }

    override fun dispose() {
        batch.dispose()
        textureAtlas.dispose()
        stage.dispose()
        tiledMapRenderer.dispose()
        tiledMap.dispose()
        box2DDebugRenderer.dispose()
        rayHandler.dispose()
        world.dispose()
    }
}
