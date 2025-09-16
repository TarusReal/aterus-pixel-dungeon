package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.*;

public class StoneOfRandom extends Runestone {
    Class<? extends Runestone> chosen;
    // Eine Wahrscheinlichkeits-Liste für alle Steine
    private static final LinkedHashMap<Class<? extends Runestone>, Float> STONE_PROBABILITIES = new LinkedHashMap<>();

    static {
        STONE_PROBABILITIES.put(StoneOfAggression.class, 0.125f);
        STONE_PROBABILITIES.put(StoneOfBlast.class, 0.125f);
        STONE_PROBABILITIES.put(StoneOfBlink.class, 0.125f);
        STONE_PROBABILITIES.put(StoneOfFear.class, 0.125f);
        STONE_PROBABILITIES.put(StoneOfClairvoyance.class, 0.125f);
        STONE_PROBABILITIES.put(StoneOfDeepSleep.class, 0.125f);
        STONE_PROBABILITIES.put(StoneOfFlock.class, 0.125f);
        STONE_PROBABILITIES.put(StoneOfShock.class, 0.125f);
        STONE_PROBABILITIES.put(StoneOfDetectMagic.class, 0.0625f);
        STONE_PROBABILITIES.put(StoneOfIntuition.class, 0.0625f);
        STONE_PROBABILITIES.put(StoneOfEnchantment.class, 0.0625f);
    }

    public StoneOfRandom() {
        super();
        image = ItemSpriteSheet.STONE_RANDOM;
    }

    public static class RandomUseTracker extends Buff {
        {
            revivePersists = true;
        }}

    ;

    protected void doUse(int cell) {

        chosen = chooseRunestone();
        if (chosen != null) {
            try {
                Runestone stone = chosen.getDeclaredConstructor().newInstance();
                // stone.anonymize();
                stone.activate(cell);
                System.out.println("Random stone used: " + chosen.getSimpleName());

            } catch (Exception e) {
                // Fehlerbehandlung
            }
        }
    }

    @Override
    protected void activate(int cell) {
        doUse(cell);
    }

    @Override
    protected void onThrow(int cell) {
        if (!anonymous) {
            Catalog.countUse(getClass());
            Talent.onRunestoneUsed(curUser, cell, getClass());

            if (curUser.buff(RandomUseTracker.class) == null) {
                Buff.affect(curUser, RandomUseTracker.class);
                Heap heap = Dungeon.level.drop(this, cell);
                if (!heap.isEmpty()) {
                    heap.sprite.drop(cell);
                }
            } else {
                curUser.buff(RandomUseTracker.class).detach();
            }

        }

        activate(cell);
        if (Actor.findChar(cell) == null) Dungeon.level.pressCell(cell);
        Invisibility.dispel();

    }

    private Class<? extends Runestone> chooseRunestone() {
        Random random = new Random();
        if (!STONE_PROBABILITIES.isEmpty()) {
            float rand = random.nextFloat();
            float cumulative = 0f;
            for (Map.Entry<Class<? extends Runestone>, Float> entry : STONE_PROBABILITIES.entrySet()) {
                cumulative += entry.getValue();
                if (rand < cumulative) return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public String name() {
        return "Runenstein des Zufalls";
    }

    @Override
    public String desc() {
        String text = super.desc();
        if (Dungeon.hero != null) {
            if (Dungeon.hero.buff(RandomUseTracker.class) == null) {
                text += "\n\n" + Messages.get(this, "break_info");
            } else {
                text += "\n\n" + Messages.get(this, "break_warn");
            }
        }
        return text;
    }
}