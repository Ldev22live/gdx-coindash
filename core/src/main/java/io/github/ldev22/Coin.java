package io.github.ldev22;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Coin extends GameObject{
    protected boolean collected = false;
    private Animation<TextureRegion> animation;
    private static final float SIZE = 1f;
    private float stateTime = 0f;
    private Sound sound;
    public Coin(float x, float y, Animation<TextureRegion> animation, Sound sound){
        super(x, y, SIZE, SIZE);
        this.animation = animation;
        this.sound = sound;
    }

    public void collected(){
        collected = true;
    }

    public boolean isCollected(){
        return collected;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
    }

    public void onCollected(Player player){
        player.addScore(1);
        sound.play();
        collected = true;
    }

    @Override
    public void draw(Batch batch) {
        if(collected) return;
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        batch.draw(frame, rect.x, rect.y, rect.width, rect.height);
    }


}
