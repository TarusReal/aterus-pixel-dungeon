package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;

public class RainbowSheepSprite extends SheepSprite {
    float offset;
    public RainbowSheepSprite(){
        super();
        offset = Random.Float(0,6f);
        setTintByTime();
    }

    private void setTintByTime() {
        float t = ((Game.timeTotal + offset) % 6f) / 6f; // t von 0 bis <1, mit Offset
        float r, g, b;
        if (t < 1f/3f) { // Hellblau -> Türkis
            float f = t * 3f;
            r = 0.25f + (0.2f-0.25f)*f;
            g = 0.45f + (0.9f-0.45f)*f;
            b = 1.0f + (1.0f-1.0f)*f;
        } else if (t < 2f/3f) { // Türkis -> Lila
            float f = (t-1f/3f)*3f;
            r = 0.2f + (0.7f-0.2f)*f;
            g = 0.9f - (0.6f)*f;
            b = 1.0f + (0.0f)*f;
        } else { // Lila -> Dunkelblau/Lila
            float f = (t-2f/3f)*3f;
            r = 0.7f - (0.45f)*f;
            g = 0.3f - (0.15f)*f;
            b = 1.0f - (0.5f)*f;
        }
        tint(r, g, b, 0.5f);
    }



    @Override
    public void update() {
        super.update();
        if (flashTime <= 0){
            setTintByTime();
        }
    }

}
