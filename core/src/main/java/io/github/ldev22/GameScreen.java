package io.github.ldev22;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;
    private final Batch batch;
    private final Texture background = new Texture(Gdx.files.internal("assets/grass.png"));

    private final Viewport viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT);

    public GameScreen(Main game){
        this.batch = game.getBatch();
        //because our tile is a 64x64 png we apply wrap to the image so that it fills the entire screen
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }


}
