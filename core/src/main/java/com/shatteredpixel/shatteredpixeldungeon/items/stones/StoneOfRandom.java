package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.*;

public class StoneOfRandom extends InventoryStone {
    private int uses = 2;

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
         image =  ItemSpriteSheet.STONE_RANDOM;
    }


    protected void doUse(int cell) {
        if (uses <= 0) return;
        uses--;
        Class<? extends Runestone> chosen = chooseRunestone();
        if (chosen != null) {
            try {
                Runestone stone = chosen.getDeclaredConstructor().newInstance();
                stone.anonymize();
                stone.activate(cell);
            } catch (Exception e) {
                // Fehlerbehandlung
            }
        }
        if (uses == 0) {
            detach(Dungeon.hero.belongings.backpack);
        }
    }

    @Override
    protected void activate(int cell) {
        doUse(cell);
    }

    @Override
    protected void onItemSelected(Item item) {

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
        String text = "Ein mysteriöser Runenstein, der bei jeder Nutzung zufällig den Effekt eines anderen Runensteins auslöst. Kann zweimal verwendet werden.";
        if (Dungeon.hero != null) {
            if (uses == 2) {
                text += "\n\n" + Messages.get(this, "break_info");
            } else if (uses == 1) {
                text += "\n\n" + Messages.get(this, "break_warn");
            }
            else if(uses <= 0) {
               text+= "you should not see this";
            }
        }
        return text;
    }
}
