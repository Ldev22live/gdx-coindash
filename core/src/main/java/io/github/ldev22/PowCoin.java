package io.github.ldev22;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PowCoin extends Coin{
    private Sound sound;
    public PowCoin(float x, float y, Animation<TextureRegion> animation, Sound sound){
        super(x, y, animation, sound);
        this.sound = sound;
    }

    @Override
    public void onCollected(Player player) {
        player.addLife(1);
        sound.play();
        collected = true;
    }
}
