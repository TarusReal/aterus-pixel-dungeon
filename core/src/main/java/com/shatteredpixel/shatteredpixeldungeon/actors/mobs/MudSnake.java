package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Berry;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MudSnakeSprite;
import com.watabou.utils.Random;

public class MudSnake extends Snake {

    {
        spriteClass = MudSnakeSprite.class;

        HP = HT = 12;
        defenseSkill = 3;

        EXP = 4;
        maxLvl = 9;

        loot = Generator.Category.SEED;
        lootChance = 0.56f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 2, 4 );
    }

    @Override
    public int attackSkill( Char target ) {return 8;}

    @Override
    public void damage(int dmg, Object src) {
        boolean isPhysical = false;
        // Physischer Schaden: src ist Char (Mob/Hero) oder null (Standardangriff)
        if (src == null || src instanceof com.shatteredpixel.shatteredpixeldungeon.actors.Char) {
            isPhysical = true;
        }
        // Falls physisch, reduziere Schaden
        if (isPhysical) {
            super.damage((int) (dmg * 0.4f), src);
        } else {
            super.damage(dmg, src);
        }
    }
/// burrow through the ground, allowing it to quickly escape from danger or ambush its prey.
    @Override
    public void rollToDropLoot() {
        super.rollToDropLoot();
        if (Dungeon.hero.lvl <= maxLvl + 2){
            switch (Random.Int(5)) {
                case 0:
                    Dungeon.level.drop(new Berry(), pos).sprite.drop();
                    break;
                case 1:
                    Dungeon.level.drop(new Dewdrop(), pos).sprite.drop();
                    break;
                case 2:
                    Dungeon.level.drop(new Dewdrop(), pos).sprite.drop();
                    Dungeon.level.drop(new Dewdrop(), pos).sprite.drop();
                case 3:
                    DriedRose rose =Dungeon.hero.belongings.getItem( DriedRose.class );
                    if(rose!=null&&rose.droppedPetals < 10) {
                        Dungeon.level.drop(new DriedRose.Petal(), pos).sprite.drop();
                        rose.droppedPetals++;
                    } else {
                        Dungeon.level.drop(new Berry(), pos).sprite.drop();
                    }
                    break;
                case 4:
                    //nothing
                    break;
                 default:
                     Dungeon.level.drop(new Gold(Random.Int(21,75)), pos).sprite.drop();
                     break;
            }
        }
    }


    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc( enemy, damage );
        if (Random.Int(2) == 0) {
            int duration = Random.IntRange(5, 6);
            //we only use half the ascension modifier here as total poison dmg doesn't scale linearly
            duration = Math.round(duration * (AscensionChallenge.statModifier(this)/2f + 0.5f));
            Buff.affect(enemy, Poison.class).set(duration);
        }

        return damage;
    }
}
