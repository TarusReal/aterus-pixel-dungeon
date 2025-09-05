package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.utils.Random;

public class GoldSword extends MeleeWeapon {
    float goldprobability; // Chance
    {
    image = ItemSpriteSheet.GOLD_SWORD;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.2f;
        tier = 6;
        goldprobability=0.1f;
    }

    @Override
    public int max(int lvl) {
        return  (int)4.7f*(tier+1) +
                lvl*(tier+1);   //scaling unchanged
    }
    @Override
    public String desc() {
        return Messages.get(this, "Ein prächtiges Schwert aus purem Gold. Es glänzt und erhöht die Chance, dass Gegner Gold fallen lassen.");
    }


    public void tryGoldSpawn(int pos) {

        if ( Dungeon.level != null && Math.random() < goldprobability) {
            Item i = new Gold().random();
            i.quantity(i.quantity()/2);
            Dungeon.level.drop(i, pos).sprite.drop();
        }
    }

}
