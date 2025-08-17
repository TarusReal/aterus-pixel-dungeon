package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;

public class Sponge extends Artifact {

    private static final int MAX_DROPS = 30;
    private static final int TRANSFER_LIMIT = 20;
    private int waterDrops = 0;
    private int tickCounter = 0;

    public Sponge() {
        super();
        image = ItemSpriteSheet.SPONGE1;
        // Beschreibung und weitere Initialisierung folgt
        defaultAction = "WRING_OUT";
    }

    public void update() {
        if (isEquipped(Dungeon.hero) && waterDrops < MAX_DROPS) {
            if (Dungeon.hero.isAlive() && Dungeon.depth > 0) {
                tickCounter++;
                if (tickCounter >= 20) {
                    waterDrops = Math.min(MAX_DROPS, waterDrops + 1);
                    updateSprite();
                    tickCounter = 0;
                }
            }
        }
    }

    private void updateSprite() {
        if (waterDrops < 10) {
            image = ItemSpriteSheet.SPONGE1;
        } else if (waterDrops < 20) {
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
        if (skin != null && waterDrops > 0) {
            int canTransfer = Math.min(waterDrops, Waterskin.MAX_VOLUME - skin.volume);
            skin.volume += canTransfer;
            waterDrops -= canTransfer;
            updateSprite();
            hero.spendAndNext(1f);
            GLog.i(Messages.get(this, "WRING_OUT", canTransfer));
        } else {
            GLog.i(Messages.get(this, "nowater"));
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", waterDrops, MAX_DROPS);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("waterDrops", waterDrops);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        waterDrops = bundle.getInt("waterDrops");
        updateSprite();
    }

    @Override
    public java.util.ArrayList<String> actions(Hero hero) {
        java.util.ArrayList<String> actions = super.actions(hero);
        actions.add("WRING_OUT");
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        if ("WRING_OUT".equals(action)) {
            wringOut(hero);
        } else {
            super.execute(hero, action);
        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new ArtifactBuff();
    }
}
