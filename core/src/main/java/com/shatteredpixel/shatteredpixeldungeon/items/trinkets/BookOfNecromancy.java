package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

public class BookOfNecromancy extends Trinket {

	{
		image = ItemSpriteSheet.BOOK_OF_NECROMANCY;

	}

	@Override
	protected int upgradeEnergyCost() {
		return 7 + 2 * level();
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

