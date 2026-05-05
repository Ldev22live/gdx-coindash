package io.github.ldev22;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CoinLevel extends BaseScreen{
    private AnimatedActor idlePlayer;
    private BaseActor cactus;
    private BaseActor background;
    private Label winText;
    private float timeElapsed;
    private boolean win;
    private Label timeLabel;
    final int mapWidth = 800;
    final int mapHeight = 800;
    private boolean isIdle = true;

    Map<State, Animation<TextureRegion>> animations = new HashMap<>();
    private ArrayList<Texture> texturesToDispose = new ArrayList<>();

    State currentState;
    float stateTime = 0;
    public CoinLevel(Game g){
        super(g);
    }

    @Override
    public void create() {
        idlePlayer = new AnimatedActor();
        TextureRegion[] frames = new TextureRegion[6];
        //this loop creates the sprite animation
        for(int n = 0; n < 6; n++){
            String filename = "player/run/player-run-" + n + ".png";
            Texture tex = new Texture(Gdx.files.internal(filename));
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            frames[n] = new TextureRegion(tex);
        }
        Array<TextureRegion> framesArray = new Array<TextureRegion>(frames);

        Animation anim = new Animation(0.1f, framesArray, Animation.PlayMode.LOOP_PINGPONG);

        idlePlayer.setAnimation(anim);
        idlePlayer.setOrigin(idlePlayer.getWidth()/2, idlePlayer.getHeight()/2);
        idlePlayer.setPosition(20, 20);
        mainStage.addActor(idlePlayer);

        cactus = new BaseActor();
        Texture grass = new Texture(Gdx.files.internal("grass.png"));
        for(int i = 0; i < mapWidth; i += grass.getWidth()){
            for(int j = 0; j < mapHeight; j += grass.getHeight()){
                BaseActor tile = new BaseActor();
                tile.setTexture(grass);
                tile.setPosition(i, j);
                mainStage.addActor(tile);
            }
        }
        cactus.setTexture(new Texture(Gdx.files.internal("assets/cactus.png")));
        cactus.setPosition(400, 300);
        cactus.setOrigin(cactus.getWidth()/2, cactus.getHeight());
        mainStage.addActor(cactus);

        BitmapFont font = new BitmapFont();
        String text = "Time :0";
        Label.LabelStyle style = new Label.LabelStyle(font, Color.BLUE);
        timeLabel = new Label(text, style);
        timeLabel.setFontScale(2);
        timeLabel.setPosition(500, 440);
        uiStage.addActor(timeLabel);

        winText = new Label("You Win!", style);
        winText.setFontScale(3);
        winText.setPosition(220, 240);
        winText.setVisible(false);
        uiStage.addActor(winText);

        win = false;
    }

    @Override
    public void update(float dt) {
        idlePlayer.velocityX = 0;
        idlePlayer.velocityY = 0;

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            idlePlayer.velocityX -= 100;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            idlePlayer.velocityX += 100;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            idlePlayer.velocityY += 100;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            idlePlayer.velocityY -= 100;
        }

        idlePlayer.setX(MathUtils.clamp(idlePlayer.getX(), 0 , mapWidth - idlePlayer.getWidth()));
        idlePlayer.setY(MathUtils.clamp(idlePlayer.getY(), 0, mapHeight - idlePlayer.getHeight()));

        Rectangle cheeseRectangle = cactus.getBoundingRectangle();
        Rectangle mouseRectangle = idlePlayer.getBoundingRectangle();

        if(!win && cheeseRectangle.contains(mouseRectangle)){
            win = true;
            Action spinShrinkFadeOut = Actions.parallel(
                Actions.alpha(1),
                Actions.rotateBy(360, 1),
                Actions.scaleTo(0,0, 2),
                Actions.fadeOut(1)
            );
            cactus.addAction(spinShrinkFadeOut);

            Action fadeInColorCycleForever = Actions.sequence(
                Actions.alpha(0),
                Actions.show(),
                Actions.fadeIn(2),
                Actions.forever(
                    Actions.sequence(
                        Actions.color(new Color(1,0,0,1)),
                        Actions.color(new Color(0,0,1,1))
                    )
                )
            );
            winText.addAction(fadeInColorCycleForever);
            winText.setVisible(true);
        }
        if(!win){
            timeElapsed += dt;
            timeLabel.setText("Time: " + (int)timeElapsed);
        }

        Camera cam = mainStage.getCamera();
        cam.position.set(idlePlayer.getX() + idlePlayer.getOriginX(), idlePlayer.getY() + idlePlayer.getOriginY(), 0);
        cam.position.x = MathUtils.clamp(cam.position.x, viewWidth/2, mapWidth - viewWidth/2);
        cam.position.y = MathUtils.clamp(cam.position.y, viewHeight/2, mapHeight - viewHeight/2);
        cam.update();
    }

    public boolean keyDown(int keycode){
//        if(keycode == Input.Keys.M){
//            game.setScreen(new CheeseMenu(game));
//        }
        if(keycode == Input.Keys.P){
            togglePaused();
        }
        return false;
    }
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void dispose() {
        for (Texture texture : texturesToDispose) {
            texture.dispose();
        }
        texturesToDispose.clear();
    }
}
