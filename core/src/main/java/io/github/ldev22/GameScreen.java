package io.github.ldev22;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;
    private final Batch batch;
    private final Texture background = new Texture(Gdx.files.internal("assets/grass.png"));

    private final Viewport viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT);

    private final Array<Texture> idleTextures = loadIdleTextures();
    private final Animation<Texture> idleAnimation = new Animation<>(1/4f, idleTextures);

    private final IdlePlayer idlePlayer = new IdlePlayer(
        WORLD_WIDTH / 2, WORLD_HEIGHT / 2,
        viewport, idleTextures, idleAnimation
    );
    public GameScreen(Main game){
        this.batch = game.getBatch();
        //because our tile is a 64x64 png we apply wrap to the image so that it fills the entire screen
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        this.batch.begin();
        drawBackground();
        this.batch.end();
    }
    @Override
    public void dispose() {
        background.dispose();
    }

    private void drawBackground(){
        float tileSize = 1f;
        float u2 = viewport.getWorldWidth() / tileSize;
        float v2 = viewport.getWorldHeight() / tileSize;
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight(), 0,0, u2, v2);
    }

    private Array<Texture> loadIdleTextures(){
        Array<Texture> textures = new Array<>();
        for(int i = 0; i <= 3; i++){
            textures.add(new Texture(Gdx.files.internal("player/idle/player-idle-" + i +".png")));
        }
        return textures;
    }
}
