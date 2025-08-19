package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Sponge extends Artifact {


    private int tickCounter = 0;

    public static final String AC_WRING = "WRING";
    public static final String AC_WRING_OUT = "WRING_OUT";
    public Sponge() {
        super();
        image = ItemSpriteSheet.SPONGE1;
        // Beschreibung und weitere Initialisierung folgt
        defaultAction =AC_WRING_OUT ;
        charge=0;
        chargeCap=30;
    }



    @Override
    public void charge(Hero target, float amount) {
        if (charge < chargeCap && !cursed && target.buff(Burning.class) == null){
            partialCharge += 0.1f*amount;
            while (partialCharge >= 1){
                partialCharge--;
                charge++;
            }
            if (charge >= chargeCap){
                partialCharge = 0;
            }
            updateQuickslot();
        }
    }

    private void updateSprite() {
        if (charge < 10) {
            image = ItemSpriteSheet.SPONGE1;
        } else if (charge < 20) {
            image = ItemSpriteSheet.SPONGE2;
        } else {
            image = ItemSpriteSheet.SPONGE3;
        }
    }

    public void wringOut(Hero hero) {
        Waterskin skin = null;
        for (Item item : hero.belongings) {
            if (item instanceof Waterskin) {
                skin = (Waterskin) item;
                break;
            }
        }
        if (skin != null && charge > 0) {
            int canTransfer = Math.min(charge, Waterskin.MAX_VOLUME - skin.volume);
            skin.volume += canTransfer;
            charge -= canTransfer;
            updateSprite();
            hero.spendAndNext(1f);
            GLog.i(Messages.get(this, "WRING_OUT", canTransfer));
        } else {
            GLog.i(Messages.get(this, "nowater"));
        }
    }
    public void wringOut(Hero hero, int amount) {
        Waterskin skin = null;
        for (Item item : hero.belongings) {
            if (item instanceof Waterskin) {
                skin = (Waterskin) item;
                break;
            }
        }
        if (skin != null && charge > 0) {
            int canTransfer = Math.min(Math.min(charge,amount), Waterskin.MAX_VOLUME - skin.volume);
            skin.volume += canTransfer;
            charge -= canTransfer;
            updateSprite();
            hero.spendAndNext(1f);
            GLog.i(Messages.get(this, "WRING_OUT", canTransfer));
        } else {
            GLog.i(Messages.get(this, "nowater"));
        }
    }


    @Override
    public String desc() {
        return Messages.get(this, "desc", charge, chargeCap);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("waterDrops", charge);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        charge = bundle.getInt("waterDrops");
        updateSprite();
    }

    @Override
    public java.util.ArrayList<String> actions(Hero hero) {
        java.util.ArrayList<String> actions = super.actions(hero);

        if (isEquipped( hero ) && charge > 0 && !cursed && hero.buff(Burning.class) == null) {
            actions.add(AC_WRING);
        }
        if (isEquipped( hero ) && charge > 0 && !cursed && hero.buff(Burning.class) == null) {
            actions.add(AC_WRING_OUT);
        }
        return actions;


    }

    @Override
    public void execute(Hero hero, String action) {
        if (AC_WRING_OUT.equals(action)) {
            wringOut(hero);
        } else if(AC_WRING.equals(action)) {
            wringOut(hero,5);
        } else {
            super.execute(hero, action);
        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new spongeRecharge();
    }
    public class spongeRecharge extends ArtifactBuff{
        @Override
        public boolean act() {
            if (charge < chargeCap
                    && !cursed
                    && target.buff(Burning.class) == null
                    ) {
                //200 turns to charge at full
                float chargeGain = chargeCap / 500f;
               // chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
                /// ^Might want to readd this later, but for now it is too strong
                ///Maybe: chargeGain*= 1+0.5f*(RingOfEnergy.artifactChargeMultiplier(target)-1) would be the right approach
                partialCharge += chargeGain;

                while (partialCharge >= 1) {
                    partialCharge --;
                    charge ++;

                    if (charge == chargeCap){
                        partialCharge = 0;
                    }
                }
            }
            updateSprite();
            updateQuickslot();

            spend( TICK );

            return true;
        }
    }
}
