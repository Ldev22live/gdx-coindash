package io.github.ldev22;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends GameObject{
    private static final float SCALE = 1f;
    private static final int LIFE = 5;
    private float life = LIFE;
    private final Vector2 moveDirection = new Vector2();
    private final Vector2 lastDirection = new Vector2();
    private static final float SPEED = 5f;
    private static final float DURATION = 0.4f;
    private static final float HURT_DURATION = 0.4f;
    private PlayerState state = PlayerState.IDLE;
    private final Animation<TextureRegion> idle;
    private final Animation<TextureRegion> run;
    private final Animation<TextureRegion> dead;
    private Animation<TextureRegion> processAnimation;;
    private float stateTime = 0f;
    private TextureRegion currentFrame;
    private Rectangle worldBounds;
    private int score = 0;
    private float hurtTimer = 0f;

    public void addScore(int i) {
        score += i;
    }

    public boolean isAlive() {
        return life > 0;
    }

    public enum PlayerState{
        IDLE, RUN, DEAD
    }

    public Player(float x, float y, Animation<TextureRegion> idle, Animation<TextureRegion> run, Animation<TextureRegion> dead, Rectangle worldBounds){
        super(x, y, SCALE, SCALE);
        this.idle = idle;
        this.run = run;
        this.dead = dead;

        this.state = PlayerState.IDLE;
        this.worldBounds = worldBounds;
        processAnimation = idle;
    }

    @Override
    public void update(float delta) {
        updateState(delta);
        move(delta);
        updateAnimation(delta);
        updateAttacks(delta);
    }

    private void updateState(float delta){
        if(hurtTimer > 0f){
            hurtTimer -= delta;
            state = PlayerState.DEAD;
        }else if(isDead()){
            state = PlayerState.DEAD;
        }else if(moveDirection.len() > 0){
            state = PlayerState.RUN;
        }else{
            state = PlayerState.IDLE;
        }
    }

    private void updateAttacks(float delta){

    }
    private boolean isDead(){ return life <= 0; }

    private void move(float deltaTime){
        if(moveDirection.isZero()) return;
        float newX = rect.getX() + moveDirection.x * SPEED * deltaTime;
        float newY = rect.getY() + moveDirection.y * SPEED * deltaTime;

        newX = MathUtils.clamp(newX, 0, worldBounds.getWidth() - rect.getWidth());
        newY = MathUtils.clamp(newY, 0, worldBounds.getHeight() - rect.getHeight());
        rect.setPosition(newX, newY);
    }

    private void updateAnimation(float deltaTime){
        stateTime += deltaTime;
        switch(state){
            case RUN:
                currentFrame = run.getKeyFrame(stateTime);
                processAnimation = run;
                break;
            case DEAD:
                currentFrame = dead.getKeyFrame(stateTime);
                processAnimation = dead;
                break;
            case IDLE:
            default:
                currentFrame = idle.getKeyFrame(stateTime);
                processAnimation = idle;
                break;

        }
    }
    @Override
    public void draw(Batch batch) {
        float animationDuration = processAnimation.getAnimationDuration();
        float animationPercent = 1f - (Math.max(0f, life) / DURATION);
        currentFrame = processAnimation.getKeyFrame(stateTime, true);
        if (currentFrame == null) {
            System.out.println("Frame is null!");
            return;
        }
        batch.draw(currentFrame, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    public void changeDirection(Vector2 direction){
        if(!direction.isZero()){
            lastDirection.set(direction);
        }

        moveDirection.set(direction);
    }

    public void setState(PlayerState state) {
        this.state = state;
    }
    public void addLife(int i){ life += i; }
    public void takeDamage(int damage){
        life -= damage;
        hurtTimer = HURT_DURATION;
        stateTime = 0f;
        state = PlayerState.DEAD;
    }
    public int getLife(){ return (int) life; }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
