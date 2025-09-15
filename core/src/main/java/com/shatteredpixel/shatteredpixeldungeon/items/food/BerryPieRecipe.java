package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import java.util.ArrayList;

public class BerryPieRecipe extends Recipe {
    @Override
    public boolean testIngredients(ArrayList<Item> ingredients) {
        boolean berry = false;
        boolean meatPie = false;
        for (Item ingredient : ingredients) {
            if (ingredient.quantity() > 0) {
                if (ingredient instanceof Berry || ingredient instanceof GreenBerry) {
                    berry = true;
                }
                if (ingredient instanceof MeatPie) {
                    meatPie = true;
                }
            }
        }
        return berry && meatPie && ingredients.size() == 2;
    }

    @Override
    public int cost(ArrayList<Item> ingredients) {
        return 10; // etwas günstiger als Meat Pie Rezept
    }

    @Override
    public Item brew(ArrayList<Item> ingredients) {
        return sampleOutput(ingredients);
    }

    @Override
    public Item sampleOutput(ArrayList<Item> ingredients) {
        return new BerryPie();
    }
}
