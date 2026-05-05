package io.github.ldev22;

import com.badlogic.gdx.Game;

public class CoinGame extends Game {

    @Override
    public void create() {
        CoinLevel cl = new CoinLevel(this);
        setScreen(cl);
    }
}
