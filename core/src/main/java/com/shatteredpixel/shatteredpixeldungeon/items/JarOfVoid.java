package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.NoXPBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class JarOfVoid extends Item {

    {
        image = ItemSpriteSheet.VIAL;
        defaultAction = AC_THROW;
        stackable = true;
    }

    private static final float TIME_TO_THROW = 1.0f;

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
    }

    @Override
    protected void onThrow(int cell) {
        if (Dungeon.level.pit[cell]) {
            super.onThrow(cell);
        } else {
            activate(cell);
            // Visual effect for the jar breaking
            if (Dungeon.level.heroFOV[cell]) {
                CellEmitter.get(cell).burst(Speck.factory(Speck.JET), 5);
                Sample.INSTANCE.play(Assets.Sounds.SHATTER);
            }
            // The jar is destroyed after use
        }
    }
    public void activate(int cell) {
        Sample.INSTANCE.play(Assets.Sounds.BURNING);
        Sample.INSTANCE.play(Assets.Sounds.BLAST);
        Sample.INSTANCE.play(Assets.Sounds.PUFF);

        // Visual effect
        for (int i = 0; i < 8; i++) {
            int pos = cell;
            for (int j = 0; j < 3; j++) {
                int n = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
                if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[n]) {
                    CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 2);
                    CellEmitter.get(n).burst(Speck.factory(Speck.LIGHT), 2);
                }
                pos = n;
            }
        }

        // Store a reference to the hero
        Hero hero = Dungeon.hero;

        // Suck in all non-boss enemies
        boolean mobKilled = false;
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment == Char.Alignment.ENEMY && !mob.properties().contains(Char.Property.BOSS)) {
                // Calculate distance for damage
                int dist = Dungeon.level.distance(cell, mob.pos);

                // Deal significant damage in one go (75% of max HP)
                int damage = (int)(mob.HT);
                if (damage < 5) damage = 5; // Minimum damage

                // Move mob towards the vortex
                int newPos = mob.pos;
                for (int i : PathFinder.NEIGHBOURS8) {
                    if (Dungeon.level.distance(cell, mob.pos + i) < dist) {
                        newPos = mob.pos + i;
                        break;
                    }
                }

                if (newPos != mob.pos && Dungeon.level.passable[newPos] && Actor.findChar(newPos) == null) {
                    mob.sprite.move(mob.pos, newPos);
                    mob.move(newPos);
                }

                // Prevent XP gain and apply damage/vertigo
                Buff.affect(mob, NoXPBuff.class, 1f);
                int hpBefore = mob.HP;
                mob.damage(damage, this);
                if (mob.HP <= 0 && hpBefore > 0 && !Dungeon.level.heroFOV[mob.pos]) {
                    mobKilled = true;
                }
                Buff.affect(mob, Vertigo.class, 3f);
            }
        }
        // Damage the player if too close
        int heroDist = Dungeon.level.distance(cell, hero.pos);
        if (heroDist <= 2) {
            int damage = (3 - heroDist) * 20; // 20 damage at distance 1, 40 at distance 0
            hero.damage(damage, this);
        }
        // Zeige die Nachricht nur einmal, wenn mindestens ein Mob außerhalb des Sichtfelds getötet wurde
        if (mobKilled) {
            GLog.n(Messages.get(JarOfVoid.class, "death")); // Rot statt Standardfarbe
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 50 * quantity;
    }
}
