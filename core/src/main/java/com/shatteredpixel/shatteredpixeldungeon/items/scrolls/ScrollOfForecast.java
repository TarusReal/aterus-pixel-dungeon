package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class ScrollOfForecast extends Scroll {

    {
        icon = ItemSpriteSheet.Icons.SCROLL_FORECAST;
    }

    /*@Override
    public boolean collect(Bag container) {
        GLog.i("Attempting to collect ScrollOfForecast");
        boolean collected = super.collect(container);
        GLog.i("ScrollOfForecast collection " + (collected ? "succeeded" : "failed"));
        return collected;
    }*/

    @Override
    public void doRead() {
        // Save the hero's current state
        Bundle bundle = new Bundle();
        Dungeon.hero.storeInBundle(bundle);

        // Apply the rewind buff
        RewindBuff rewind = Buff.affect(Dungeon.hero, RewindBuff.class);
        rewind.setRewindData(bundle);

        identify();
        Sample.INSTANCE.play(Assets.Sounds.READ);
        GLog.i(Messages.get(this, "forewarned"));

        readAnimation();
    }

    public static class RewindBuff extends Buff {

        private static final String BUNDLE = "bundle";
        private static final String TURNS_LEFT = "turns_left";

        private Bundle rewindData;
        private int turnsLeft = 10;

        public void setRewindData(Bundle bundle) {
            this.rewindData = bundle;
            turnsLeft = 10; // Reset turns when new data is set
        }

        @Override
        public boolean act() {
            turnsLeft--;

            if (turnsLeft <= 0) {
                // Time to rewind!
                if (rewindData != null) {
                    Dungeon.hero.restoreFromBundle(rewindData);
                    GLog.w(Messages.get(this, "rewind"));
                }
                detach();
            } else {
                spend(TICK);
            }

            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            if (rewindData != null) {
                bundle.put(BUNDLE, rewindData);
            }
            bundle.put(TURNS_LEFT, turnsLeft);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            if (bundle.contains(BUNDLE)) {
                rewindData = bundle.getBundle(BUNDLE);
            }
            turnsLeft = bundle.getInt(TURNS_LEFT);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", turnsLeft);
        }
    }
}
