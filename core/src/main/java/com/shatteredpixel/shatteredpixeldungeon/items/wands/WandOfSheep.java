package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RainbowSheep;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class WandOfSheep extends Wand {

    {
        image = ItemSpriteSheet.WAND_SHEEP;
        collisionProperties = Ballistica.WONT_STOP;
    }

    ConeAOE cone;
    int target;

    @Override
    public boolean tryToZap(Hero owner, int target) {
        if (super.tryToZap(owner, target)){
            this.target = target;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
            cone = null;
            ((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
                    MagicMissile.MAGIC_MISS_CONE,
                    curUser.sprite,
                    target,
                    callback
            );
            Sample.INSTANCE.play( Assets.Sounds.ZAP );

    }

    @Override
    public void onZap(Ballistica bolt) {
            // Nur das Ziel-Feld prüfen
            int cell = target;
            //int terr = Dungeon.level.map[cell];
            if ((Dungeon.level.insideMap(cell)
                    && Actor.findChar(cell) == null
                    && !(Dungeon.level.pit[cell]))
                    && !Char.hasProp(Actor.findChar(cell), Char.Property.IMMOVABLE)
                    && Actor.findChar(cell) == null) {
                RainbowSheep sheep = new RainbowSheep();
                sheep.pos = cell;
                sheep.initialize((int) (chargesPerCast()+(buffedLvl()+2)/3f),10f + Random.Float(2f)+buffedLvl()*2f);
                GameScene.add(sheep);
            }

    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {

    }

    @Override
    protected int chargesPerCast() {
        if (cursed) {
            return 1;
        }
        return (int) GameMath.gate(1, (int)Math.ceil(curCharges*0.3f), 3);
    }

    @Override
    public String statsDesc() {
        return "Verwandelt ein Gebiet in einen Schaf-Kegel. Anzahl Schafe: " + (int) (chargesPerCast()+(buffedLvl()+2)/3f);
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        // Weiße bis hellgraue Partikel für Schafwolle
        particle.color( ColorMath.random(0xFFFFFF, 0xEEEEEE) );
        particle.am = 1f;
        particle.setLifespan(1f);
        particle.setSize( 1f, 1.5f);
        particle.shuffleXY(0.5f);
        float dst = Random.Float(11f);
        particle.x -= dst;
        particle.y += dst;
    }
}
