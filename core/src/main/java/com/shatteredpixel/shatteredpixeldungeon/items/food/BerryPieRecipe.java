package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import java.util.ArrayList;

public class BerryPieRecipe extends Recipe {
    @Override
    public boolean testIngredients(ArrayList<Item> ingredients) {
        boolean berry = false;
        boolean pasty = false;
        for (Item ingredient : ingredients) {
            if (ingredient.quantity() > 0) {
                if (ingredient instanceof Berry || ingredient instanceof GreenBerry) {
                    berry = true;
                }
                if (ingredient instanceof Pasty) {
                    pasty = true;
                }
            }
        }
        return berry && pasty && ingredients.size() == 2;
    }

    @Override
    public int cost(ArrayList<Item> ingredients) {
        return 10; // etwas günstiger als Meat Pie Rezept
    }

    @Override
    public Item brew(ArrayList<Item> ingredients) {
        for (Item ingredient : ingredients) {
            ingredient.quantity(ingredient.quantity() - 1);
            if (ingredient.quantity() <= 0) {
                ingredient.detach(com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero.belongings.backpack);
            }
        }
        return sampleOutput(ingredients);
    }

    @Override
    public Item sampleOutput(ArrayList<Item> ingredients) {
        return new BerryPie();
    }
}
