package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;


import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;

public class Halberd extends MeleeWeapon {

    {
        image = ItemSpriteSheet.HALBERD;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 0.85f;

        tier = 6;
        DLY = 1.4f; //0.67x speed
        RCH = 2;    //extra reach
    }

    @Override
    public int max(int lvl) {
        return  Math.round(6.57f*(tier+1)) +    //46 base, up from 35
                lvl*Math.round(1.285f*(tier+1)); //+9 per level, up from +7
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        //+(12+2.5*lvl) damage, roughly +55% base damage, +55% scaling
        int dmgBoost = augment.damageFactor(12 + Math.round(2.5f*buffedLvl()));
        Spear.spikeAbility(hero, target, 1, dmgBoost, this);
    }

    public String upgradeAbilityStat(int level){
        int dmgBoost = 12 + Math.round(2.5f*level);
        return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost = levelKnown ? 12 + Math.round(2.5f*buffedLvl()) : 12;
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
        } else {
            return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        boolean fastAttack = false;
        float usedDLY = 1.4f; // Standardgeschwindigkeit
        if (attacker.pos != -1 && defender.pos != -1 && attacker.pos != defender.pos && Dungeon.level != null) {
            int width = Dungeon.level.width();
            int ax = attacker.pos % width;
            int ay = attacker.pos / width;
            int dx = defender.pos % width;
            int dy = defender.pos / width;
            int manhattan = Math.abs(ax - dx) + Math.abs(ay - dy);
            if (manhattan == 1) {
                usedDLY = 1.0f;
                fastAttack = true;
            }
        }
        this.DLY = usedDLY; // DLY bleibt gesetzt, kein Zurücksetzen
        int result = super.proc(attacker, defender, damage);
        if (fastAttack) {
            result *= 0.8f;
        }
        return result;
    }

}
