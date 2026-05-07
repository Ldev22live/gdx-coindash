package io.github.ldev22;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
    protected Rectangle rect;

    public GameObject(float x, float y, float width, float height) {
        this.rect = new Rectangle(x, y, width, height);
    }

    public abstract void update(float delta);
    public abstract void draw(Batch batch);

    public boolean overlaps(GameObject other){ return rect.overlaps(other.rect); }

    public Rectangle getRect() {
        return rect;
    }
}
