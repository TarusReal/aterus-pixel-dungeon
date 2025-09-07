package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.watabou.utils.Random;

public class BookOfNecromancy extends Trinket {

	{
		image = ItemSpriteSheet.BOOK_OF_NECROMANCY;

	}
	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x440066 );
	@Override
	protected int upgradeEnergyCost() {
		return 6 + 2 * level();
	}

	@Override
	public String statsDesc() {
		if (isIdentified()){
			return Messages.get(this,
					"stats_desc",
					Messages.decimalFormat("#.##", 100*(allyReviveChance())),
					Messages.decimalFormat("#.##", 100*(enemyReviveChance())));
		} else {
			return Messages.get(this,
					"typical_stats_desc",
					Messages.decimalFormat("#.##", 100*(allyReviveChance(0))),
					Messages.decimalFormat("#.##", 100*(enemyReviveChance(0))));

		}
	}

	public static float allyReviveChance() {
		return allyReviveChance(trinketLevel(BookOfNecromancy.class)+1);
	}

	public static float allyReviveChance(int level) {
		if (level <= -1) {
			return 0f;
		} else {
			return 0.10f + 0.05f * level; // 10% Basis, +5% pro Level
		}
	}
	public static float enemyReviveChance() {
		return  enemyReviveChance(trinketLevel(BookOfNecromancy.class)+1);
	}

	public static float enemyReviveChance(int level) {
		if (level <= -1) {
			return 0f;
		} else {
			return 0.05f + 0.03f * level; // 10% Basis, +5% pro Level
		}
	}
	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	public static boolean tryRevive( Char defender, int damage) {
		if(trinketLevel(BookOfNecromancy.class)<0 || defender == null) {
			return false;
		}
		float randomf=Random.Float();
		if (damage >= defender.HP
				&& randomf <= allyReviveChance()+enemyReviveChance()
				&& !defender.isImmune(Corruption.class)
				&& defender.buff(Corruption.class) == null
				&& !defender.isImmune(Undead.class)
				&& defender.buff(Undead.class) == null
				&& defender instanceof Mob
				&& defender.isAlive()){

			Mob enemy = (Mob) defender;
			Hero hero = Dungeon.hero;


			if(randomf<=allyReviveChance()) {
				Corruption.corruptionHeal(enemy);
				AllyBuff.affectAndLoot(enemy, hero, Corruption.class);
			}
			else
			{
				Undead.heal(enemy);
				Buff.affect(enemy, Undead.class);
				System.out.println("respawn dead");
			}
			return true;
		}

		return false;
	}
	// Diese Methode sollte beim Tod eines Feindes aufgerufen werden
	/*public static void tryRevive(Mob mob) {
		if (mob != null && Math.random() < reviveChance()) {
			UndeadMinion undead = new UndeadMinion();
			undead.pos = mob.pos;
			undead.state = Char.State.HUNTING;
			// Füge den Untoten als Verbündeten hinzu
			mob.getLevel().mobs.add(undead);
		}
	}*/
}

