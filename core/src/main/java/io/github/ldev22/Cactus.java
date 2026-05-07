package io.github.ldev22;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Cactus extends GameObject{
    private static final float SIZE = 1f;
    private float SPEED = 1.5f;
    private float stateTime = 0f;
    protected boolean isHurt = false;
    private TextureRegion cactusTexture;
    private Sound sound;
    private Player player;
    private final Vector2 moveDirection = new Vector2();
    public Cactus(float x, float y, TextureRegion cactusTexture, Sound sound, Player player){
        super(x, y, SIZE, SIZE);
        this.cactusTexture = cactusTexture;
        this.sound = sound;
        this.player = player;
    }

    public void onHurt(Player player){
        player.takeDamage(1);
        isHurt = true;
        sound.play();
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        moveDirection.set(player.getRect().x - rect.x, player.getRect().y - rect.y);
        if(!moveDirection.isZero()){
            moveDirection.nor();
            rect.x += moveDirection.x * SPEED * delta;
            rect.y += moveDirection.y * SPEED * delta;
        }
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(cactusTexture, rect.x, rect.y, rect.width, rect.height);
    }

    public void increaseSpeed(){
        SPEED += 0.5f;
    }
}
