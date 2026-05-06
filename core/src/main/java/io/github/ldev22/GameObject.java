package io.github.ldev22;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
    protected final Rectangle rect;
    protected Texture texture;

    protected static final Vector2 TMP_VEC2 = new Vector2();

    public GameObject(float x, float y, float width, float height, Texture texture){
        this.rect = new Rectangle(x ,y, width, height);
        this.texture = texture;
    }

    public GameObject(float x, float y, float width, float height){
        this(x, y, width, height, null);
    }

    public boolean overlaps(GameObject other){ return rect.overlaps(other.rect); }

    public void draw(Batch batch){
        if(texture == null) return;
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    public Vector2 getCenter(Vector2 out){
        return out.set(rect.x + rect.width / 2, rect.y + rect.height / 2);
    }

    abstract void update(float deltaTime);
}
