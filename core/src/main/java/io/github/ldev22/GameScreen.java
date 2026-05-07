package io.github.ldev22;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;
    protected static final int LIFE = 1;
    private final Batch batch;
    private final Texture background = new Texture(Gdx.files.internal("assets/grass.png"));

    private final Viewport gameViewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT);

    private final Array<TextureRegion> idleTextures = loadIdleTextures();
    private final Animation<TextureRegion> idleAnimation = new Animation<TextureRegion>(1/4f, idleTextures);
    private final Array<TextureRegion> runTextures = loadRunTextures();
    private final Animation<TextureRegion> runAnimation = new Animation<>(1/4f, runTextures);
    private final Array<TextureRegion> deadTextures = loadDeadTextures();
    private final Animation<TextureRegion> deadAnimation = new Animation<>(1/4f, deadTextures);
    private final Animation<TextureRegion> coinAnimation = new Animation<>(1/12f, loadCoinTextures());
    private final Animation<TextureRegion> powCoinAnimation = new Animation<>(1/12f, loadPowCoinTextures());
    private final Rectangle worldBounds = new Rectangle(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
    private final Player player;
    private Vector2 inputMoveDirection = new Vector2();
    private Sound collectSound;
    private Sound powCollectSound;
    private Coin coin;
    private PowCoin powCoin;
    private TextureRegion cactusTexture;
    private Sound hurtSound;
    private Sound levelUpSound;
    private final Array<Cactus> cacti = new Array<>();
    private Viewport uiViewport = new ScreenViewport();
    private final GlyphLayout layout = new GlyphLayout();
    private final BitmapFont font;
    private int level = 1;
    private int nextLevelScore = 10;
    public GameScreen(Main game){
        this.batch = game.getBatch();
        //because our tile is a 64x64 png we apply wrap to the image so that it fills the entire screen
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        player = new Player(
            WORLD_WIDTH / 2, WORLD_HEIGHT / 2,
            idleAnimation, runAnimation, deadAnimation, worldBounds

        );
        font = game.getFont();
        collectSound = Gdx.audio.newSound(Gdx.files.internal("audio/Coin.wav"));
        powCollectSound = Gdx.audio.newSound(Gdx.files.internal("audio/Powerup.wav"));
        levelUpSound = Gdx.audio.newSound(Gdx.files.internal("audio/Level.wav"));
        spawnCoin();
        spawnPowCoin();

        cactusTexture = new TextureRegion(new Texture(Gdx.files.internal("cactus.png")));
        hurtSound = Gdx.audio.newSound(Gdx.files.internal("audio/Hit.wav"));
        spawnCacti();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        gamePlay(delta);
        if(!player.isAlive() && Gdx.input.isKeyPressed(Input.Keys.C)){
            continueGame();
        }else if(!player.isAlive() && Gdx.input.isKeyPressed(Input.Keys.R)){
            resetGame();
        }
    }    private void continueGame() {


        player.addLife(LIFE);
        player.setState(Player.PlayerState.IDLE);
        spawnCoin();
        spawnPowCoin();
        spawnCacti();
    }

    private void resetGame() {
        player.addLife(5);
        player.setState(Player.PlayerState.IDLE);
        cacti.clear();
        spawnCoin();
        spawnPowCoin();
        spawnCacti();
    }

    private void gamePlay(float delta) {
        ScreenUtils.clear(Color.BLACK);
        gameViewport.apply();

        batch.setProjectionMatrix(gameViewport.getCamera().combined);
        if(player.isAlive()){
            processInput();
            player.update(delta);
            checkCollisions();
            updateLevel();
        }
        this.batch.begin();
        drawBackground();

        player.draw(batch);
        coin.update(delta);
        coin.draw(batch);
        powCoin.update(delta);
        powCoin.draw(batch);
        for(Cactus cactus : cacti){
            cactus.update(delta);
            cactus.draw(batch);
        }
        this.batch.end();

        drawUi();
    }

    private void updateLevel() {
        if(player.getScore() >= nextLevelScore){
            levelUpSound.play();
            level++;
            nextLevelScore += 10;
            spawnCacti();
        }
    }

    private void drawUi(){
        uiViewport.apply();
        batch.setProjectionMatrix(uiViewport.getCamera().combined);
        this.batch.begin();
        font.draw(batch, "Score: " + player.getScore(), 20, uiViewport.getWorldHeight() - 60);
        font.draw(batch, "Lives: " + player.getLife(), 20, uiViewport.getWorldHeight() - 120);
        font.draw(batch, "Level: " + level, 20, uiViewport.getWorldHeight() - 180);
        if(!player.isAlive()){
            layout.setText(font, "Game Over\nPress C to continue");
            font.draw(batch, layout, uiViewport.getWorldWidth() / 2 - layout.width / 2, uiViewport.getWorldHeight() / 2 - 180);
            layout.setText(font, "Press R to restart");
            font.draw(batch, layout, uiViewport.getWorldWidth() / 2 - layout.width / 2, uiViewport.getWorldHeight() / 2 - 240);
        }
        this.batch.end();
    }

    private void checkCollisions(){
        if(!coin.isCollected() && player.getRect().overlaps(coin.getRect())){
            coin.onCollected(player);
            spawnCoin();
        }
        if(!powCoin.isCollected() && player.getRect().overlaps(powCoin.getRect())){
            powCoin.onCollected(player);
            spawnPowCoin();
        }
        for(Cactus cactus : cacti){
            if(player.getRect().overlaps(cactus.getRect())){
                cactus.onHurt(player);
                spawnCacti();
                break;
            }
        }
    }

    private void spawnCoin(){
        Coin newCoin;
        do {
            float x = MathUtils.random(0, worldBounds.getWidth() - 1f);
            float y = MathUtils.random(0, worldBounds.getHeight() - 1f);
            newCoin = new Coin(x, y, coinAnimation, collectSound);
        } while (newCoin.getRect().overlaps(player.getRect()) || powCoin != null && newCoin.getRect().overlaps(powCoin.getRect()) || overlapsAnyCactus(newCoin));
        coin = newCoin;
    }

    private void spawnPowCoin(){
        PowCoin newCoin;
        do{
            float x = MathUtils.random(0, worldBounds.getWidth() - 1f);
            float y = MathUtils.random(0, worldBounds.getHeight() - 1f);
            newCoin = new PowCoin(x, y, powCoinAnimation, powCollectSound);
        }while(newCoin.getRect().overlaps(player.getRect()) || coin != null && newCoin.getRect().overlaps(coin.getRect()) || overlapsAnyCactus(newCoin));
        powCoin = newCoin;
    }

    private void spawnCacti(){
        cacti.clear();
        for(int i = 0; i < level; i++){
            spawnCactus();
        }
    }

    private void spawnCactus(){
        Cactus cactus;
        do{
            float x = MathUtils.random(0, worldBounds.getWidth() - 1f);
            float y = MathUtils.random(0, worldBounds.getHeight() - 1f);
            cactus = new Cactus(x, y, cactusTexture, hurtSound, player);
        }while(cactus.getRect().overlaps(player.getRect()) || coin != null && cactus.getRect().overlaps(coin.getRect()) || powCoin != null && cactus.getRect().overlaps(powCoin.getRect()) || overlapsAnyCactus(cactus));
        cacti.add(cactus);
    }

    private boolean overlapsAnyCactus(GameObject object){
        for(Cactus cactus : cacti){
            if(object.getRect().overlaps(cactus.getRect())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void dispose() {
        background.dispose();
        batch.dispose();
        collectSound.dispose();
        powCollectSound.dispose();
    }

    private void drawBackground(){
        float tileSize = 1f;
        float u2 = gameViewport.getWorldWidth() / tileSize;
        float v2 = gameViewport.getWorldHeight() / tileSize;
        batch.draw(background, 0, 0, gameViewport.getWorldWidth(), gameViewport.getWorldHeight(), 0,0, u2, v2);
    }

    private Array<TextureRegion> loadIdleTextures(){
        Array<TextureRegion> frames = new Array<>();
        for(int i = 0; i <= 3; i++){
            frames.add(new TextureRegion(new Texture(Gdx.files.internal("player/idle/player-idle-" + i +".png"))));
        }
        return frames;
    }

    private Array<TextureRegion> loadRunTextures(){
        Array<TextureRegion> frames = new Array<>();
        for(int i = 0; i <= 5; i++){
            frames.add(new TextureRegion(new Texture(Gdx.files.internal("player/run/player-run-" + i +".png"))));
        }
        return frames;
    }

    private Array<TextureRegion> loadDeadTextures(){
        Array<TextureRegion> frames = new Array<>();
        for(int i = 1; i <= 2; i++){
            frames.add(new TextureRegion(new Texture(Gdx.files.internal("player/hurt/player-hurt-" + i +".png"))));
        }
        return frames;
    }

    private Array<TextureRegion> loadCoinTextures() {
        Array<TextureRegion> frames = new Array<>();
        for(int i = 1; i <= 11; i++){
            frames.add(new TextureRegion(new Texture(Gdx.files.internal("coin/coin-frame-" + i +".png"))));
        }
        return frames;
    }

    private Array<TextureRegion> loadPowCoinTextures(){
        Array<TextureRegion> frames = new Array<>();
        for(int i = 1; i <= 10; i++){
            frames.add(new TextureRegion(new Texture(Gdx.files.internal("pow/pow-frame-" + i +".png"))));
        }
        return frames;
    }

    private void processInput(){
        inputMoveDirection.setZero();
        player.setState(Player.PlayerState.RUN);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            inputMoveDirection.x = -1;
        }else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            inputMoveDirection.x = 1;
        }else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            inputMoveDirection.y = 1;
        }else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            inputMoveDirection.y = -1;
        }
        inputMoveDirection.nor();
        player.changeDirection(inputMoveDirection);
    }
}
