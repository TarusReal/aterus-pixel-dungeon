package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RainbowSheep;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.ColorMath;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class WandOfSheep extends Wand {

    {
        image = ItemSpriteSheet.WAND_SHEEP;
    }

    @Override
    public void onZap(Ballistica bolt) {
        int cell = bolt.collisionPos;
        if (Dungeon.level.insideMap(cell)
                && Actor.findChar(cell) == null
                && !Dungeon.level.pit[cell]) {
            RainbowSheep sheep = new RainbowSheep();
            sheep.pos = cell;
            sheep.initialize((int) (chargesPerCast()+(buffedLvl()+2)/3f), 8f + Random.Float(2f)+buffedLvl()*1.6f);
            GameScene.add(sheep);
            Dungeon.level.occupyCell(sheep);
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
        return (int) GameMath.gate(1, (int) Math.ceil(curCharges*0.3f), 3);
    }

    @Override
    public String statsDesc() {
        return "Erschafft ein Schaf auf dem Zielfeld.";
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        if (Random.Int(10) == 0){
            particle.color(ColorMath.random(0xFFF568, 0x80791A));
        } else {
            particle.color(ColorMath.random(0x805500, 0x332500));
        }
        particle.color( ColorMath.random(0x4400CC, 0x99EEFF) );

        particle.am = 1f;
        particle.setLifespan(1f);
        particle.setSize( 1f, 1.5f);
        particle.shuffleXY(0.5f);
        float dst = Random.Float(11f);
        particle.x -= dst;
        particle.y += dst;
    }
}
