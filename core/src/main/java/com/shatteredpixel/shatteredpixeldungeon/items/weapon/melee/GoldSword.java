package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.utils.Random;

public class GoldSword extends MeleeWeapon {
    float goldprobability; // Chance
    {
        image = ItemSpriteSheet.LONGSWORD; // Platzhalter, ggf. eigenes Sprite
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.1f;
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

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // Erhöhe Gold-Drop-Chance: 50% Chance, 1-3 Gold zu droppen
        if (defender.isAlive() == false && Dungeon.level != null && Math.random() < goldprobability) {
            Dungeon.level.drop(new Gold(Random.Int(55,433)), defender.pos).sprite.drop();
        }
       return super.proc(attacker, defender, damage);

    }
}
