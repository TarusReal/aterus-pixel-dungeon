package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import java.util.ArrayList;
import com.watabou.utils.Random;

public class RainbowSheep extends Sheep {
    private int depth;

    void initialize(int depth, float lifespan) {
        this.depth = depth;
        super.initialize(lifespan);
        if (depth > 0) {
            for (int i : PathFinder.NEIGHBOURS8) {
                int npos = pos + i;
                int terr = Dungeon.level.map[npos];
                if ((terr == Terrain.EMPTY || terr == Terrain.EMBERS || terr == Terrain.EMPTY_DECO||terr==Terrain.WATER||terr==Terrain.GRASS||terr==Terrain.HIGH_GRASS||terr==Terrain.FURROWED_GRASS) && Actor.findChar(npos) == null) {
                    RainbowSheep child = new RainbowSheep();
                    child.pos = npos;
                    child.initialize(depth - 1, lifespan*Random.Float(0.8f, 1.1f));
                    GameScene.add(child);
                }
            }
        }
    }

    @Override
    public void initialize(float lifespan) {
        initialize(3, lifespan);
    }
}