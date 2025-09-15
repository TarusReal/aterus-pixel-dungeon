package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WellFed;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BerryPie extends Food {
    {
        image = ItemSpriteSheet.BERRY_PIE;
        energy = Hunger.STARVING * 1.5f; // etwas schwächer als Meat Pie
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        Buff.affect(hero, WellFed.class).reset(200);
    }

    @Override
    public int value() {
        return 35 * quantity; // etwas günstiger als Meat Pie
    }
}

