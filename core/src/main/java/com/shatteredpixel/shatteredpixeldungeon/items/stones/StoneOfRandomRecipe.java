package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import java.util.ArrayList;

public class StoneOfRandomRecipe extends Recipe.SimpleRecipe {

    public StoneOfRandomRecipe() {
        // Nur ein Runenstein als Zutat, Energie als cost
        this.inputs = new Class[]{Runestone.class};
        this.inQuantity = new int[]{1};
        this.cost = 2;
        this.output = StoneOfRandom.class;
        this.outQuantity = 1;
    }

    @Override
    public boolean testIngredients(ArrayList<Item> ingredients) {
        if (ingredients.size() != 1) return false;
        Item stone = ingredients.get(0);
        return stone instanceof Runestone;
    }

    @Override
    public int cost(ArrayList<Item> ingredients) {
        return 2;
    }

    @Override
    public Item brew(ArrayList<Item> ingredients) {
        if (!testIngredients(ingredients)) return null;
        Item stone = ingredients.get(0);
        stone.quantity(stone.quantity() - 1);
        return new StoneOfRandom();
    }

    @Override
    public Item sampleOutput(ArrayList<Item> ingredients) {
        return new StoneOfRandom();
    }
}
