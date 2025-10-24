package com.shatteredpixel.shatteredpixeldungeon.sprites;


import com.watabou.noosa.Game;
import com.watabou.utils.Random;

import java.nio.Buffer;

public class RainbowSheepSprite extends SheepSprite {
    float offset;
    public RainbowSheepSprite(){
        super();
        offset = Random.Float(0,6f);
        setTintByTime();
    }

    private void setTintByTime() {
        float t = ((Game.timeTotal + offset) % 7f)/7; // t von 0 bis <1, mit Offset
        // Farbverlauf: Hellblau -> Türkis -> Lila -> Dunkelblau -> Hellblau
        float r, g, b;
        if (t < 0.25f) { // Hellblau -> Türkis
            float f = t / 0.25f;
            r = 0.3f * (1 - f) + 0.0f * f;
            g = 0.7f * (1 - f) + 0.9f * f;
            b = 1.0f;
        } else if (t < 0.5f) { // Türkis -> Lila
            float f = (t - 0.25f) / 0.25f;
            r = 0.0f * (1 - f) + 0.6f * f;
            g = 0.9f * (1 - f) + 0.2f * f;
            b = 1.0f * (1 - f) + 0.7f * f;
        } else if (t < 0.75f) { // Lila -> Dunkelblau
            float f = (t - 0.5f) / 0.25f;
            r = 0.6f * (1 - f) + 0.1f * f;
            g = 0.2f * (1 - f) + 0.1f * f;
            b = 0.7f * (1 - f) + 0.5f * f;
        } else { // Dunkelblau -> Hellblau
            float f = (t - 0.75f) / 0.25f;
            r = 0.1f * (1 - f) + 0.3f * f;
            g = 0.1f * (1 - f) + 0.7f * f;
            b = 0.5f * (1 - f) + 1.0f * f;
        }
        tint(r, g, b, 0.7f);
    }

    @Override
    public void draw() {

        super.draw(0.3f);

    }
    @Override
    public void update() {
        super.update();
        if (flashTime <= 0){
            setTintByTime();
        }
    }

}
