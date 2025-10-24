package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import java.util.ArrayList;
import java.util.List;

import com.watabou.utils.Random;

public class RainbowSheep extends Sheep {
    {
        this.spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.RainbowSheepSprite.class;
    }
    void initialize(int depth, float lifespan) {
        super.initialize(lifespan);
        if (depth > 0) {
            ArrayList<RainbowSheep> children = new ArrayList();
            for (int i : PathFinder.NEIGHBOURS4) {
                int npos = pos + i;

                if (Dungeon.level.insideMap(npos)
                        && Actor.findChar(npos) == null
                        && !(Dungeon.level.pit[npos])&&!Dungeon.level.solid[npos]
                    /*(terr == Terrain.EMPTY || terr == Terrain.EMBERS || terr == Terrain.EMPTY_DECO||terr==Terrain.WATER||terr==Terrain.GRASS||terr==Terrain.HIGH_GRASS||terr==Terrain.FURROWED_GRASS) && Actor.findChar(npos) == null*/) {

                    boolean wallAdjacent = false;
                    for (int n : PathFinder.NEIGHBOURS8){
                        int spos = npos + n;
                        if(Dungeon.level.insideMap(spos) && !Dungeon.level.passable[spos]){
                            wallAdjacent = true;
                            break;
                        }
                    }
                    //if(!wallAdjacent){break;}

                    RainbowSheep child = new RainbowSheep();
                    child.pos = npos;
                    children.add(child);
                    GameScene.add(child);
                }
            }
            for(RainbowSheep child : children) {
                child.initialize(depth - 1, lifespan*Random.Float(0.7f, 1.1f));

            }
        }
    }

    @Override
    public void initialize(float lifespan) {
        initialize(3, lifespan);
    }

    @Override
    public boolean interact(Char c) {
        if (c == Dungeon.hero) {
            Sample.INSTANCE.play(Assets.Sounds.SHEEP, 1, Random.Float(0.91f, 1.1f));
            // Spieler auf das Feld des Schafs bewegen
            int oldPos = Dungeon.hero.pos;
            Dungeon.hero.pos = this.pos;
            Dungeon.hero.sprite.move(oldPos, this.pos);
            Dungeon.hero.sprite.visible = true;
        }
        return true;
    }
}
