package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfGuidance;
import java.util.ArrayList;

public class StoneOfRevelationRecipe extends Recipe.SimpleRecipe {

    public StoneOfRevelationRecipe() {
        this.inputs = new Class[]{ScrollOfGuidance.class};
        this.inQuantity = new int[]{1};
        this.cost = 2;
        this.output = StoneOfRevelation.class;
        this.outQuantity = 2;
    }

    @Override
    public boolean testIngredients(ArrayList<Item> ingredients) {
        if (ingredients.size() != 1) return false;
        return ingredients.get(0) instanceof ScrollOfGuidance;
    }

    @Override
    public int cost(ArrayList<Item> ingredients) {
        return 2;
    }

    @Override
    public Item brew(ArrayList<Item> ingredients) {
        if (!testIngredients(ingredients)) return null;
        for (Item item : ingredients) {
            if (item instanceof Runestone && !(item instanceof StoneOfRevelation)) item.quantity(item.quantity() - 1);
            if (item instanceof ScrollOfGuidance) item.quantity(item.quantity() - 1);
        }
        Item result = new StoneOfRevelation();
        result.quantity(outQuantity);
        return result;
    }

    @Override
    public Item sampleOutput(ArrayList<Item> ingredients) {
        return new StoneOfRevelation();
    }
}

