package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;

public class GoldSword extends MeleeWeapon {
    float goldprobability; // Chance
    {
    image = ItemSpriteSheet.GOLD_SWORD;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.2f;
        tier = 6;
        goldprobability=0.1f;
    }

    /*@Override
    public int max(int lvl) {
        return  (int)4.7f*(tier+1) +
                lvl*(tier+1);   //scaling unchanged
    }*/

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        //+(7+lvl) damage, roughly +40% base dmg, +30% scaling
        int dmgBoost = augment.damageFactor(9 + buffedLvl());
        Sword.cleaveAbility(hero, target, 1, dmgBoost, this);
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target){
        if (hero.buff(Sword.CleaveTracker.class) != null){
            return 0;
        } else {
            return 1;
        }
    }
    @Override
    public String abilityInfo() {
        int dmgBoost = levelKnown ? 7 + buffedLvl() : 7;
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
        } else {
            return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
        }
    }

    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }


    public void tryGoldSpawn(int pos) {
        if ( Dungeon.level != null && Dungeon.hero.STR() > STRReq()- 2 &&Math.random() < goldprobability) {
            Item i = new Gold().random();
            i.quantity(i.quantity()/2);
            Dungeon.level.drop(i, pos).sprite.drop();
        }
    }

}
