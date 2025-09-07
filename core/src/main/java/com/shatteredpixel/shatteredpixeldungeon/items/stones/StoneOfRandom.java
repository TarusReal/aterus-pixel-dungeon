package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.*;

public class StoneOfRandom extends Runestone {
    private int uses = 2;

    // Map von Runenstein-Klassen zu Wahrscheinlichkeiten (Summe = 1.0)
    private static final LinkedHashMap<Class<? extends Runestone>, Float> RUNESTONE_PROBABILITIES = new LinkedHashMap<>();
    static {
        RUNESTONE_PROBABILITIES.put(StoneOfAggression.class, 0.125f);
        RUNESTONE_PROBABILITIES.put(StoneOfBlast.class, 0.125f);
        RUNESTONE_PROBABILITIES.put(StoneOfBlink.class, 0.125f);
        RUNESTONE_PROBABILITIES.put(StoneOfClairvoyance.class, 0.125f);
        RUNESTONE_PROBABILITIES.put(StoneOfDeepSleep.class, 0.125f);
        //RUNESTONE_PROBABILITIES.put(StoneOfDisarming.class, 0.125f);
        RUNESTONE_PROBABILITIES.put(StoneOfFear.class, 0.125f);
        RUNESTONE_PROBABILITIES.put(StoneOfFlock.class, 0.125f);
        RUNESTONE_PROBABILITIES.put(StoneOfShock.class, 0.125f);
        // Summe = 1.125, ggf. anpassen, falls du andere Wahrscheinlichkeiten willst
    }

    // Zwei Wahrscheinlichkeits-Listen für Inventar- und Wurfsteine
    private static final LinkedHashMap<Class<? extends Runestone>, Float> INVENTORY_STONE_PROBABILITIES = new LinkedHashMap<>();
    private static final LinkedHashMap<Class<? extends Runestone>, Float> THROWABLE_STONE_PROBABILITIES = new LinkedHashMap<>();
    static {
        // Beispielhafte Verteilung, anpassbar
        INVENTORY_STONE_PROBABILITIES.put(StoneOfClairvoyance.class, 0.25f);
        INVENTORY_STONE_PROBABILITIES.put(StoneOfDeepSleep.class, 0.25f);
        INVENTORY_STONE_PROBABILITIES.put(StoneOfFear.class, 0.25f);
        INVENTORY_STONE_PROBABILITIES.put(StoneOfFlock.class, 0.25f);
        THROWABLE_STONE_PROBABILITIES.put(StoneOfAggression.class, 0.25f);
        THROWABLE_STONE_PROBABILITIES.put(StoneOfBlast.class, 0.25f);
        THROWABLE_STONE_PROBABILITIES.put(StoneOfBlink.class, 0.25f);
        THROWABLE_STONE_PROBABILITIES.put(StoneOfShock.class, 0.25f);
    }

    public StoneOfRandom() {
        super();
         image =  ItemSpriteSheet.STONE_RANDOM;
    }

    @Override
    protected void activate(int cell) {
        if (uses <= 0) return;
        uses--;
        Class<? extends Runestone> chosen = chooseRunestone();
        if (chosen != null) {
            try {
                Runestone stone = chosen.getDeclaredConstructor().newInstance();
                stone.anonymize(); // damit keine doppelten Drops etc.
                stone.activate(cell);
            } catch (Exception e) {
                // Fehlerbehandlung
            }
        }
        if (uses == 0) {
            curItem.detach(curUser.belongings.backpack);
        }
    }

    private Class<? extends Runestone> chooseRunestone() {
        Random random = new Random();
        LinkedHashMap<Class<? extends Runestone>, Float> map;
        if (uses == 1) {
            map = INVENTORY_STONE_PROBABILITIES;
        } else {
            map = THROWABLE_STONE_PROBABILITIES;
        }
        if (!map.isEmpty()) {
            float rand = random.nextFloat();
            float cumulative = 0f;
            for (Map.Entry<Class<? extends Runestone>, Float> entry : map.entrySet()) {
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
